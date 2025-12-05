import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CaminhaoService } from '../../../nucleo/servicos/caminhao.service';
import { CaminhaoResponse } from '../../../compartilhado/models/caminhao.model';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TagModule } from 'primeng/tag';
import { ConfirmationService, MessageService } from 'primeng/api';
import { InputTextareaModule } from 'primeng/inputtextarea';

@Component({
  selector: 'app-caminhao-lista',
  standalone: true,
  imports: [
    CommonModule, RouterLink, FormsModule,
    TableModule, ButtonModule, CardModule, TooltipModule,
    ConfirmDialogModule, TagModule, InputTextareaModule
  ],
  templateUrl: './caminhao-lista.component.html',
  styleUrl: './caminhao-lista.component.scss'
})
export class CaminhaoListaComponent implements OnInit {
  private caminhaoService = inject(CaminhaoService);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);

  caminhoes: CaminhaoResponse[] = [];
  isLoading = true;
  queryManual: string = '';

  atalhos = [
    { label: 'Placa', valor: 'placa=""' },
    { label: 'Motorista', valor: 'motorista=""' },
    { label: 'Capacidade', valor: 'capacidadeKg>=""' },
    { label: 'Ativos', valor: 'ativo=true' },
    { label: 'Inativos', valor: 'ativo=false' },
    { label: 'E (AND)', valor: ' AND ' },
    { label: 'OU (OR)', valor: ' OR ' }
  ];

  ngOnInit() {
    this.buscar();
  }

  adicionarAtalho(snippet: string) {
    this.queryManual += snippet;
  }

  buscar() {
    this.isLoading = true;
    const buscaFinal = this.queryManual.trim();

    console.log('Executando Query:', buscaFinal);

    this.caminhaoService.listar(buscaFinal).subscribe({
      next: (dados) => {
        this.caminhoes = dados;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Erro na execução da query:', err);
      }
    });
  }

  limparFiltros() {
    this.queryManual = '';
    this.buscar();
  }

  confirmarAlteracaoStatus(caminhao: CaminhaoResponse) {
    const estaAtivo = caminhao.ativo;
    this.confirmationService.confirm({
      message: estaAtivo
        ? `Deseja inativar o caminhão <b>${caminhao.placa}</b>?`
        : `Deseja reativar o caminhão <b>${caminhao.placa}</b>?`,
      header: estaAtivo ? 'Confirmar Inativação' : 'Confirmar Reativação',
      icon: estaAtivo ? 'pi pi-ban' : 'pi pi-check-circle',
      acceptLabel: estaAtivo ? 'Sim, inativar' : 'Sim, reativar',
      acceptButtonStyleClass: estaAtivo ? 'p-button-warning p-button-text' : 'p-button-success p-button-text',
      accept: () => this.alterarStatus(caminhao)
    });
  }

  private alterarStatus(caminhao: CaminhaoResponse) {
    this.caminhaoService.alterarStatus(caminhao.id).subscribe({
      next: () => this.buscar(),
      error: () => {}
    });
  }

  private excluir(id: number) {
    this.caminhaoService.excluir(id).subscribe({
      next: () => {
        this.caminhoes = this.caminhoes.filter(c => c.id !== id);
      },
      error: (err) => {
        if (err.status === 409) {
          this.messageService.add({
            severity: 'warn',
            summary: 'Não é possível excluir',
            detail: 'Registro em uso.',
            life: 5000
          });
        }
      }
    });
  }
}
