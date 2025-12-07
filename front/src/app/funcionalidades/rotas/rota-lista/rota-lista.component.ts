import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { ListboxModule } from 'primeng/listbox';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { CardModule } from 'primeng/card';
import { RotaService } from '../../../nucleo/servicos/rota.service';
import { RotaResponse } from '../../../compartilhado/models/rota.model';

@Component({
  selector: 'app-rota-lista',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    ButtonModule,
    TooltipModule,
    DialogModule,
    TagModule,
    ListboxModule,
    FormsModule,
    InputTextareaModule,
    CardModule
  ],
  templateUrl: './rota-lista.component.html',
  styles: [`
    :host ::ng-deep .p-dialog-content {
      padding: 0 1.5rem 1.5rem 1.5rem;
    }
  `]
})
export class RotaListaComponent implements OnInit {
  private rotaService = inject(RotaService);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);

  rotas = signal<RotaResponse[]>([]);
  loading = signal<boolean>(false);
  exibirDetalhes = signal<boolean>(false);
  rotaSelecionada = signal<RotaResponse | null>(null);
  queryManual: string = '';

  atalhos = [
    { label: 'Nome da Rota', valor: 'nome=""' },
    { label: 'Bairro Incluso', valor: 'listaDeBairros.nome=""' },
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
    this.loading.set(true);
    const query = this.queryManual.trim();
    
    this.rotaService.listar(query).subscribe({
      next: (dados) => {
        this.rotas.set(dados);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        console.error('Erro na busca de rotas:', err);
      }
    });
  }

  limparFiltros() {
    this.queryManual = '';
    this.buscar();
  }

  carregarRotas() {
    this.buscar();
  }

  verDetalhes(rota: RotaResponse) {
    this.rotaSelecionada.set(rota);
    this.exibirDetalhes.set(true);
  }

  confirmarExclusao(rota: RotaResponse) {
    this.confirmationService.confirm({
      message: `Tem certeza que deseja excluir a rota <b>${rota.nome}</b>?`,
      header: 'Confirmação de Exclusão',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim',
      rejectLabel: 'Não',
      acceptButtonStyleClass: 'p-button-danger p-button-text',
      rejectButtonStyleClass: 'p-button-text',
      accept: () => {
        this.excluir(rota.id);
      }
    });
  }

  private excluir(id: number) {
    this.loading.set(true);
    this.rotaService.excluir(id).subscribe({
      next: () => {
        this.buscar();
        this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Rota excluída.' });
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }
}