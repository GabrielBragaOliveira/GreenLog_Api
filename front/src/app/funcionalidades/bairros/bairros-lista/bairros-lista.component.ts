import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

import { BairroService } from '../../../nucleo/servicos/bairro.service';
import { BairroResponse } from '../../../compartilhado/models/bairro.model';

@Component({
  selector: 'app-bairros-lista',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    ButtonModule,
    RouterModule,
    CardModule,
    TooltipModule,
    ToastModule,
    ConfirmDialogModule
  ],
  providers: [ConfirmationService, MessageService],
  templateUrl: './bairros-lista.component.html',
  styleUrl: './bairros-lista.component.scss'
})
export class BairrosListaComponent implements OnInit {

  private bairroService = inject(BairroService);
  private router = inject(Router);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);
  bairros: BairroResponse[] = [];
  isLoading = true;

  ngOnInit(): void {
    this.carregarBairros();
  }

  carregarBairros() {
    this.isLoading = true;
    this.bairroService.listar().subscribe({
      next: (dados) => {
        this.bairros = dados;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  novoBairro() {
    this.router.navigate(['/bairros/novo']);
  }

  editarBairro(id: number) {
    this.router.navigate([`/bairros/editar/${id}`]);
  }

  confirmarExclusao(bairro: BairroResponse) {
    this.confirmationService.confirm({
      message: `Tem certeza que deseja excluir o bairro <b>${bairro.nome}</b>?`,
      header: 'Confirmar Exclusão',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim, excluir',
      rejectLabel: 'Cancelar',
      acceptButtonStyleClass: 'p-button-danger p-button-text',
      accept: () => this.excluir(bairro.id)
    });
  }

  private excluir(id: number) {
    this.bairroService.excluir(id).subscribe({
      next: () => {
        this.bairros = this.bairros.filter(b => b.id !== id);
      },
      error: (err) => {
        if (err.status === 409) {
          this.messageService.add({
            severity: 'warn',
            summary: 'Não é possível excluir',
            detail: 'Este bairro possui registros vinculados e não pode ser removido.',
            life: 5000
          });
        }
      }
    });
  }
}