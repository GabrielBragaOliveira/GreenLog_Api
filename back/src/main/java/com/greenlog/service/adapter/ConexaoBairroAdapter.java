/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.adapter;

import com.greenlog.domain.entity.ConexaoBairro;
import com.greenlog.util.Adjacente;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class ConexaoBairroAdapter {
    public Map<Long, List<Adjacente>> adaptarParaGrafo(List<ConexaoBairro> conexoes) {
        Map<Long, List<Adjacente>> grafo = new HashMap<>();

        for (ConexaoBairro conexao : conexoes) {
            Long idOrigem = conexao.getBairroOrigem().getId();
            Long idDestino = conexao.getBairroDestino().getId();
            double distancia = conexao.getDistancia();

            // Garante que a chave de origem exista
            grafo.putIfAbsent(idOrigem, new ArrayList<>());
            
            // Adiciona o adjacente
            grafo.get(idOrigem).add(new Adjacente(idDestino, distancia));
        }

        return grafo;
    }
}