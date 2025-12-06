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
import { ConexaoService } from '../../../nucleo/servicos/conexao.service';
import { ConexaoBairroResponse } from '../../../compartilhado/models/conexao-bairro.model';

@Component({
  selector: 'app-conexoes-lista',
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
  templateUrl: './conexoes-lista.component.html',
  styleUrl: './conexoes-lista.component.scss'
})
export class ConexoesListaComponent implements OnInit {
  private conexaoService = inject(ConexaoService);
  private confirmationService = inject(ConfirmationService);

  conexoes: ConexaoBairroResponse[] = [];
  isLoading = true;
  queryManual: string = '';

  atalhos = [
    { label: 'Origem', valor: 'bairroOrigem.nome=""' },
    { label: 'Destino', valor: 'bairroDestino.nome=""' },
    { label: 'Distância (km)', valor: 'distancia=""' },
    { label: 'Ativo', valor: 'ativo=true' },
    { label: 'Inativo', valor: 'ativo=false' },
    { label: 'E (AND)', valor: ' AND ' },
    { label: 'OU (OR)', valor: ' OR ' }
  ];

  ngOnInit() {
    this.carregarDados();
  }

  adicionarAtalho(snippet: string) {
    this.queryManual += snippet;
  }

  buscar() {
    this.isLoading = true;
    const query = this.queryManual.trim();
    this.conexaoService.listar(query).subscribe({
      next: (dados) => {
        this.conexoes = dados;
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

  carregarDados() {
    this.isLoading = true;
    this.conexaoService.listar().subscribe({
      next: (dados) => { 
        this.conexoes = dados; 
        this.isLoading = false; 
      },
      error: () => this.isLoading = false
    });
  }

  confirmarAlteracaoStatus(conexao: ConexaoBairroResponse) {
    const estaAtivo = conexao.ativo;

    this.confirmationService.confirm({
      message: estaAtivo
        ? `Deseja inativar a conexão entre <b>${conexao.bairroOrigem.nome}</b> e <b>${conexao.bairroDestino.nome}</b>?`
        : `Deseja reativar a conexão entre <b>${conexao.bairroOrigem.nome}</b> e <b>${conexao.bairroDestino.nome}</b>?`,
      header: estaAtivo ? 'Inativar' : 'Reativar',
      icon: estaAtivo ? 'pi pi-ban' : 'pi pi-check-circle',
      acceptLabel: estaAtivo ? 'Sim, inativar' : 'Sim, reativar',
      acceptButtonStyleClass: estaAtivo ? 'p-button-warning p-button-text' : 'p-button-success p-button-text',
      accept: () => this.alterarStatus(conexao)
    });
  }

  private alterarStatus(conexao: ConexaoBairroResponse) {
      this.conexaoService.alterarStatus(conexao.id).subscribe({
          next: () => this.carregarDados()
      });
  }
}