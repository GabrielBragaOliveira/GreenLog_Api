import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from "primeng/tooltip";
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ConfirmationService, MessageService } from 'primeng/api';
import { UsuarioService } from '../../../nucleo/servicos/usuario.service';
import { UsuarioResponse } from '../../../compartilhado/models/usuario.model';
import { Perfil } from '../../../compartilhado/models/perfil.enum';

@Component({
  selector: 'app-usuarios-lista',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule,
    TableModule,
    ButtonModule,
    CardModule,
    TagModule,
    TooltipModule,
    InputTextareaModule
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
  queryManual: string = '';

  atalhos = [
    { label: 'Nome', valor: 'nome=""' },
    { label: 'E-mail', valor: 'email=""' },
    { label: 'Perfil', valor: 'perfil=""' },
    { label: 'Ativo', valor: 'ativo=true' },
    { label: 'Inativo', valor: 'ativo=false' },
    { label: 'E (AND)', valor: ' AND ' },
    { label: 'OU (OR)', valor: ' OR ' }
  ];

  ngOnInit() {
    this.carregarUsuarios();
  }

  adicionarAtalho(snippet: string) {
    this.queryManual += snippet;
  }

  buscar() {
    this.isLoading = true;
    const query = this.queryManual.trim();
    this.usuarioService.listar(query).subscribe({
      next: (dados) => {
        this.usuarios = dados;
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
      header: estaAtivo ? 'Inativar' : 'Reativar',
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
}