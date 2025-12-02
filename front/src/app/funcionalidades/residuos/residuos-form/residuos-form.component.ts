import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { TipoResiduoService } from '../../../nucleo/servicos/tipo-residuo.service';
import { TipoResiduoRequest } from '../../../compartilhado/models/tipo-residuo.model';

@Component({
  selector: 'app-residuos-form',
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
  templateUrl: './residuos-form.component.html',
  styleUrl: './residuos-form.component.scss'
})
export class ResiduosFormComponent implements OnInit {

  private fb = inject(FormBuilder);
  private tipoResiduoService = inject(TipoResiduoService); // Injeção corrigida
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  form = this.fb.group({
    nome: ['', [Validators.required, Validators.maxLength(50)]],
  });

  isEdicao = false;
  idEdicao: number | null = null;
  isSaving = false;

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdicao = true;
      this.idEdicao = +id;
      this.carregarResiduo(this.idEdicao);
    }
  }

  carregarResiduo(id: number) {
    this.tipoResiduoService.buscarPorId(id).subscribe({
      next: (residuo) => {
        this.form.patchValue({
          nome: residuo.nome,
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
    const request = this.form.value as TipoResiduoRequest;

    const op = (this.isEdicao && this.idEdicao)
      ? this.tipoResiduoService.atualizar(this.idEdicao, request)
      : this.tipoResiduoService.salvar(request);

    op.subscribe({
      next: () => {
        this.form.markAsPristine();

        this.router.navigate(['/tipos-residuo']);
      },
      error: () => this.isSaving = false
    });
  }

  cancelar() {
    this.router.navigate(['/tipos-residuo']);
  }

  temMudancasNaoSalvas(): boolean {
    return !this.isSaving && this.form.dirty;
  }
}
