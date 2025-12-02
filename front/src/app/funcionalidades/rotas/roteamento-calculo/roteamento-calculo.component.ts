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
  
  // Constante para definir o nome do bairro fixo
  readonly NOME_BAIRRO_ORIGEM = 'Centro';

  todosBairros: BairroResponse[] = [];
  bairrosDestinoFiltrados: BairroResponse[] = [];
  
  // Origem Fixa
  origemBairroId: number | null = null;
  
  // Destino Dinâmico
  pontosDestino: PontoColetaResponse[] = [];
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
        this.definirOrigemFixa();
      },
      error: () => {}
    });
  }

  private definirOrigemFixa() {
    const bairroCentro = this.todosBairros.find(b => b.nome === this.NOME_BAIRRO_ORIGEM);
    
    if (bairroCentro) {
      this.origemBairroId = bairroCentro.id;
      // Remove o Centro da lista de destinos possíveis
      this.bairrosDestinoFiltrados = this.todosBairros.filter(b => b.id !== this.origemBairroId);
    } else {
      this.messageService.add({
        severity: 'error',
        summary: 'Configuração',
        detail: `Bairro de origem '${this.NOME_BAIRRO_ORIGEM}' não encontrado no cadastro.`
      });
      this.bairrosDestinoFiltrados = [...this.todosBairros];
    }
  }

  aoSelecionarDestino() {
    this.destinoPontoId = null;
    this.pontosDestino = [];

    if (this.destinoBairroId) {
      this.carregarPontosDestino(this.destinoBairroId);
    }
  }

  private carregarPontosDestino(bairroId: number) {
    this.pontoService.buscarPorBairro(bairroId).subscribe({
      next: (pontos) => {
        this.pontosDestino = pontos;
        if (pontos.length === 0) {
          this.messageService.add({
            severity: 'info', 
            summary: 'Aviso', 
            detail: 'O bairro de destino não possui pontos de coleta cadastrados.'
          });
        }
      }
    });
  }

  calcular() {
    if (!this.origemBairroId || !this.destinoBairroId || !this.destinoPontoId) {
      this.messageService.add({ 
        severity: 'warn', 
        summary: 'Campos incompletos', 
        detail: 'Por favor, selecione o bairro e o ponto de coleta de destino.' 
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
    const nomeBairro = this.todosBairros.find(b => b.id === this.destinoBairroId)?.nome;
    const pontoSelecionado = this.pontosDestino.find(p => p.id === this.destinoPontoId);
    
    // Formatação solicitada: "Bairro (Ponto)"
    if (nomeBairro && pontoSelecionado) {
      this.nomeRotaSalvar = `${nomeBairro} (${pontoSelecionado.nomePonto})`;
    } else {
      this.nomeRotaSalvar = '';
    }
    
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
      error: () => {} 
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