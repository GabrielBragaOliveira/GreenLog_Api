/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.domain.dto.BairroResponseDTO;
import com.greenlog.domain.dto.ResultadoRotaDTO;
import com.greenlog.domain.entity.Bairro;
import com.greenlog.domain.entity.ConexaoBairro;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.domain.repository.ConexaoBairroRepository;
import com.greenlog.service.adapter.ConexaoBairroAdapter;
import com.greenlog.service.strategy.AlgoritmoDeRota;
import com.greenlog.service.strategy.DijkstraStrategy;
import com.greenlog.util.Adjacente;
import java.util.List;
import java.util.Map;
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
    private AlgoritmoDeRota algoritmoDeRota = new DijkstraStrategy();

    private Map<Long, List<Adjacente>> montarGrafo() {
        List<ConexaoBairro> conexoesTodas = conexaoBairroRepository.findAll();

        List<ConexaoBairro> conexoesValidas = conexoesTodas.stream()
                .filter(c -> Boolean.TRUE.equals(c.getAtivo()))
                .collect(Collectors.toList());

        return conexaoBairroAdapter.adaptarParaGrafo(conexoesValidas);
    }

    @Transactional(readOnly = true)
    public ResultadoRotaDTO calcularMelhorRota(Long idOrigem, Long idDestino) {

        Map<Long, List<Adjacente>> grafo = montarGrafo();

        Bairro bairroCentro = bairroService.buscarEntityPorNome("Centro");

        if (!idOrigem.equals(bairroCentro.getId())) {
            throw new RegraDeNegocioException(
                    "A origem deve ser obrigatoriamente o bairro 'Centro'.");
        }

        if (Boolean.FALSE.equals(bairroCentro.getAtivo())) {
            throw new RegraDeNegocioException(
                    "O bairro Centro deve estar ativo");
        }

        if (Boolean.FALSE.equals(bairroService.buscarEntityPorId(idDestino).getAtivo())) {
            throw new RegraDeNegocioException(
                    "O bairro destino deve estar ativo");
        }

        if (idOrigem.equals(idDestino)) {
            throw new RegraDeNegocioException("Os bairros de origem e destino não podem ser iguais.");
        }

        ResultadoRotaDTO resultadoIda = algoritmoDeRota.calcular(
                idOrigem,
                idDestino,
                grafo,
                bairroService::buscarEntityPorId
        );

        ResultadoRotaDTO resultadoVolta = algoritmoDeRota.calcular(
                idDestino,
                idOrigem,
                grafo,
                bairroService::buscarEntityPorId
        );

        Double distanciaTotal = resultadoIda.distanciaTotal() + resultadoVolta.distanciaTotal();

        List<BairroResponseDTO> listaCompleta = new java.util.ArrayList<>(resultadoIda.listaOrdenadaDeBairros());

        if (!resultadoVolta.listaOrdenadaDeBairros().isEmpty()) {
            List<BairroResponseDTO> bairrosVolta = resultadoVolta.listaOrdenadaDeBairros();
            listaCompleta.addAll(bairrosVolta.subList(1, bairrosVolta.size()));
        }

        return new ResultadoRotaDTO(distanciaTotal, listaCompleta);
    }
}
