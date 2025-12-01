import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoteamentoService } from '../../../nucleo/servicos/roteamento.service';
import { BairroService } from '../../../nucleo/servicos/bairro.service';
import { PontoColetaService } from '../../../nucleo/servicos/ponto-coleta.service';
import { RotaService } from '../../../nucleo/servicos/rota.service';
import { MessageService } from 'primeng/api';
import { BairroResponse } from '../../../compartilhado/models/bairro.model';
import { PontoColetaResponse } from '../../../compartilhado/models/ponto-coleta.model';
import { ResultadoRota } from '../../../compartilhado/models/roteamento.model';
import { RotaRequest } from '../../../compartilhado/models/rota.model';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { PanelModule } from 'primeng/panel';
import { TimelineModule } from 'primeng/timeline';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { DividerModule } from 'primeng/divider';

@Component({
  selector: 'app-roteamento-calculo',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    DropdownModule, 
    ButtonModule, 
    CardModule, 
    PanelModule, 
    TimelineModule, 
    DialogModule, 
    InputTextModule,
    DividerModule
  ],
  templateUrl: './roteamento-calculo.component.html',
  styleUrl: './roteamento-calculo.component.scss'
})
export class RoteamentoCalculoComponent implements OnInit {
  
  private roteamentoService = inject(RoteamentoService);
  private bairroService = inject(BairroService);
  private pontoService = inject(PontoColetaService);
  private rotaService = inject(RotaService);
  private messageService = inject(MessageService);
  
  todosBairros: BairroResponse[] = [];
  bairrosDestinoFiltrados: BairroResponse[] = [];
  pontosOrigem: PontoColetaResponse[] = [];
  pontosDestino: PontoColetaResponse[] = [];
  origemBairroId: number | null = null;
  origemPontoId: number | null = null;
  destinoBairroId: number | null = null;
  destinoPontoId: number | null = null;
  isCalculating = false;
  resultado: ResultadoRota | null = null;
  showSalvarDialog = false;
  nomeRotaSalvar = '';

  ngOnInit() {
    this.carregarBairrosIniciais();
  }

  carregarBairrosIniciais() {
    this.bairroService.listar().subscribe({
      next: (dados) => {
        this.todosBairros = dados;
        this.bairrosDestinoFiltrados = [...dados];
      },
      error: () => {}
    });
  }

  aoSelecionarOrigem() {
    this.origemPontoId = null;
    this.pontosOrigem = [];
    
    if (this.origemBairroId === this.destinoBairroId) {
      this.destinoBairroId = null;
      this.destinoPontoId = null;
      this.pontosDestino = [];
    }

    if (this.origemBairroId) {
      this.carregarPontosPorBairro(this.origemBairroId, 'origem');
      this.bairrosDestinoFiltrados = this.todosBairros.filter(b => b.id !== this.origemBairroId);
    } else {
      this.bairrosDestinoFiltrados = [...this.todosBairros];
    }
  }

  aoSelecionarDestino() {
    this.destinoPontoId = null;
    this.pontosDestino = [];

    if (this.destinoBairroId) {
      this.carregarPontosPorBairro(this.destinoBairroId, 'destino');
    }
  }

  private carregarPontosPorBairro(bairroId: number, tipo: 'origem' | 'destino') {
    this.pontoService.buscarPorBairro(bairroId).subscribe({
      next: (pontos) => {
        if (tipo === 'origem') {
          this.pontosOrigem = pontos;
          if (pontos.length === 0) this.avisoSemPontos();
        } else {
          this.pontosDestino = pontos;
          if (pontos.length === 0) this.avisoSemPontos();
        }
      }
    });
  }

  avisoSemPontos() {
    this.messageService.add({
      severity: 'info', 
      summary: 'Aviso', 
      detail: 'O bairro selecionado não possui pontos de coleta cadastrados.'
    });
  }

  calcular() {
    if (!this.origemBairroId || !this.destinoBairroId || !this.origemPontoId || !this.destinoPontoId) {
      this.messageService.add({ 
        severity: 'warn', 
        summary: 'Campos incompletos', 
        detail: 'Por favor, selecione os bairros e os pontos de coleta específicos.' 
      });
      return;
    }

    this.isCalculating = true;
    this.resultado = null;

    this.roteamentoService.calcularRota(this.origemBairroId, this.destinoBairroId).subscribe({
      next: (res) => {
        this.resultado = res;
        this.isCalculating = false;
        
        if (res.listaOrdenadaDeBairros.length === 0) {
          this.messageService.add({ 
            severity: 'warn', 
            summary: 'Sem rota', 
            detail: 'Não foi possível encontrar um caminho entre estes bairros.' 
          });
        }
      },
      error: () => this.isCalculating = false
    });
  }

  abrirModalSalvar() {
    const nomeOrigem = this.todosBairros.find(b => b.id === this.origemBairroId)?.nome;
    const nomeDestino = this.todosBairros.find(b => b.id === this.destinoBairroId)?.nome;
    
    this.nomeRotaSalvar = `Rota ${nomeOrigem} -> ${nomeDestino}`;
    this.showSalvarDialog = true;
  }

  salvarRota() {
    if (!this.resultado) return;
    const idsBairros = this.resultado.listaOrdenadaDeBairros.map(b => b.id);

    const novaRota: RotaRequest = {
      nome: this.nomeRotaSalvar,
      listaDeBairrosIds: idsBairros
    };

    this.rotaService.salvar(novaRota).subscribe({
      next: () => {
        this.showSalvarDialog = false;
      },
      error: () => {
      }
    });
  }

  getCormarker(index: number, total: number): string {
    if (index === 0) return '#22C55E';
    if (index === total - 1) return '#EF4444';
    return '#64748B';
  }

  getIconeMarker(index: number, total: number): string {
    if (index === 0) return 'pi pi-play text-xs';
    if (index === total - 1) return 'pi pi-flag text-xs';
    return 'pi pi-circle-fill text-xs scale-50';
  }
}