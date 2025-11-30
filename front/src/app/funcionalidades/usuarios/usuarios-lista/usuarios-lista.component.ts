import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TagModule } from 'primeng/tag';
import {ConfirmationService, MessageService} from 'primeng/api';

import { UsuarioService } from '../../../nucleo/servicos/usuario.service';
import { UsuarioResponse } from '../../../compartilhado/models/usuario.model';
import { Perfil } from '../../../compartilhado/models/perfil.enum';
import {TooltipModule} from "primeng/tooltip";

@Component({
  selector: 'app-usuarios-lista',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    TableModule,
    ButtonModule,
    CardModule,
    ToastModule,
    ConfirmDialogModule,
    TagModule,
    TooltipModule
  ],
  providers: [ConfirmationService, MessageService],
  templateUrl: './usuarios-lista.component.html',
  styleUrl: './usuarios-lista.component.scss'
})
export class UsuariosListaComponent implements OnInit {

  private usuarioService = inject(UsuarioService);
  private router = inject(Router);
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

  novoUsuario() {
    this.router.navigate(['/usuarios/novo']);
  }

  editarUsuario(id: number) {
    this.router.navigate([`/usuarios/editar/${id}`]);
  }

  confirmarExclusao(usuario:UsuarioResponse) {
    this.confirmationService.confirm({
      message: `Tem certeza que deseja excluir este usuário <b>${usuario.nome}</b>?`,
      header: 'Confirmação',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim,excluir',
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
      error: () => {

      }
    });
  }
}
