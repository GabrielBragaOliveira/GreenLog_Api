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
import { FormsModule } from '@angular/forms';
import { InputTextareaModule } from 'primeng/inputtextarea';

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
    RouterLink,
    FormsModule,
    InputTextareaModule
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
  queryManual: string = '';

  atalhos = [
    { label: 'Nome', valor: 'nome=""' },
    { label: 'Ativo', valor: 'ativo=true' },
    { label: 'Inativo', valor: 'ativo=false' },
    { label: 'E (AND)', valor: ' AND ' },
    { label: 'OU (OR)', valor: ' OR ' }
  ];

  ngOnInit(): void {
    this.carregarResiduos();
  }

   adicionarAtalho(snippet: string) {
    this.queryManual += snippet;
  }

  buscar() {
    this.isLoading = true;
    const query = this.queryManual.trim();
    this.tipoResiduoService.listar(query).subscribe({
      next: (dados) => {
        this.residuos = dados;
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
