import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { CalendarModule } from 'primeng/calendar';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';
import { TagModule } from 'primeng/tag';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { ConfirmationService, MessageService } from 'primeng/api';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { CaminhaoService } from '../../nucleo/servicos/caminhao.service';
import { ItinerarioService } from '../../nucleo/servicos/itinerario.service';
import { RotaService } from '../../nucleo/servicos/rota.service';
import { PontoColetaService } from '../../nucleo/servicos/ponto-coleta.service';
import { CaminhaoResponse } from '../../compartilhado/models/caminhao.model';
import { ItinerarioResponse, ItinerarioRequest } from '../../compartilhado/models/itinerario.model';
import { RotaResponse } from '../../compartilhado/models/rota.model';
import { TipoResiduoResponse } from '../../compartilhado/models/tipo-residuo.model';

export interface DiaCalendario {
  data: Date;
  diaMes: number;
  diaSemana: string;
  hoje: boolean;
  isoString: string;
}

@Component({
  selector: 'app-itinerario-scheduler',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CalendarModule,
    ButtonModule,
    TooltipModule,
    TagModule,
    DialogModule,
    DropdownModule,
    InputTextareaModule
  ],
  templateUrl: './itinerarios-agendamento.component.html',
  styleUrl: './itinerarios-agendamento.component.scss'
})
export class ItinerarioSchedulerComponent implements OnInit {
  
