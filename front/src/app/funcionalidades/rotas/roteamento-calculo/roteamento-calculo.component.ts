import { Component, OnInit, inject, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs'; // Importante para carregar dados em paralelo

// Services
import { RoteamentoService } from '../../../nucleo/servicos/roteamento.service';
import { BairroService } from '../../../nucleo/servicos/bairro.service';
import { PontoColetaService } from '../../../nucleo/servicos/ponto-coleta.service';
import { RotaService } from '../../../nucleo/servicos/rota.service';
import { ConexaoService } from '../../../nucleo/servicos/conexao.service'; // <--- NOVO
import { MessageService } from 'primeng/api';

// Models
import { BairroResponse } from '../../../compartilhado/models/bairro.model';
import { PontoColetaResponse } from '../../../compartilhado/models/ponto-coleta.model';
import { ResultadoRota } from '../../../compartilhado/models/roteamento.model';
import { RotaRequest } from '../../../compartilhado/models/rota.model';
import { ConexaoBairroResponse } from '../../../compartilhado/models/conexao-bairro.model'; // <--- NOVO

// PrimeNG & Vis.js
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { PanelModule } from 'primeng/panel';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { DividerModule } from 'primeng/divider';
import { Network, Options } from 'vis-network';
import { DataSet } from 'vis-data';

@Component({
  selector: 'app-roteamento-calculo',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DropdownModule,
    ButtonModule,
    CardModule,
    PanelModule,
    DialogModule,
    InputTextModule,
    DividerModule
  ],
  templateUrl: './roteamento-calculo.component.html',
  styleUrl: './roteamento-calculo.component.scss'
})
export class RoteamentoCalculoComponent implements OnInit, OnDestroy {

  // Injeções
  private roteamentoService = inject(RoteamentoService);
  private bairroService = inject(BairroService);
  private pontoService = inject(PontoColetaService);
  private rotaService = inject(RotaService);
  private conexaoService = inject(ConexaoService); // <--- INJETADO
  private messageService = inject(MessageService);

  @ViewChild('networkContainer') networkContainer!: ElementRef;
  private network: Network | null = null;

  readonly NOME_BAIRRO_ORIGEM = 'Centro';

  // Dados
  todosBairros: BairroResponse[] = [];
  todasConexoes: ConexaoBairroResponse[] = []; // <--- Cache das conexões
  bairrosDestinoFiltrados: BairroResponse[] = [];

  // Seleção
  origemBairroId: number | null = null;
  pontosDestino: PontoColetaResponse[] = [];
  destinoBairroId: number | null = null;
  destinoPontoId: number | null = null;

  // Estado
  isCalculating = false;
  resultado: ResultadoRota | null = null;
  showSalvarDialog = false;
  nomeRotaSalvar = '';

  ngOnInit() {
    this.carregarDadosIniciais();
  }

  ngOnDestroy() {
    if (this.network) {
      this.network.destroy();
    }
  }

  carregarDadosIniciais() {
    // Usamos forkJoin para carregar Bairros e Conexões simultaneamente
    forkJoin({
      bairros: this.bairroService.listar(),
      conexoes: this.conexaoService.listar()
    }).subscribe({
      next: (dados) => {
        this.todosBairros = dados.bairros;
        this.todasConexoes = dados.conexoes; // Guarda as conexões para consulta futura
        this.definirOrigemFixa();
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao carregar dados iniciais.' });
      }
    });
  }

  private definirOrigemFixa() {
    const bairroCentro = this.todosBairros.find(b => b.nome === this.NOME_BAIRRO_ORIGEM);
    if (bairroCentro) {
      this.origemBairroId = bairroCentro.id;
      this.bairrosDestinoFiltrados = this.todosBairros.filter(b => b.id !== this.origemBairroId);
    } else {
      this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Bairro de origem não encontrado.' });
    }
  }

  aoSelecionarDestino() {
    this.destinoPontoId = null;
    this.pontosDestino = [];
    if (this.destinoBairroId) {
      this.carregarPontosDestino(this.destinoBairroId);
    }
  }

  private carregarPontosDestino(bairroId: number) {
    this.pontoService.buscarPorBairro(bairroId).subscribe({
      next: (pontos) => {
        this.pontosDestino = pontos;
        if (pontos.length === 0) {
          this.messageService.add({ severity: 'info', summary: 'Aviso', detail: 'Sem pontos de coleta neste bairro.' });
        }
      }
    });
  }

