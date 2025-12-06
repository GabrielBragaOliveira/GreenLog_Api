import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';
import { TagModule } from 'primeng/tag';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ConfirmationService } from 'primeng/api';

import { BairroService } from '../../../nucleo/servicos/bairro.service';
import { BairroResponse } from '../../../compartilhado/models/bairro.model';

@Component({
  selector: 'app-bairros-lista',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule,
    TableModule,
    ButtonModule,
    CardModule,
    TooltipModule,
    TagModule,
    InputTextareaModule
  ],
  templateUrl: './bairros-lista.component.html',
  styleUrl: './bairros-lista.component.scss'
})
export class BairrosListaComponent implements OnInit {

  private bairroService = inject(BairroService);
  private confirmationService = inject(ConfirmationService);

  bairros: BairroResponse[] = [];
  isLoading = true;
  queryManual: string = '';

  atalhos = [
    { label: 'Nome', valor: 'nome=""' },
    { label: 'Descrição', valor: 'descricao=""' },
    { label: 'Ativo', valor: 'ativo=true' },
    { label: 'Inativo', valor: 'ativo=false' },
    { label: 'E (AND)', valor: ' AND ' },
    { label: 'OU (OR)', valor: ' OR ' }
  ];

  ngOnInit(): void {
    this.buscar();
  }

  adicionarAtalho(snippet: string) {
    this.queryManual += snippet;
  }

  buscar() {
    this.isLoading = true;
    const query = this.queryManual.trim();
    this.bairroService.listar(query).subscribe({
      next: (dados) => {
        this.bairros = dados;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Erro na busca:', err);
      }
    });
  }

  limparFiltros() {
    this.queryManual = '';
    this.buscar();
  }

  confirmarAlteracaoStatus(bairro: BairroResponse) {
    const estaAtivo = bairro.ativo;

    this.confirmationService.confirm({
      message: estaAtivo
        ? `Deseja inativar o bairro <b>${bairro.nome}</b>?`
        : `Deseja reativar o bairro <b>${bairro.nome}</b>?`,
      header: estaAtivo ? 'Inativar' : 'Reativar',
      icon: estaAtivo ? 'pi pi-ban' : 'pi pi-check-circle',
      acceptLabel: estaAtivo ? 'Sim, inativar' : 'Sim, reativar',
      acceptButtonStyleClass: estaAtivo ? 'p-button-warning p-button-text' : 'p-button-success p-button-text',
      accept: () => this.alterarStatus(bairro)
    });
  }

  private alterarStatus(bairro: BairroResponse) {
    this.bairroService.alterarStatus(bairro.id).subscribe({
      next: () => this.buscar()
    });
  }
}