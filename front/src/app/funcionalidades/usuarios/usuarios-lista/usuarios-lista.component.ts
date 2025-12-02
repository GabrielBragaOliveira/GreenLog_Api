import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from "primeng/tooltip";
import { ConfirmationService, MessageService } from 'primeng/api';
import { UsuarioService } from '../../../nucleo/servicos/usuario.service';
import { UsuarioResponse } from '../../../compartilhado/models/usuario.model';
import { Perfil } from '../../../compartilhado/models/perfil.enum';

@Component({
  selector: 'app-usuarios-lista',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    TableModule,
    ButtonModule,
    CardModule,
    TagModule,
    TooltipModule
  ],
  templateUrl: './usuarios-lista.component.html',
  styleUrl: './usuarios-lista.component.scss'
})
export class UsuariosListaComponent implements OnInit {

  private usuarioService = inject(UsuarioService);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);
  
  usuarios: UsuarioResponse[] = [];
  isLoading = true;
  PerfilEnum = Perfil;

  ngOnInit() {
    this.carregarUsuarios();
  }

  carregarUsuarios() {
    this.isLoading = true;
    this.usuarioService.listar().subscribe({
      next: (dados) => {
        this.usuarios = dados;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  confirmarAlteracaoStatus(usuario: UsuarioResponse) {
    const estaAtivo = usuario.ativo;

    this.confirmationService.confirm({
      message: estaAtivo 
        ? `Deseja inativar o usuário <b>${usuario.nome}</b>? <br><small>Ele perderá o acesso ao sistema.</small>`
        : `Deseja reativar o usuário <b>${usuario.nome}</b>? <br><small>Ele poderá fazer login novamente.</small>`,
      header: estaAtivo ? 'Confirmar Inativação' : 'Confirmar Reativação',
      icon: estaAtivo ? 'pi pi-ban' : 'pi pi-check-circle',
      acceptLabel: estaAtivo ? 'Sim, inativar' : 'Sim, reativar',
      acceptButtonStyleClass: estaAtivo ? 'p-button-warning p-button-text' : 'p-button-success p-button-text',
      accept: () => this.alterarStatus(usuario)
    });
  }

  private alterarStatus(usuario: UsuarioResponse) {
    this.usuarioService.alterarStatus(usuario.id).subscribe({
      next: () => this.carregarUsuarios()
    });
  }

  confirmarExclusao(usuario: UsuarioResponse) {
    this.confirmationService.confirm({
      message: `Tem certeza que deseja excluir permanentemente o usuário <b>${usuario.nome}</b>?`,
      header: 'Confirmar Exclusão',
      icon: 'pi pi-trash',
      acceptLabel: 'Sim, excluir',
      rejectLabel: 'Cancelar',
      acceptButtonStyleClass: 'p-button-danger p-button-text',
      accept: () => this.excluir(usuario.id)
    });
  }

  private excluir(id: number) {
    this.usuarioService.excluir(id).subscribe({
      next: () => {
        this.carregarUsuarios();
      },
      error: (err) => {
        if (err.status === 409) {
            this.messageService.add({
                severity: 'warn',
                summary: 'Não é possível excluir',
                detail: 'Este usuário possui registros vinculados.'
            });
        }
      }
    });
  }
}