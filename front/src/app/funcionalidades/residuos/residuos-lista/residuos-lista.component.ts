import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';
import { TagModule } from 'primeng/tag';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TipoResiduoService } from '../../../nucleo/servicos/tipo-residuo.service';
import { TipoResiduoResponse } from '../../../compartilhado/models/tipo-residuo.model';

@Component({
  selector: 'app-residuos-lista',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    ButtonModule,
    CardModule,
    TooltipModule,
    TagModule,
    RouterLink
  ],
  templateUrl: './residuos-lista.component.html',
  styleUrl: './residuos-lista.component.scss'
})
export class ResiduosListaComponent implements OnInit {

  private tipoResiduoService = inject(TipoResiduoService);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);

  residuos: TipoResiduoResponse[] = [];
  isLoading = true;

  ngOnInit(): void {
    this.carregarResiduos();
  }

  carregarResiduos() {
    this.isLoading = true;
    this.tipoResiduoService.listar().subscribe({
      next: (dados) => {
        this.residuos = dados;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  confirmarAlteracaoStatus(residuo: TipoResiduoResponse) {
    const estaAtivo = residuo.ativo;

    this.confirmationService.confirm({
      message: estaAtivo
        ? `Deseja inativar o tipo de resíduo <b>${residuo.nome}</b>? <br><small>Ele não poderá ser selecionado em novos cadastros.</small>`
        : `Deseja reativar o tipo de resíduo <b>${residuo.nome}</b>?`,
      header: estaAtivo ? 'Confirmar Inativação' : 'Confirmar Reativação',
      icon: estaAtivo ? 'pi pi-ban' : 'pi pi-check-circle',
      acceptLabel: estaAtivo ? 'Sim, inativar' : 'Sim, reativar',
      acceptButtonStyleClass: estaAtivo ? 'p-button-warning p-button-text' : 'p-button-success p-button-text',
      accept: () => this.alterarStatus(residuo)
    });
  }

  private alterarStatus(residuo: TipoResiduoResponse) {
    this.tipoResiduoService.alterarStatus(residuo.id).subscribe({
      next: () => this.carregarResiduos()
    });
  }

  confirmarExclusao(residuo: TipoResiduoResponse) {
    this.confirmationService.confirm({
      message: `Tem certeza que deseja excluir permanentemente o tipo <b>${residuo.nome}</b>?`,
      header: 'Confirmar Exclusão',
      icon: 'pi pi-trash',
      acceptLabel: 'Sim, excluir',
      rejectLabel: 'Cancelar',
      acceptButtonStyleClass: 'p-button-danger p-button-text',
      accept: () => this.excluir(residuo.id)
    });
  }

  private excluir(id: number) {
    this.tipoResiduoService.excluir(id).subscribe({
      next: () => {
        this.residuos = this.residuos.filter(r => r.id !== id);
      },
      error: (err) => {
        if (err.status === 409) {
          this.messageService.add({
            severity: 'warn',
            summary: 'Não é possível excluir',
            detail: 'Este tipo de resíduo está em uso e não pode ser removido.',
            life: 5000
          });
        }
      }
    });
  }
}
