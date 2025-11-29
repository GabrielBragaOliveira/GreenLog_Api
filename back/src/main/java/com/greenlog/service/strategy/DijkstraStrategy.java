/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.strategy;

import com.greenlog.domain.dto.ResultadoRotaDTO;
import com.greenlog.domain.entity.Bairro;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.util.Adjacente;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author GabrielB
 */
public class DijkstraStrategy implements AlgoritmoDeRota {

    @Override
    public ResultadoRotaDTO calcular(
            Long idOrigem,
            Long idDestino,
            Map<Long, List<Adjacente>> grafo,
            Function<Long, Bairro> buscarBairroPorId) {

        Map<Long, Double> distancias = new HashMap<>();
        Map<Long, Long> antecessores = new HashMap<>();

        PriorityQueue<Map.Entry<Long, Double>> filaPrioridade =
                new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));

        distancias.put(idOrigem, 0.0);
        filaPrioridade.offer(Map.entry(idOrigem, 0.0));

        while (!filaPrioridade.isEmpty()) {
            Long u = filaPrioridade.poll().getKey();

            for (Adjacente adj : grafo.getOrDefault(u, Collections.emptyList())) {
                Long v = adj.getIdBairroDestino();
                double peso = adj.getDistancia();

                double novaDist = distancias.get(u) + peso;

                if (novaDist < distancias.getOrDefault(v, Double.MAX_VALUE)) {
                    distancias.put(v, novaDist);
                    antecessores.put(v, u);
                    filaPrioridade.offer(Map.entry(v, novaDist));
                }
            }
        }

        if (!distancias.containsKey(idDestino)) {
            throw new RecursoNaoEncontradoException(
                    "Não existe rota entre os bairros informados.");
        }

        LinkedList<Long> caminhoIds = new LinkedList<>();
        Long atual = idDestino;

        while (atual != null) {
            caminhoIds.addFirst(atual);
            if (atual.equals(idOrigem)) break;
            atual = antecessores.get(atual);
        }

        List<String> nomesBairros = caminhoIds.stream()
                .map(id -> buscarBairroPorId.apply(id).getNome())
                .collect(Collectors.toList());

        return new ResultadoRotaDTO(distancias.get(idDestino), nomesBairros);
    }
}