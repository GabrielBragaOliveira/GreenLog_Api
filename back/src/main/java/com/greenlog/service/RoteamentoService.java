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
import com.greenlog.service.strategy.AlgoritmoDeRota;
import com.greenlog.service.strategy.DijkstraStrategy;
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

    // ---------- NOVO: Strategy ----------
    private AlgoritmoDeRota algoritmoDeRota = new DijkstraStrategy();

    /**
     * Padrão ADAPTER: Carrega dados brutos e usa o adapter para montar o grafo.
     */
    @Transactional(readOnly = true)
    private void montarGrafoUsandoAdapter() {
        List<ConexaoBairro> conexoes = conexaoBairroRepository.findAll();
        this.grafo = conexaoBairroAdapter.adaptarParaGrafo(conexoes);
    }

    /**
     * Método público que orquestra o cálculo da melhor rota usando a STRATEGY.
     */
    public ResultadoRotaDTO calcularMelhorRota(Long idOrigem, Long idDestino) {

        // Monta o grafo usando Adapter
        montarGrafoUsandoAdapter();

        // Valida se bairros existem
        bairroService.buscarEntityPorId(idOrigem);
        bairroService.buscarEntityPorId(idDestino);

        if (idOrigem.equals(idDestino)) {
            throw new RegraDeNegocioException("Os bairros de origem e destino não podem ser iguais.");
        }

        // ---------- AQUI A STRATEGY É EXECUTADA ----------
        return algoritmoDeRota.calcular(
                idOrigem,
                idDestino,
                grafo,
                bairroService::buscarEntityPorId // passa função para obter bairros
        );
        // --------------------------------------------------
    }
}