  private itinerarioService = inject(ItinerarioService);
  private caminhaoService = inject(CaminhaoService);
  private rotaService = inject(RotaService);
  private pontoService = inject(PontoColetaService);
  private fb = inject(FormBuilder);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);
  dataSelecionada = signal<Date>(new Date());
  rotas = signal<RotaResponse[]>([]);
  itinerarios = signal<ItinerarioResponse[]>([]); 
  isLoading = signal(true);
  caminhoesFiltrados = signal<CaminhaoResponse[]>([]);
  residuosFinais = signal<TipoResiduoResponse[]>([]);
  isLoadingCompatibilidade = signal(false);
  private residuosDaRotaSelecionada: TipoResiduoResponse[] = [];

  modalVisivel = false;
  isSaving = false;

  form = this.fb.group({
    data: [null as Date | null, [Validators.required]],
    rotaId: [null as number | null, [Validators.required]],
    caminhaoId: [{ value: null as number | null, disabled: true }, [Validators.required]],
    tipoResiduoId: [{ value: null as number | null, disabled: true }, [Validators.required]]
  });

  diasDaSemana = computed<DiaCalendario[]>(() => {
    const start = new Date(this.dataSelecionada());
    start.setHours(0, 0, 0, 0);
    const dias: DiaCalendario[] = [];
    const hoje = new Date();
    
    for (let i = 0; i < 7; i++) {
      const d = new Date(start);
      d.setDate(start.getDate() + i);

      const ano = d.getFullYear();
      const mes = String(d.getMonth() + 1).padStart(2, '0');
      const dia = String(d.getDate()).padStart(2, '0');
      const dataFormatada = `${ano}-${mes}-${dia}`;

      dias.push({
        data: d,
        diaMes: d.getDate(),
        diaSemana: this.getDiaSemana(d),
        hoje: d.toDateString() === hoje.toDateString(),
        isoString: dataFormatada 
      });
    }
    return dias;
  });

  todosCaminhoesGrid = signal<CaminhaoResponse[]>([]);

  dadosCronograma = computed(() => {
    const listaCaminhoes = this.todosCaminhoesGrid();
    const listaItinerarios = this.itinerarios();
    const dias = this.diasDaSemana();

    return listaCaminhoes.filter(c => c.ativo).map(caminhao => {
      const agendamentos: { [key: string]: ItinerarioResponse | null } = {};
      dias.forEach(dia => {
        const match = listaItinerarios.find(it => 
          it.caminhao.id === caminhao.id && 
          it.data.toString() === dia.isoString 
        );
        agendamentos[dia.isoString] = match || null;
      });
      return { caminhao, agendamentos };
    });
  });

  ngOnInit() {
    this.carregarDados();
  }

  carregarDados() {
    this.isLoading.set(true);
    forkJoin({
      caminhoes: this.caminhaoService.listar(),
      rotas: this.rotaService.listar(),
      itinerarios: this.itinerarioService.listar()
    }).subscribe({
      next: (dados) => {
        this.todosCaminhoesGrid.set(dados.caminhoes);
        this.rotas.set(dados.rotas);
        this.itinerarios.set(dados.itinerarios);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  novoAgendamento(caminhaoPreSelecionado?: CaminhaoResponse, dataPreSelecionada?: Date) {
    this.form.reset();
    
    this.caminhoesFiltrados.set([]);
    this.residuosFinais.set([]);
    this.form.get('caminhaoId')?.disable();
    this.form.get('tipoResiduoId')?.disable();

    if (dataPreSelecionada) {
      this.form.patchValue({ data: dataPreSelecionada });
    } else {
      this.form.patchValue({ data: this.dataSelecionada() });
    }
    this.modalVisivel = true;
  }

  aoSelecionarRota() {
    const rotaId = this.form.get('rotaId')?.value;
    
    this.form.patchValue({ caminhaoId: null, tipoResiduoId: null });
    this.form.get('caminhaoId')?.disable();
    this.form.get('tipoResiduoId')?.disable();
    this.caminhoesFiltrados.set([]);
    this.residuosFinais.set([]);
    this.residuosDaRotaSelecionada = [];

    if (!rotaId) return;

    this.isLoadingCompatibilidade.set(true);

    const rota = this.rotas().find(r => r.id === rotaId);
    if (rota && rota.listaDeBairros.length > 0) {
        const requests = rota.listaDeBairros.map(b => this.pontoService.buscarPorBairro(b.id));
        forkJoin(requests).subscribe(respostas => {
            const todosPontos = respostas.flat();
            const mapRes = new Map<number, TipoResiduoResponse>();
            todosPontos.forEach(p => p.tiposResiduosAceitos.forEach(t => mapRes.set(t.id, t)));
            this.residuosDaRotaSelecionada = Array.from(mapRes.values());
        });
    }

    this.caminhaoService.buscarCompativeisComRota(rotaId).subscribe({
      next: (caminhoes) => {
        this.caminhoesFiltrados.set(caminhoes);
        this.isLoadingCompatibilidade.set(false);
        this.form.get('caminhaoId')?.enable();
        
        if(caminhoes.length === 0) {
            this.messageService.add({severity:'warn', summary:'Aviso', detail:'Nenhum caminhão atende esta rota.'});
        }
      },
      error: () => this.isLoadingCompatibilidade.set(false)
    });
  }

  aoSelecionarCaminhao() {
    const caminhaoId = this.form.get('caminhaoId')?.value;
    
    this.form.patchValue({ tipoResiduoId: null });
    this.form.get('tipoResiduoId')?.disable();
    this.residuosFinais.set([]);

    if (!caminhaoId) return;

    const caminhao = this.caminhoesFiltrados().find(c => c.id === caminhaoId);

    if (caminhao && this.residuosDaRotaSelecionada.length > 0) {
        const intersecao = this.residuosDaRotaSelecionada.filter(resRota => 
            caminhao.tiposSuportados.some(resCam => resCam.id === resRota.id)
        );

        this.residuosFinais.set(intersecao);
        this.form.get('tipoResiduoId')?.enable();

        if (intersecao.length === 1) {
            this.form.patchValue({ tipoResiduoId: intersecao[0].id });
        }
    }
  }

  salvar() {
    if (this.form.invalid) return;
    this.isSaving = true;
    
    const val = this.form.value;
    const dataObj = val.data!;
    const ano = dataObj.getFullYear();
    const mes = String(dataObj.getMonth() + 1).padStart(2, '0');
    const dia = String(dataObj.getDate()).padStart(2, '0');

    const request: ItinerarioRequest = {
      data: `${ano}-${mes}-${dia}`,
      rotaId: val.rotaId!,
      caminhaoId: val.caminhaoId!,
      tipoResiduoId: val.tipoResiduoId!
    };

    this.itinerarioService.salvar(request).subscribe({
      next: (novo) => {
        this.itinerarios.update(lista => [...lista, novo]);
        this.modalVisivel = false;
        this.isSaving = false;
      },
      error: () => this.isSaving = false
    });
  }
  
  confirmarExclusao(event: Event, id: number) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Cancelar este agendamento?',
      acceptLabel: 'Sim',
      acceptButtonStyleClass: 'p-button-danger p-button-sm',
      accept: () => {
        this.itinerarioService.excluir(id).subscribe(() => {
          this.itinerarios.update(l => l.filter(i => i.id !== id));
        });
      }
    });
  }

  private getDiaSemana(date: Date): string {
    const dias = ['DOM', 'SEG', 'TER', 'QUA', 'QUI', 'SEX', 'SÁB'];
    return dias[date.getDay()];
  }
}