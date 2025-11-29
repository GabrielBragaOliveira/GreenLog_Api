import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

// PrimeNG Imports
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmDialogModule } from 'primeng/confirmdialog'; // Necessário para o popup
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
    ConfirmDialogModule // Adicionado
  ],
  providers: [ConfirmationService, MessageService], // Provedores locais
  templateUrl: './bairros-lista.component.html',
  styleUrl: './bairros-lista.component.scss'
})
export class BairrosListaComponent implements OnInit {

  // INJEÇÃO DE DEPENDÊNCIA MODERNA
  private bairroService = inject(BairroService);
  private router = inject(Router);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService); // Usado apenas para erros específicos (ex: 409)

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
      // O erro genérico já é tratado no ApiBaseService, não precisa por aqui
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
        // O ApiBaseService já mandou o Toast de Sucesso.
        // Só precisamos atualizar a lista na tela.
        this.bairros = this.bairros.filter(b => b.id !== id);
      },
      error: (err) => {
        // Tratamento específico: Se o bairro estiver em uso (Erro 409 Conflict)
        if (err.status === 409) {
          this.messageService.add({
            severity: 'warn',
            summary: 'Não é possível excluir',
            detail: 'Este bairro possui registros vinculados e não pode ser removido.',
            life: 5000
          });
        }
        // Outros erros já são tratados pelo ApiBaseService
      }
    });
  }
}
