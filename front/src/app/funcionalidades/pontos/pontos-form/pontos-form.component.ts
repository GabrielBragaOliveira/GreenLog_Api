import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ComponenteComFormulario } from '../../../nucleo/guards/form-exit.guard';
import { PontoColetaService } from '../../../nucleo/servicos/ponto-coleta.service';
import { BairroService } from '../../../nucleo/servicos/bairro.service';
import { TipoResiduoService } from '../../../nucleo/servicos/tipo-residuo.service';
import { BairroResponse } from '../../../compartilhado/models/bairro.model';
import { TipoResiduoResponse } from '../../../compartilhado/models/tipo-residuo.model';
import { PontoColetaRequest } from '../../../compartilhado/models/ponto-coleta.model';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { MultiSelectModule } from 'primeng/multiselect';

@Component({
  selector: 'app-pontos-form',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    CardModule, 
    InputTextModule, 
    ButtonModule, 
    DropdownModule, 
    MultiSelectModule],
  templateUrl: './pontos-form.component.html',
  styleUrl: './pontos-form.component.scss'
})
export class PontosFormComponent implements OnInit, ComponenteComFormulario {
  
  private fb = inject(FormBuilder);
  private pontoService = inject(PontoColetaService);
  private bairroService = inject(BairroService);
  private tipoService = inject(TipoResiduoService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  readonly PHONE_REGEX = /^\(?\d{2}\)?\s?\d{4,5}-?\d{4}$/;

  form = this.fb.group({
    nomePonto: ['', [Validators.required, Validators.maxLength(100)]],
    nomeResponsavel: ['', [Validators.required, Validators.maxLength(100)]],
    contato: ['', [Validators.required, Validators.pattern(this.PHONE_REGEX)]],
    email: ['', [Validators.required, Validators.email]],
    endereco: ['', [Validators.required]],
    bairroId: [null as number | null, [Validators.required]],
    tiposResiduosIds: [<number[]>[], [Validators.required]]
  });

  bairros: BairroResponse[] = [];
  tiposResiduos: TipoResiduoResponse[] = [];
  isEdicao = false;
  idEdicao: number | null = null;
  isSaving = false;

  ngOnInit() {
    this.carregarDependencias();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdicao = true;
      this.idEdicao = +id;
      this.carregarPonto(this.idEdicao);
    }
  }

  carregarDependencias() {
    this.bairroService.listar().subscribe(dados => this.bairros = dados);
    this.tipoService.listar().subscribe(dados => this.tiposResiduos = dados);
  }

  carregarPonto(id: number) {
    this.pontoService.buscarPorId(id).subscribe(ponto => {
      this.form.patchValue({
        nomePonto:ponto.nomePonto,
        nomeResponsavel: ponto.nomeResponsavel,
        contato: ponto.contato,
        email: ponto.email,
        endereco: ponto.endereco,
        bairroId: ponto.bairro.id,
        tiposResiduosIds: ponto.tiposResiduosAceitos.map(t => t.id)
      });
    });
  }

  salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    const request = this.form.value as PontoColetaRequest;

    const op = (this.isEdicao && this.idEdicao)
      ? this.pontoService.atualizar(this.idEdicao, request)
      : this.pontoService.salvar(request);

    op.subscribe({
      next: () => this.navegarAposSucesso(),
      error: () => this.isSaving = false
    });
  }

  navegarAposSucesso() {
    this.form.markAsPristine();
    this.router.navigate(['/pontos-coleta']);
  }

  cancelar() {
    this.router.navigate(['/pontos-coleta']);
  }

  temMudancasNaoSalvas(): boolean {
    return !this.isSaving && this.form.dirty;
  }
}