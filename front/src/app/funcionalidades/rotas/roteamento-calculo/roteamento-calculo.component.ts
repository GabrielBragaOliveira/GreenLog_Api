import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RoteamentoService } from '../../../nucleo/servicos/roteamento.service';
import { BairroService } from '../../../nucleo/servicos/bairro.service';
import { RotaService } from '../../../nucleo/servicos/rota.service';
import { MessageService } from 'primeng/api';
import { BairroResponse } from '../../../compartilhado/models/bairro.model';
import { ResultadoRota } from '../../../compartilhado/models/roteamento.model';
import { RotaRequest } from '../../../compartilhado/models/rota.model';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { PanelModule } from 'primeng/panel';
import { TimelineModule } from 'primeng/timeline';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';

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
    InputTextModule
  ],
  templateUrl: './roteamento-calculo.component.html',
  styleUrl: './roteamento-calculo.component.scss'
})
export class RoteamentoCalculoComponent implements OnInit {
  
  private roteamentoService = inject(RoteamentoService);
  private bairroService = inject(BairroService);
  private rotaService = inject(RotaService);
  private messageService = inject(MessageService);
  private router = inject(Router);

  bairros: BairroResponse[] = [];
  origemId: number | null = null;
  destinoId: number | null = null;
  
  isCalculating = false;
  resultado: ResultadoRota | null = null;

  showSalvarDialog = false;
  nomeRotaSalvar = '';

  ngOnInit() {
    this.carregarBairros();
  }

  carregarBairros() {
    this.bairroService.listar().subscribe(dados => this.bairros = dados);
  }

  calcular() {
    if (!this.origemId || !this.destinoId) return;

    this.isCalculating = true;
    this.resultado = null;

    this.roteamentoService.calcularRota(this.origemId, this.destinoId).subscribe({
      next: (res) => {
        this.resultado = res;
        this.isCalculating = false;
        
        if (res.listaOrdenadaDeBairros.length === 0) {
          this.messageService.add({ severity: 'warn', summary: 'Atenção', detail: 'Nenhuma rota encontrada.' });
        }
      },
      error: () => this.isCalculating = false
    });
  }

  abrirModalSalvar() {
    const nomeOrigem = this.bairros.find(b => b.id === this.origemId)?.nome;
    const nomeDestino = this.bairros.find(b => b.id === this.destinoId)?.nome;
    this.nomeRotaSalvar = `Rota ${nomeOrigem} para ${nomeDestino}`;
    this.showSalvarDialog = true;
  }

  salvarRota() {
    if (!this.resultado) return;
    
    const idsBairros: number[] = [];
    
    for (const nomeBairro of this.resultado.listaOrdenadaDeBairros) {
      const bairroEncontrado = this.bairros.find(b => b.nome === nomeBairro);
      if (bairroEncontrado) {
        idsBairros.push(bairroEncontrado.id);
      }
    }

    const novaRota: RotaRequest = {
      nome: this.nomeRotaSalvar,
      listaDeBairrosIds: idsBairros
    };

    this.rotaService.salvar(novaRota).subscribe({
      next: () => {
        this.showSalvarDialog = false;
        this.router.navigate(['/rotas']); 
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