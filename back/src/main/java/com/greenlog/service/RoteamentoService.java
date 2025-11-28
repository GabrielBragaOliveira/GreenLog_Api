/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.domain.dto.ResultadoRotaDTO;
import com.greenlog.domain.entity.ConexaoBairro;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.domain.repository.ConexaoBairroRepository;
import com.greenlog.service.adapter.ConexaoBairroAdapter;
import com.greenlog.util.Adjacente;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kayqu
 */
@Service
public class RoteamentoService {

    @Autowired
    private ConexaoBairroRepository conexaoBairroRepository;
    @Autowired
    private BairroService bairroService;
    @Autowired
    private ConexaoBairroAdapter conexaoBairroAdapter;

    // Estrutura do Grafo: Mapeia o ID do Bairro de Origem para a lista de Adjacentes
    private Map<Long, List<Adjacente>> grafo;

    /**
     * Padrão ADAPTER: Carrega dados brutos e usa o adapter para montar o grafo.
     */
    @Transactional(readOnly = true)
    private void montarGrafoUsandoAdapter() {
        // 1. Carrega todas as conexões (dados brutos)
        List<ConexaoBairro> conexoes = conexaoBairroRepository.findAll();

        // 2. Adapta a lista de entidades para a estrutura de grafo otimizada
        this.grafo = conexaoBairroAdapter.adaptarParaGrafo(conexoes);
    }

    /**
     * Implementação do Algoritmo de Dijkstra. Encontra a menor distância e o
     * caminho correspondente no grafo.
     */
    private Map<Long, Long> dijkstra(Long idOrigem, Map<Long, Double> distancias) {

        Map<Long, Long> antecessores = new HashMap<>();

        // Fila de prioridade para selecionar o nó com a menor distância
        PriorityQueue<Map.Entry<Long, Double>> filaPrioridade = new PriorityQueue<>(
                Comparator.comparingDouble(Map.Entry::getValue)
        );

        // Inicialização
        distancias.put(idOrigem, 0.0);
        filaPrioridade.offer(Map.entry(idOrigem, 0.0));

        while (!filaPrioridade.isEmpty()) {
            Long u = filaPrioridade.poll().getKey();

            // Pega os vizinhos do nó atual 'u'
            List<Adjacente> vizinhos = grafo.getOrDefault(u, Collections.emptyList());

            for (Adjacente adjacente : vizinhos) {
                Long v = adjacente.getIdBairroDestino();
                double pesoUV = adjacente.getDistancia();
                double distanciaU = distancias.getOrDefault(u, Double.MAX_VALUE);

                double novaDistancia = distanciaU + pesoUV;

                // Relaxamento: Se acharmos um caminho mais curto para 'v'
                if (novaDistancia < distancias.getOrDefault(v, Double.MAX_VALUE)) {
                    distancias.put(v, novaDistancia);
                    antecessores.put(v, u);
                    filaPrioridade.offer(Map.entry(v, novaDistancia));
                }
            }
        }
        return antecessores;
    }

    /**
     * Método público que orquestra o cálculo da melhor rota.
     */
    public ResultadoRotaDTO calcularMelhorRota(Long idOrigem, Long idDestino) {

        // 1. Monta o grafo usando o Adapter
        montarGrafoUsandoAdapter();

        // Validação inicial (garante que os bairros existam)
        bairroService.buscarEntityPorId(idOrigem);
        bairroService.buscarEntityPorId(idDestino);

        if (idOrigem.equals(idDestino)) {
            throw new RegraDeNegocioException("Os bairros de origem e destino não podem ser iguais.");
        }

        // 2. Prepara o mapa de distâncias e executa Dijkstra
        Map<Long, Double> distancias = new HashMap<>();
        Map<Long, Long> antecessores = dijkstra(idOrigem, distancias);

        // 3. Constrói o resultado final
        return construirResultado(idOrigem, idDestino, distancias, antecessores);
    }

    /**
     * Constrói o DTO de resultado a partir dos mapas gerados por Dijkstra.
     */
    private ResultadoRotaDTO construirResultado(Long idOrigem, Long idDestino,
            Map<Long, Double> distancias,
            Map<Long, Long> antecessores) {

        // Verifica se o destino foi alcançado
        if (!distancias.containsKey(idDestino) || distancias.get(idDestino).equals(Double.MAX_VALUE)) {
            throw new RecursoNaoEncontradoException(
                    "Não foi possível encontrar uma rota entre os bairros fornecidos. Verifique as conexões."
            );
        }

        // 1. Reconstroi o caminho (IDs)
        LinkedList<Long> caminhoIds = new LinkedList<>();
        Long atual = idDestino;
        while (atual != null) {
            caminhoIds.addFirst(atual);
            if (atual.equals(idOrigem)) {
                break; // Garante parada, embora a condição inicial devesse ser suficiente
            }
            atual = antecessores.get(atual);
        }

        // 2. Converte IDs para nomes
        List<String> nomesBairros = caminhoIds.stream()
                .map(id -> bairroService.buscarEntityPorId(id).getNome())
                .collect(Collectors.toList());

        // 3. Monta o DTO final
        ResultadoRotaDTO resultado = new ResultadoRotaDTO(
                distancias.get(idDestino),
                nomesBairros
        );

        return resultado;
    }
}
