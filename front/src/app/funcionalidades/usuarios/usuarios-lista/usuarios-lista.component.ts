import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TagModule } from 'primeng/tag';
import { ConfirmationService } from 'primeng/api';

import { UsuarioService } from '../../../nucleo/servicos/usuario.service';
import { UsuarioResponse } from '../../../compartilhado/models/usuario.model';
import { Perfil } from '../../../compartilhado/models/perfil.enum';

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
    TagModule
  ],
  providers: [ConfirmationService],
  templateUrl: './usuarios-lista.component.html',
  styleUrl: './usuarios-lista.component.scss'
})
export class UsuariosListaComponent implements OnInit {

  private usuarioService = inject(UsuarioService);
  private router = inject(Router);
  private confirmationService = inject(ConfirmationService);

  usuarios: UsuarioResponse[] = [];
  loading = true;


  PerfilEnum = Perfil;

  ngOnInit() {
    this.buscarUsuarios();
  }

  buscarUsuarios() {
    this.loading = true;
    this.usuarioService.listar().subscribe({
      next: (dados) => {
        this.usuarios = dados;
        this.loading = false;
      },
      error: () => {

        this.loading = false;
      }
    });
  }

  novoUsuario() {
    this.router.navigate(['/usuarios/novo']);
  }

  editarUsuario(id: number) {
    this.router.navigate(['/usuarios/editar', id]);
  }

  excluirUsuario(event: Event, id: number) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Tem certeza que deseja excluir este usuário?',
      header: 'Confirmação',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim',
      rejectLabel: 'Não',
      acceptButtonStyleClass: 'p-button-danger p-button-text',
      rejectButtonStyleClass: 'p-button-text',
      accept: () => {
        this.confirmarExclusao(id);
      }
    });
  }

  private confirmarExclusao(id: number) {
    this.usuarioService.excluir(id).subscribe({
      next: () => {

        this.buscarUsuarios();
      },
      error: () => {

      }
    });
  }
}
