import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
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

@Component({
  selector: 'app-caminhao-lista',
  standalone: true,
  imports: [CommonModule, RouterLink, TableModule, ButtonModule, CardModule, TooltipModule, ConfirmDialogModule, TagModule],
  templateUrl: './caminhao-lista.component.html',
  styleUrl: './caminhao-lista.component.scss'
})
export class CaminhaoListaComponent implements OnInit {
  private caminhaoService = inject(CaminhaoService);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);

  caminhoes: CaminhaoResponse[] = [];
  isLoading = true;

  ngOnInit() {
    this.carregarDados();
  }

  carregarDados() {
    this.isLoading = true;
    this.caminhaoService.listar().subscribe({
      next: (dados) => {
        this.caminhoes = dados;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  confirmarExclusao(caminhao: CaminhaoResponse) {
    this.confirmationService.confirm({
      message: `Tem certeza que deseja excluir o caminhão de placa <b>${caminhao.placa}</b>?`,
      header: 'Confirmar Exclusão',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim, excluir',
      rejectLabel: 'Cancelar',
      acceptButtonStyleClass: 'p-button-danger p-button-text',
      accept: () => this.excluir(caminhao.id)
    });
  }

  confirmarAlteracaoStatus(caminhao: CaminhaoResponse) {
    const estaAtivo = caminhao.ativo;

    this.confirmationService.confirm({
      message: estaAtivo
        ? `Deseja inativar o caminhão <b>${caminhao.placa}</b>? <br><small>Ele não aparecerá mais em novas rotas.</small>`
        : `Deseja reativar o caminhão <b>${caminhao.placa}</b>? <br><small>Ele voltará a ficar disponível para uso.</small>`,
      header: estaAtivo ? 'Confirmar Inativação' : 'Confirmar Reativação',
      icon: estaAtivo ? 'pi pi-ban' : 'pi pi-check-circle',
      acceptLabel: estaAtivo ? 'Sim, inativar' : 'Sim, reativar',
      acceptButtonStyleClass: estaAtivo ? 'p-button-warning p-button-text' : 'p-button-success p-button-text',
      accept: () => this.alterarStatus(caminhao)
    });
  }

  private alterarStatus(caminhao: CaminhaoResponse) {
    this.caminhaoService.alterarStatus(caminhao.id).subscribe({
      next: () => {
        this.carregarDados();
      },
      error: () => {
      }
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
            detail: 'Este caminhão possui itinerários ou rotas associadas e não pode ser removido.',
            life: 5000
          });
        }
      }
    });
  }
}