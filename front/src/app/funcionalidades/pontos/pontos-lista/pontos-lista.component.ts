import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';
import { TagModule } from 'primeng/tag';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ConfirmationService } from 'primeng/api';
import { PontoColetaService } from '../../../nucleo/servicos/ponto-coleta.service';
import { PontoColetaResponse } from '../../../compartilhado/models/ponto-coleta.model';

@Component({
  selector: 'app-pontos-lista',
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
  templateUrl: './pontos-lista.component.html',
  styleUrl: './pontos-lista.component.scss'
})
export class PontosListaComponent implements OnInit {
  private pontoService = inject(PontoColetaService);
  private confirmationService = inject(ConfirmationService);

  pontos: PontoColetaResponse[] = [];
  isLoading = true;
  queryManual: string = '';

  atalhos = [
    { label: 'Nome Do Ponto', valor: 'nomePonto=""' },
    { label: 'Responsável', valor: 'nomeResponsavel=""' },
    { label: 'Contato', valor: 'contato=""' },
    { label: 'Email', valor: 'email=""' },
    { label: 'Bairro', valor: 'bairro.nome=""' },
    { label: 'Resíduos', valor: 'tiposResiduosAceitos.nome=""' },
    { label: 'Ativo', valor: 'ativo=true' },
    { label: 'Inativo', valor: 'ativo=false' },
    { label: 'E (AND)', valor: ' AND ' },
    { label: 'OU (OR)', valor: ' OR ' }
  ];

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

  adicionarAtalho(snippet: string) {
    this.queryManual += snippet;
  }

  buscar() {
    this.isLoading = true;
    const query = this.queryManual.trim();
    this.pontoService.listar(query).subscribe({
      next: (dados) => {
        this.pontos = dados;
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

  confirmarAlteracaoStatus(ponto: PontoColetaResponse) {
    const estaAtivo = ponto.ativo;

    this.confirmationService.confirm({
      message: estaAtivo
        ? `Deseja inativar o ponto <b>${ponto.nomePonto}</b>? <br><small>Ele não receberá novos agendamentos.</small>`
        : `Deseja reativar o ponto <b>${ponto.nomePonto}</b>?`,
      header: estaAtivo ? 'Inativar' : 'Reativar',
      icon: estaAtivo ? 'pi pi-ban' : 'pi pi-check-circle',
      acceptLabel: estaAtivo ? 'Sim, inativar' : 'Sim, reativar',
      acceptButtonStyleClass: estaAtivo ? 'p-button-warning p-button-text' : 'p-button-success p-button-text',
      accept: () => this.alterarStatus(ponto)
    });
  }

  private alterarStatus(ponto: PontoColetaResponse) {
    this.pontoService.alterarStatus(ponto.id).subscribe({
      next: () => this.carregarDados()
    });
  }
}