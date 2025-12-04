import { Component, OnInit, ElementRef, ViewChild, inject, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { SidebarModule } from 'primeng/sidebar';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { CardModule } from 'primeng/card';
import { Network, Options } from 'vis-network';
import { DataSet } from 'vis-data';
import { BairroService } from '../../nucleo/servicos/bairro.service';
import { ConexaoService } from '../../nucleo/servicos/conexao.service';
import { PontoColetaService } from '../../nucleo/servicos/ponto-coleta.service';
import { BairroResponse } from '../../compartilhado/models/bairro.model';
import { PontoColetaResponse } from '../../compartilhado/models/ponto-coleta.model';
import { ConexaoBairroResponse } from '../../compartilhado/models/conexao-bairro.model';

@Component({
    selector: 'app-mapa-interativo',
    standalone: true,
    imports: [
        CommonModule,
        SidebarModule,
        TableModule,
        TagModule,
        ButtonModule,
        ProgressSpinnerModule,
        CardModule
    ],
    templateUrl: './mapa-interativo.component.html',
    styleUrl: './mapa-interativo.component.scss'
})
export class MapaInterativoComponent implements OnInit, OnDestroy {

    private bairroService = inject(BairroService);
    private conexaoService = inject(ConexaoService);
    private pontoService = inject(PontoColetaService);

    @ViewChild('networkContainer') networkContainer!: ElementRef;

    private network: Network | null = null;
    private nodes!: DataSet<any>;
    private edges!: DataSet<any>;

    isLoading = true;

    sidebarVisible = false;
    bairroSelecionado: BairroResponse | null = null;
    pontosDoBairro: PontoColetaResponse[] = [];
    isLoadingPontos = false;

    private todosBairros: BairroResponse[] = [];
    private todasConexoes: ConexaoBairroResponse[] = [];

    private readonly COLOR_ACTIVE = '#22C55E';
    private readonly COLOR_INACTIVE = '#EF4444';
    private readonly COLOR_DIMMED = 'rgba(200, 200, 200, 0.2)';
    private readonly EDGE_COLOR = '#64748B';
    private readonly EDGE_COLOR_INACTIVE = '#EF4444';

    ngOnInit() {
        this.carregarDadosIniciais();
    }

    carregarDadosIniciais() {
        this.isLoading = true;

        forkJoin({
            bairros: this.bairroService.listar(),
            conexoes: this.conexaoService.listar()
        }).subscribe({
            next: (dados) => {
                this.todosBairros = dados.bairros;
                this.todasConexoes = dados.conexoes;
                this.isLoading = false;
                setTimeout(() => this.initGraph(), 100);
            },
            error: (err) => {
                console.error('Erro ao carregar mapa', err);
                this.isLoading = false;
            }
        });
    }

    private initGraph() {
        if (!this.networkContainer) return;

        this.nodes = new DataSet(
            this.todosBairros.map(b => ({
                id: b.id,
                label: b.nome,
                title: b.descricao || 'Sem descrição',
                shape: 'dot',
                color: b.ativo ? this.COLOR_ACTIVE : this.COLOR_INACTIVE,
                size: 30,
                font: { color: '#374151', size: 16, face: 'Inter', strokeWidth: 0, strokeColor: '#fff' },
                borderWidth: 2
            }))
        );

        this.edges = new DataSet(
            this.todasConexoes.map(c => ({
                id: c.id,
                from: c.bairroOrigem.id,
                to: c.bairroDestino.id,
                label: `${c.distancia} km`,
                arrows: 'to',

                color: {
                    color: c.ativo ? this.EDGE_COLOR : this.EDGE_COLOR_INACTIVE,
                    highlight: c.ativo ? '#15803d' : '#b91c1c',
                    opacity: 1
                },

                dashes: !c.ativo,

                width: 2,
                font: { align: 'top', size: 12, color: '#475569', strokeWidth: 0 }
            }))
        );

        const options: Options = {
            nodes: {
                shadow: true
            },
            edges: {
                shadow: false,
                smooth: {
                    enabled: true,
                    type: 'continuous',
                    roundness: 0.5
                }
            },
            physics: {
                enabled: true,
                barnesHut: {
                    gravitationalConstant: -8000,
                    centralGravity: 0.1,
                    springLength: 350,
                    springConstant: 0.04,
                    damping: 0.09
                },
                stabilization: {
                    iterations: 1000
                }
            },
            interaction: {
                hover: true,
                tooltipDelay: 200,
                zoomView: true
            }
        };

        const data = { nodes: this.nodes, edges: this.edges };
        this.network = new Network(this.networkContainer.nativeElement, data, options);
        this.network.on('click', (params) => {
            if (params.nodes.length > 0) {
                const bairroId = params.nodes[0] as number;
                this.focarNoBairro(bairroId);
            } else {
                this.resetarVisualizacao();
                this.sidebarVisible = false;
            }
        });
    }

    focarNoBairro(bairroId: number) {
        this.abrirDetalhesBairro(bairroId);

        const connectedNodes = this.network?.getConnectedNodes(bairroId) || [];
        const connectedEdges = this.network?.getConnectedEdges(bairroId) || [];

        const nodesToKeepActive = new Set([bairroId, ...connectedNodes]);
        const edgesToKeepActive = new Set(connectedEdges);

        const nodeUpdates = this.todosBairros.map(b => {
            const isConnected = nodesToKeepActive.has(b.id);
            return {
                id: b.id,
                color: isConnected ? (b.ativo ? this.COLOR_ACTIVE : this.COLOR_INACTIVE) : this.COLOR_DIMMED,
                font: { color: isConnected ? '#374151' : 'rgba(200,200,200,0.4)' },
                size: b.id === bairroId ? 45 : (isConnected ? 30 : 20)
            };
        });

        const edgeUpdates = this.todasConexoes.map(c => {
            const isConnected = edgesToKeepActive.has(c.id);
            return {
                id: c.id,
                color: {
                    color: isConnected ? this.EDGE_COLOR : this.COLOR_DIMMED,
                    opacity: isConnected ? 1 : 0.1
                },
                font: { size: isConnected ? 12 : 0 }
            };
        });

        this.nodes.update(nodeUpdates);
        this.edges.update(edgeUpdates);
    }

    resetarVisualizacao() {
        const nodeUpdates = this.todosBairros.map(b => ({
            id: b.id,
            color: b.ativo ? this.COLOR_ACTIVE : this.COLOR_INACTIVE,
            font: { color: '#374151' },
            size: 30
        }));

        const edgeUpdates = this.todasConexoes.map(c => ({
            id: c.id,
            color: {
                color: c.ativo ? this.EDGE_COLOR : this.EDGE_COLOR_INACTIVE,
                opacity: 1
            },
            font: { size: 12 }
        }));

        this.nodes.update(nodeUpdates);
        this.edges.update(edgeUpdates);
    }

    abrirDetalhesBairro(id: number) {
        this.bairroSelecionado = this.todosBairros.find(b => b.id === id) || null;

        if (this.bairroSelecionado) {
            this.sidebarVisible = true;
            this.carregarPontosDoBairro(id);
        }
    }

    carregarPontosDoBairro(bairroId: number) {
        this.isLoadingPontos = true;
        this.pontosDoBairro = [];

        this.pontoService.buscarPorBairro(bairroId).subscribe({
            next: (pontos) => {
                this.pontosDoBairro = pontos;
                this.isLoadingPontos = false;
            },
            error: () => this.isLoadingPontos = false
        });
    }

    ngOnDestroy() {
        if (this.network) {
            this.network.destroy();
            this.network = null;
        }
    }
}