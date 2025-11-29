import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { BairroService } from '../../../nucleo/servicos/bairro.service';
import { BairroRequest } from '../../../compartilhado/models/bairro.model';

@Component({
  selector: 'app-bairros-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputTextModule,
    InputTextareaModule,
    ButtonModule,
    RouterModule,
    CardModule,
    ToastModule
  ],

  templateUrl: './bairros-form.component.html',
  styleUrl: './bairros-form.component.scss'
})
export class BairrosFormComponent implements OnInit {


  private fb = inject(FormBuilder);
  private bairroService = inject(BairroService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  form = this.fb.group({
    nome: ['', [Validators.required,Validators.maxLength(50)]],
    descricao: ['', [Validators.maxLength(200)]],
  });

  isEdicao = false;
  idEdicao: number | null = null;
  isSaving = false;

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdicao = true;
      this.idEdicao = +id;
      this.carregarBairro(this.idEdicao);
    }
  }

  carregarBairro(id: number) {
    this.bairroService.buscarPorId(id).subscribe({
      next: (bairro) => {
        this.form.patchValue({
          nome: bairro.nome,
          descricao: bairro.descricao
        });
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
    const request = this.form.value as BairroRequest;

    const op = (this.isEdicao && this.idEdicao)
      ? this.bairroService.atualizar(this.idEdicao, request)
      : this.bairroService.salvar(request);

    op.subscribe({
      next: () => {
        this.form.markAsPristine();
        this.router.navigate(['/bairros']);
      },
      error: () => this.isSaving = false
    });
  }

  cancelar() {
    this.router.navigate(['/bairros']);
  }
  temMudancasNaoSalvas(): boolean {
    return this.form.dirty && !this.form.pristine;
  }
}