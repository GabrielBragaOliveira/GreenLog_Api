import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ConexaoService } from '../../../nucleo/servicos/conexao.service';
import { ConexaoBairroResponse } from '../../../compartilhado/models/conexao-bairro.model';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-conexoes-lista',
  standalone: true,
  imports: [CommonModule, RouterLink, TableModule, ButtonModule, CardModule, TooltipModule],
  templateUrl: './conexoes-lista.component.html',
  styleUrl: './conexoes-lista.component.scss'
})
export class ConexoesListaComponent implements OnInit {
  private conexaoService = inject(ConexaoService);
  private confirmationService = inject(ConfirmationService);

  conexoes: ConexaoBairroResponse[] = [];
  isLoading = true;

  ngOnInit() {
    this.carregarDados();
  }

  carregarDados() {
    this.isLoading = true;
    this.conexaoService.listar().subscribe({
      next: (dados) => { this.conexoes = dados; this.isLoading = false; },
      error: () => this.isLoading = false
    });
  }

  confirmarExclusao(conexao: ConexaoBairroResponse) {
    this.confirmationService.confirm({
      message: `Excluir conexão de <b>${conexao.bairroOrigem.nome}</b> para <b>${conexao.bairroDestino.nome}</b>?`,
      header: 'Confirmar Exclusão',
      icon: 'pi pi-trash',
      acceptLabel: 'Sim, excluir',
      rejectLabel: 'Cancelar',
      acceptButtonStyleClass: 'p-button-danger p-button-text',
      accept: () => this.excluir(conexao.id)
    });
  }

  private excluir(id: number) {
    this.conexaoService.excluir(id).subscribe(() => {
      this.conexoes = this.conexoes.filter(c => c.id !== id);
    });
  }
}