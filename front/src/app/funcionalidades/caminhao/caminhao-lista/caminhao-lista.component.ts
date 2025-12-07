import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { TagModule } from 'primeng/tag';
import { ConfirmationService, MessageService } from 'primeng/api';
import { CaminhaoService } from '../../../nucleo/servicos/caminhao.service';
import { CaminhaoResponse } from '../../../compartilhado/models/caminhao.model';

@Component({
  selector: 'app-caminhao-lista',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule,
    TableModule,
    ButtonModule,
    CardModule,
    TooltipModule,
    InputTextareaModule,
    TagModule
  ],
  templateUrl: './caminhao-lista.component.html',
  styleUrl: './caminhao-lista.component.scss'
})
export class CaminhaoListaComponent implements OnInit {
  private caminhaoService = inject(CaminhaoService);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);

  caminhoes: CaminhaoResponse[] = [];
  isLoading = true;
  queryManual: string = '';

  atalhos = [
    { label: 'Placa', valor: 'placa=""' },
    { label: 'Motorista', valor: 'motorista=""' },
    { label: 'Capacidade', valor: 'capacidadeKg>=""' },
    { label: 'Ativos', valor: 'ativo=true' },
    { label: 'Inativos', valor: 'ativo=false' },
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
    this.isLoading = true;
    const buscaFinal = this.queryManual.trim();

    this.caminhaoService.listar(buscaFinal).subscribe({
      next: (dados) => {
        this.caminhoes = dados;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Erro na execução da query:', err);
      }
    });
  }

  limparFiltros() {
    this.queryManual = '';
    this.buscar();
  }

  confirmarAlteracaoStatus(caminhao: CaminhaoResponse) {
    const estaAtivo = caminhao.ativo;
    this.confirmationService.confirm({
      message: estaAtivo
        ? `Deseja inativar o caminhão <b>${caminhao.placa}</b>?`
        : `Deseja reativar o caminhão <b>${caminhao.placa}</b>?`,
      header: estaAtivo ? 'Inativar' : 'Reativar',
      icon: estaAtivo ? 'pi pi-ban' : 'pi pi-check-circle',
      acceptLabel: estaAtivo ? 'Sim, inativar' : 'Sim, reativar',
      acceptButtonStyleClass: estaAtivo ? 'p-button-warning p-button-text' : 'p-button-success p-button-text',
      accept: () => this.alterarStatus(caminhao)
    });
  }

  private alterarStatus(caminhao: CaminhaoResponse) {
    this.caminhaoService.alterarStatus(caminhao.id).subscribe({
      next: () => this.buscar(),
      error: () => {}
    });
  }

  confirmarExclusao(caminhao: CaminhaoResponse) {
     this.confirmationService.confirm({
        message: `Tem certeza que deseja excluir o caminhão <b>${caminhao.placa}</b>?`,
        header: 'Confirmar Exclusão',
        icon: 'pi pi-trash',
        acceptButtonStyleClass: 'p-button-danger p-button-text',
        accept: () => this.excluir(caminhao.id)
     });
  }

  private excluir(id: number) {
    this.caminhaoService.excluir(id).subscribe({
      next: () => {
        this.caminhoes = this.caminhoes.filter(c => c.id !== id);
      },
      error: (err) => {
        if (err.status === 409) {
          this.messageService.add({
            severity: 'warn',
            summary: 'Ação Bloqueada',
            detail: 'Não é possível excluir pois o registro está em uso.',
            life: 5000
          });
        }
      }
    });
  }
}