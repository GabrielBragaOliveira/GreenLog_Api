/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.domain.dto.ResultadoRotaDTO;
import com.greenlog.domain.entity.ConexaoBairro;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.domain.repository.ConexaoBairroRepository;
import com.greenlog.service.adapter.ConexaoBairroAdapter;
import com.greenlog.service.strategy.AlgoritmoDeRota;
import com.greenlog.service.strategy.DijkstraStrategy;
import com.greenlog.util.Adjacente;
import java.util.List;
import java.util.Map;
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
    private Map<Long, List<Adjacente>> grafo;
    private AlgoritmoDeRota algoritmoDeRota = new DijkstraStrategy();

    @Transactional(readOnly = true)
    private void montarGrafoUsandoAdapter() {
        List<ConexaoBairro> conexoes = conexaoBairroRepository.findAll();
        this.grafo = conexaoBairroAdapter.adaptarParaGrafo(conexoes);
    }

    public ResultadoRotaDTO calcularMelhorRota(Long idOrigem, Long idDestino) {

        montarGrafoUsandoAdapter();

        bairroService.buscarEntityPorId(idOrigem);
        bairroService.buscarEntityPorId(idDestino);

        if (idOrigem.equals(idDestino)) {
            throw new RegraDeNegocioException("Os bairros de origem e destino não podem ser iguais.");
        }

        return algoritmoDeRota.calcular(
                idOrigem,
                idDestino,
                grafo,
                bairroService::buscarEntityPorId 
        );
    }
}
