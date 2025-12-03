/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.strategy;

import com.greenlog.domain.dto.ResultadoRotaDTO;
import com.greenlog.domain.entity.Bairro;
import com.greenlog.util.Adjacente;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author GabrielB
 */
public interface AlgoritmoDeRota {
    ResultadoRotaDTO calcular(
            Long idOrigem,
            Long idDestino,
            Map<Long, List<Adjacente>> grafo,
            Function<Long, Bairro> buscarBairroPorId
    );
}