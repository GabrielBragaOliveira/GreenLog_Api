/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.mapper;

import com.greenlog.domain.dto.RotaRequestDTO;
import com.greenlog.domain.dto.RotaResponseDTO;
import com.greenlog.domain.entity.Rota;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class RotaMapper {

    private final BairroMapper bairroMapper;
    private final PontoColetaMapper pontoColetaMapper;

    public RotaMapper(BairroMapper bairroMapper, PontoColetaMapper pontoColetaMapper) {
        this.bairroMapper = bairroMapper;
        this.pontoColetaMapper = pontoColetaMapper;
    }

    public RotaResponseDTO toResponseDTO(Rota rota) {
        return new RotaResponseDTO(
                rota.getId(),
                rota.getNome(),
                rota.getListaDeBairros() == null ? null
                : rota.getListaDeBairros().stream()
                        .map(bairroMapper::toResponseDTO)
                        .collect(Collectors.toList()),
                rota.getPontoColetaDestino() != null ? pontoColetaMapper.toResponseDTO(rota.getPontoColetaDestino()) : null,
                rota.getAtivo()
        );
    }

    public Rota toEntity(RotaRequestDTO request) {
        if (request == null) {
            return null;
        }

        Rota entity = new Rota();
        entity.setNome(request.nome());
        return entity;
    }

    public void updateEntityFromDTO(RotaRequestDTO request, Rota entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setNome(request.nome());
    }
}