  calcular() {
    if (!this.origemBairroId || !this.destinoBairroId || !this.destinoPontoId) {
      this.messageService.add({ severity: 'warn', summary: 'Atenção', detail: 'Preencha todos os campos.' });
      return;
    }

    this.isCalculating = true;
    this.resultado = null;

    this.roteamentoService.calcularRota(this.origemBairroId, this.destinoBairroId).subscribe({
      next: (res) => {
        this.resultado = res;
        this.isCalculating = false;

        if (res.listaOrdenadaDeBairros.length === 0) {
          this.messageService.add({ severity: 'warn', summary: 'Sem rota', detail: 'Caminho não encontrado.' });
        } else {
          setTimeout(() => this.renderizarGrafo(), 100);
        }
      },
      error: () => this.isCalculating = false
    });
  }

  private renderizarGrafo() {
    if (!this.networkContainer || !this.resultado) return;

    const bairros = this.resultado.listaOrdenadaDeBairros;
    const nodes = new DataSet<any>([]);
    const edges = new DataSet<any>([]);

    bairros.forEach((bairro, index) => {
      // Verifica se o nó já existe
      if (!nodes.get(bairro.id)) {

        // --- LÓGICA DE CORES E TAMANHOS ---
        const isOrigem = bairro.id === this.origemBairroId;
        const isDestinoAlvo = bairro.id === this.destinoBairroId;

        let corFundo = '#4B5563'; // Cinza Escuro (Intermediário)
        let tamanho = 20;         // Pequeno

        if (isOrigem) {
          corFundo = '#22C55E'; // Verde (Origem)
          tamanho = 30;         // Grande
        } else if (isDestinoAlvo) {
          corFundo = '#3B82F6'; // Azul (Destino Final)
          tamanho = 30;         // Grande
        }

        nodes.add({
          id: bairro.id,
          label: bairro.nome,
          title: bairro.descricao || 'Bairro da Rota',
          level: index, // <--- TRUQUE: Força a posição horizontal baseada na ordem de chegada
          shape: 'dot',
          color: corFundo,
          size: tamanho,
          font: {
            color: '#374151',
            size: 14,
            face: 'Inter',
            strokeWidth: 3,
            strokeColor: '#ffffff'
          },
          borderWidth: 2
        });
      }

      // --- ARESTAS ---
      if (index < bairros.length - 1) {
        const proximoBairro = bairros[index + 1];

        const conexao = this.todasConexoes.find(c =>
          c.bairroOrigem.id === bairro.id && c.bairroDestino.id === proximoBairro.id
        );

        const labelDistancia = conexao ? `${conexao.distancia} km` : '?';

        edges.add({
          from: bairro.id,
          to: proximoBairro.id,
          label: labelDistancia,
          arrows: 'to',
          color: { color: '#64748B' },
          width: 3,
          font: {
            align: 'top',
            size: 12,
            color: '#475569',
            background: 'rgba(255, 255, 255, 0.7)'
          },
          smooth: {
            enabled: true,
            type: 'curvedCW',
            roundness: 0.2
          }
        });
      }
    });

    const options: Options = {
      nodes: {
        shadow: true,
        fixed: true // Impede que o usuário bagunce a posição calculada
      },
      edges: {
        shadow: false,
        smooth: {
          enabled: true,
          type: 'dynamic',
          forceDirection: 'none',
          roundness: 0.5
        }
      },
      layout: {
        hierarchical: {
          enabled: true,
          direction: 'LR',       // <--- Força Esquerda -> Direita
          sortMethod: 'directed',
          levelSeparation: 200,  // Distância horizontal entre os nós
          nodeSpacing: 150       // Distância vertical (se houvesse ramificações)
        }
      },
      physics: false, // <--- IMPORTANTE: Desliga a física para manter a linha reta estática
      interaction: {
        dragNodes: false, // Opcional: Trava os nós no lugar
        zoomView: true,
        dragView: true
      }
    };

    const data = { nodes, edges };
    this.network = new Network(this.networkContainer.nativeElement, data, options);

    // Ajusta o zoom para caber tudo na tela
    setTimeout(() => {
      this.network?.fit();
    }, 100);
  }

  abrirModalSalvar() {
    const nomeBairro = this.todosBairros.find(b => b.id === this.destinoBairroId)?.nome;
    const pontoSelecionado = this.pontosDestino.find(p => p.id === this.destinoPontoId);
    if (nomeBairro && pontoSelecionado) {
      this.nomeRotaSalvar = `${nomeBairro} (${pontoSelecionado.nomePonto})`;
    } else {
      this.nomeRotaSalvar = '';
    }
    this.showSalvarDialog = true;
  }

  salvarRota() {
    if (!this.resultado) return;
    const idsBairros = this.resultado.listaOrdenadaDeBairros.map(b => b.id);
    const novaRota: RotaRequest = {
      nome: this.nomeRotaSalvar,
      listaDeBairrosIds: idsBairros
    };
    this.rotaService.salvar(novaRota).subscribe({
      next: () => this.showSalvarDialog = false,
      error: () => { }
    });
  }
}