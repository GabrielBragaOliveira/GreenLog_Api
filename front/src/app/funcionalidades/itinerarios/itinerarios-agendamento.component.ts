import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';

// PrimeNG Imports
import { CalendarModule } from 'primeng/calendar';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { TooltipModule } from 'primeng/tooltip';
import { TagModule } from 'primeng/tag';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { ToastModule } from 'primeng/toast';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { ConfirmationService, MessageService } from 'primeng/api';

// Services & Models
import { CaminhaoService } from '../../nucleo/servicos/caminhao.service';
import { ItinerarioService } from '../../nucleo/servicos/itinerario.service';
import { RotaService } from '../../nucleo/servicos/rota.service';
import { CaminhaoResponse } from '../../compartilhado/models/caminhao.model';
import { ItinerarioResponse, ItinerarioRequest } from '../../compartilhado/models/itinerario.model';
import { RotaResponse } from '../../compartilhado/models/rota.model';
import { forkJoin } from 'rxjs';

export interface DiaCalendario {
  data: Date;
  diaMes: number;
  diaSemana: string;
  hoje: boolean;
  isoString: string; // Adicionado para facilitar a chave do map
}

@Component({
  selector: 'app-itinerario-scheduler',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    CalendarModule,
    ButtonModule,
    CheckboxModule,
    TooltipModule,
    TagModule,
    CardModule,
    DialogModule,
    DropdownModule,
    ToastModule,
    ConfirmPopupModule
  ],
  providers: [DatePipe, ConfirmationService, MessageService],
  templateUrl: './itinerarios-agendamento.component.html',
  styleUrl: './itinerarios-agendamento.component.scss'
})
export class ItinerarioSchedulerComponent implements OnInit {
  
  // Injeções
  private itinerarioService = inject(ItinerarioService);
  private caminhaoService = inject(CaminhaoService);
  private rotaService = inject(RotaService);
  private fb = inject(FormBuilder);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);

  // Estado (Signals)
  dataSelecionada = signal<Date>(new Date());
  caminhoes = signal<CaminhaoResponse[]>([]);
  rotas = signal<RotaResponse[]>([]);
  itinerarios = signal<ItinerarioResponse[]>([]);
  isLoading = signal(true);

  // Controle do Dialog de Cadastro
  modalVisivel = false;
  isSaving = false;

  // Formulário Reativo
  form = this.fb.group({
    data: [null as Date | null, [Validators.required]],
    caminhaoId: [null as number | null, [Validators.required]],
    rotaId: [null as number | null, [Validators.required]]
  });

  // --- Computeds (Lógica da Grid) ---

  // 1. Gera a semana baseada na data selecionada
  diasDaSemana = computed<DiaCalendario[]>(() => {
    const start = new Date(this.dataSelecionada());
    // Ajusta para o início da semana (Domingo) se quiser, ou mantém data atual como inicio
    // Aqui mantive a lógica de "A partir de hoje"
    
    const dias: DiaCalendario[] = [];
    const hoje = new Date();
    
    for (let i = 0; i < 7; i++) {
      const d = new Date(start);
      d.setDate(start.getDate() + i);
      
      dias.push({
        data: d,
        diaMes: d.getDate(),
        diaSemana: this.getDiaSemana(d),
        hoje: d.toDateString() === hoje.toDateString(),
        isoString: d.toISOString().split('T')[0] // YYYY-MM-DD
      });
    }
    return dias;
  });

  // 2. Monta a Matriz Caminhão x Dia
  dadosCronograma = computed(() => {
    const listaCaminhoes = this.caminhoes();
    const listaItinerarios = this.itinerarios();
    const dias = this.diasDaSemana();

    return listaCaminhoes.filter(c => c.ativo).map(caminhao => {
      const agendamentos: { [key: string]: ItinerarioResponse | null } = {};

      dias.forEach(dia => {
        // Encontra itinerário para este caminhão nesta data específica
        const match = listaItinerarios.find(it => 
          it.caminhao.id === caminhao.id && 
          it.data.toString() === dia.isoString // Comparação segura de string YYYY-MM-DD
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
    
    // Carrega tudo necessário em paralelo
    forkJoin({
      caminhoes: this.caminhaoService.listar(),
      rotas: this.rotaService.listar(),
      itinerarios: this.itinerarioService.listar() // Idealmente seria buscarPorPeriodo no backend
    }).subscribe({
      next: (dados) => {
        this.caminhoes.set(dados.caminhoes);
        this.rotas.set(dados.rotas);
        this.itinerarios.set(dados.itinerarios);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  // --- Ações de UI ---

  novoAgendamento(caminhaoPreSelecionado?: CaminhaoResponse, dataPreSelecionada?: Date) {
    this.form.reset();
    
    // Pré-preenche se clicou no botão (+) da grid
    if (caminhaoPreSelecionado) {
      this.form.patchValue({ caminhaoId: caminhaoPreSelecionado.id });
    }
    if (dataPreSelecionada) {
      this.form.patchValue({ data: dataPreSelecionada });
    } else {
      this.form.patchValue({ data: this.dataSelecionada() });
    }

    this.modalVisivel = true;
  }

  salvar() {
    if (this.form.invalid) return;

    this.isSaving = true;
    const val = this.form.value;
    
    // Converter Date para String YYYY-MM-DD
    // Ajuste de fuso horário simples para garantir o dia correto
    const dataObj = val.data!;
    const ano = dataObj.getFullYear();
    const mes = String(dataObj.getMonth() + 1).padStart(2, '0');
    const dia = String(dataObj.getDate()).padStart(2, '0');
    const dataFormatada = `${ano}-${mes}-${dia}`;

    const request: ItinerarioRequest = {
      data: dataFormatada,
      caminhaoId: val.caminhaoId!,
      rotaId: val.rotaId!
    };

    this.itinerarioService.salvar(request).subscribe({
      next: (novoItinerario) => {
        // Atualiza a lista localmente para refletir na grid instantaneamente
        this.itinerarios.update(lista => [...lista, novoItinerario]);
        this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Agendamento criado!' });
        this.modalVisivel = false;
        this.isSaving = false;
      },
      error: (err) => {
        this.isSaving = false;
        // O ApiBaseService já exibe toast, mas se quiser tratar específico:
        if(err.status === 409) {
           // Conflito de agenda
        }
      }
    });
  }

  confirmarExclusao(event: Event, id: number) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Cancelar este agendamento?',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim',
      rejectLabel: 'Não',
      acceptButtonStyleClass: 'p-button-danger p-button-sm',
      accept: () => {
        this.itinerarioService.excluir(id).subscribe(() => {
          this.itinerarios.update(lista => lista.filter(i => i.id !== id));
          this.messageService.add({ severity: 'info', summary: 'Cancelado', detail: 'Itinerário removido.' });
        });
      }
    });
  }

  // --- Helpers ---
  private getDiaSemana(date: Date): string {
    const dias = ['DOM', 'SEG', 'TER', 'QUA', 'QUI', 'SEX', 'SÁB'];
    return dias[date.getDay()];
  }
}