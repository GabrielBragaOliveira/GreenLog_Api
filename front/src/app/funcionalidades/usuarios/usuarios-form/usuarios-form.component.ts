import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';


import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { PasswordModule } from 'primeng/password';
import { DropdownModule } from 'primeng/dropdown';


import { UsuarioService } from '../../../nucleo/servicos/usuario.service';
import { UsuarioRequest } from '../../../compartilhado/models/usuario.model';
import { Perfil } from '../../../compartilhado/models/perfil.enum';

@Component({
  selector: 'app-usuarios-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputTextModule,
    PasswordModule,
    DropdownModule,
    ButtonModule,
    RouterModule,
    CardModule,
    ToastModule
  ],
  templateUrl: './usuarios-form.component.html',
  styleUrl: './usuarios-form.component.scss'
})
export class UsuariosFormComponent implements OnInit {

  private fb = inject(FormBuilder);
  private usuarioService = inject(UsuarioService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  opcoesPerfil = [
    { label: 'Administrador', value: Perfil.ADMIN },
    { label: 'Usuário Padrão', value: Perfil.USER }
  ];

  form = this.fb.group({
    nome: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
    perfil: [null as Perfil | null, [Validators.required]],
    senha: ['']
  });

  isEdicao = false;
  idEdicao: number | null = null;
  isSaving = false;

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.isEdicao = true;
      this.idEdicao = +id;
      this.form.controls['senha'].removeValidators(Validators.required);
      this.carregarUsuario(this.idEdicao);
    } else {
      this.form.controls['senha'].addValidators([Validators.required, Validators.minLength(6)]);
    }
    this.form.controls['senha'].updateValueAndValidity();
  }

  carregarUsuario(id: number) {
    this.usuarioService.buscarPorId(id).subscribe({
      next: (usuario) => {
        this.form.patchValue({
          nome: usuario.nome,
          email: usuario.email,
          perfil: usuario.perfil
        });

        this.form.markAsPristine();
      },
      error: () => this.cancelar()
    });
  }

  salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    const request = { ...this.form.value } as UsuarioRequest;

    if (this.isEdicao && !request.senha) {
      delete (request as any).senha;
    }

    const op = (this.isEdicao && this.idEdicao)
      ? this.usuarioService.atualizar(this.idEdicao, request)
      : this.usuarioService.salvar(request);

    op.subscribe({
      next: () => {
        this.form.markAsPristine(); // Garante que não pergunte ao sair
        this.router.navigate(['/usuarios']);
      },
      error: () => this.isSaving = false
    });
  }

  cancelar() {
    this.router.navigate(['/usuarios']);
  }

  temMudancasNaoSalvas(): boolean {

    return !this.isSaving && this.form.dirty;
  }
}
