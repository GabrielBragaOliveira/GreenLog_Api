import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, ValidatorFn, AbstractControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ComponenteComFormulario } from '../../../nucleo/guards/form-exit.guard';
import { ConexaoService } from '../../../nucleo/servicos/conexao.service';
import { BairroService } from '../../../nucleo/servicos/bairro.service';
import { BairroResponse } from '../../../compartilhado/models/bairro.model';
import { ConexaoBairroRequest } from '../../../compartilhado/models/conexao-bairro.model';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { MessageModule } from 'primeng/message';

@Component({
  selector: 'app-conexoes-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CardModule,
    ButtonModule,
    DropdownModule,
    InputNumberModule,
    MessageModule
  ],
  templateUrl: './conexoes-form.component.html',
  styleUrl: './conexoes-form.component.scss'
})
export class ConexoesFormComponent implements OnInit, ComponenteComFormulario {
  
  private fb = inject(FormBuilder);
  private conexaoService = inject(ConexaoService);
  private bairroService = inject(BairroService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  validadorOrigemDestino: ValidatorFn = (control: AbstractControl) => {
    const origem = control.get('bairroOrigemId')?.value;
    const destino = control.get('bairroDestinoId')?.value;
    return origem && destino && origem === destino ? { origemIgualDestino: true } : null;
  };

  form = this.fb.group({
    bairroOrigemId: [null as number | null, [Validators.required]],
    bairroDestinoId: [null as number | null, [Validators.required]],
    distancia: [null as number | null, [Validators.required, Validators.min(0.1)]]
  }, { validators: this.validadorOrigemDestino });

  bairros: BairroResponse[] = [];
  isEdicao = false;
  idEdicao: number | null = null;
  isSaving = false;

  ngOnInit() {
    this.bairroService.listar().subscribe(dados => this.bairros = dados);
    
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdicao = true;
      this.idEdicao = +id;
      this.carregarConexao(this.idEdicao);
    }
  }

  carregarConexao(id: number) {
    this.conexaoService.buscarPorId(id).subscribe(conexao => {
      this.form.patchValue({
        bairroOrigemId: conexao.bairroOrigem.id,
        bairroDestinoId: conexao.bairroDestino.id,
        distancia: conexao.distancia
      });
    });
  }

  salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    const request = this.form.value as ConexaoBairroRequest;

    const operacao = (this.isEdicao && this.idEdicao)
      ? this.conexaoService.atualizar(this.idEdicao, request)
      : this.conexaoService.salvar(request);

    operacao.subscribe({
      next: () => {
        this.form.markAsPristine();
        this.router.navigate(['/conexoes']);
      },
      error: () => this.isSaving = false
    });
  }

  cancelar() {
    this.router.navigate(['/conexoes']);
  }

  temMudancasNaoSalvas(): boolean {
    return !this.isSaving && this.form.dirty;
  }
}