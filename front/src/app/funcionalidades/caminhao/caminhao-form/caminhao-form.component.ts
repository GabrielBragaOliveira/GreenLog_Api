import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ComponenteComFormulario } from '../../../nucleo/guards/form-exit.guard';
import { CaminhaoService } from '../../../nucleo/servicos/caminhao.service';
import { TipoResiduoService } from '../../../nucleo/servicos/tipo-residuo.service';
import { TipoResiduoResponse } from '../../../compartilhado/models/tipo-residuo.model';
import { CaminhaoRequest } from '../../../compartilhado/models/caminhao.model';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { ButtonModule } from 'primeng/button';
import { MultiSelectModule } from 'primeng/multiselect';

@Component({
  selector: 'app-caminhao-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CardModule, InputTextModule, InputNumberModule, ButtonModule, MultiSelectModule],
  templateUrl: './caminhao-form.component.html',
  styleUrl: './caminhao-form.component.scss'
})
export class CaminhaoFormComponent implements OnInit, ComponenteComFormulario {
  
  private fb = inject(FormBuilder);
  private caminhaoService = inject(CaminhaoService);
  private tipoResiduoService = inject(TipoResiduoService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  readonly PLACA_REGEX = /^[A-Z]{3}-?\d{4}|^[A-Z]{3}[0-9]{1}[A-Z]{1}[0-9]{2}$/;

  form = this.fb.group({
    placa: ['', [Validators.required, Validators.pattern(this.PLACA_REGEX)]],
    motorista: ['', [Validators.required]],
    capacidadeKg: [0, [Validators.required, Validators.min(1)]],
    tiposSuportadosIds: [<number[]>[], [Validators.required]]
  });

  tiposResiduos: TipoResiduoResponse[] = [];
  isEdicao = false;
  idEdicao: number | null = null;
  isSaving = false;

  ngOnInit() {
    this.carregarTiposResiduo();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdicao = true;
      this.idEdicao = +id;
      this.carregarCaminhao(this.idEdicao);
    }
  }

  carregarTiposResiduo() {
    this.tipoResiduoService.listar().subscribe(tipos => { 
      this.tiposResiduos = tipos;
    });
  }

  carregarCaminhao(id: number) {
    this.caminhaoService.buscarPorId(id).subscribe(caminhao => {
      const idsTipos = caminhao.tiposSuportados.map(t => t.id);
      this.form.patchValue({
        placa: caminhao.placa,
        motorista: caminhao.motorista,
        capacidadeKg: caminhao.capacidadeKg,
        tiposSuportadosIds: idsTipos
      });
    });
  }

  salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    const request: CaminhaoRequest = this.form.value as CaminhaoRequest;
    request.placa = request.placa.toUpperCase();

    const operacao = (this.isEdicao && this.idEdicao) 
      ? this.caminhaoService.atualizar(this.idEdicao, request)
      : this.caminhaoService.salvar(request);

    operacao.subscribe({
      next: () => this.navegarAposSucesso(),
      error: () => this.isSaving = false
    });
  }

  navegarAposSucesso() {
    this.form.markAsPristine();
    this.router.navigate(['/caminhoes']);
  }

  cancelar() {
    this.router.navigate(['/caminhoes']);
  }

  temMudancasNaoSalvas(): boolean {
    return !this.isSaving && this.form.dirty;
  }

  converterParaMaiusculas(event: Event) {
    const input = event.target as HTMLInputElement;
    const valor = input.value.toUpperCase();
    
    // Atualiza o input visualmente e o FormControl
    if (input.value !== valor) {
      input.value = valor;
      this.form.get('placa')?.setValue(valor);
    }
  }
}