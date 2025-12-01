import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PontoColetaService } from '../../../nucleo/servicos/ponto-coleta.service';
import { PontoColetaResponse } from '../../../compartilhado/models/ponto-coleta.model';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';
import { TagModule } from 'primeng/tag';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-pontos-lista',
  standalone: true,
  imports: [CommonModule, RouterLink, TableModule, ButtonModule, CardModule, TooltipModule, TagModule],
  templateUrl: './pontos-lista.component.html',
  styleUrl: './pontos-lista.component.scss'
})
export class PontosListaComponent implements OnInit {
  private pontoService = inject(PontoColetaService);
  private confirmationService = inject(ConfirmationService);

  pontos: PontoColetaResponse[] = [];
  isLoading = true;

  ngOnInit() {
    this.carregarDados();
  }

  carregarDados() {
    this.isLoading = true;
    this.pontoService.listar().subscribe({
      next: (dados) => {
        this.pontos = dados;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  confirmarExclusao(ponto: PontoColetaResponse) {
    this.confirmationService.confirm({
      message: `Deseja realmente excluir o ponto de coleta <b>${ponto.nomePonto}</b>?`,
      header: 'Confirmar Exclusão',
      icon: 'pi pi-trash',
      acceptLabel: 'Sim, excluir',
      rejectLabel: 'Cancelar',
      acceptButtonStyleClass: 'p-button-danger p-button-text',
      accept: () => this.excluir(ponto.id)
    });
  }

  private excluir(id: number | undefined) {
    if (!id) return;
    this.pontoService.excluir(id).subscribe(() => {
      this.pontos = this.pontos.filter(p => p.id !== id);
    });
  }
}