/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.mapper;

import com.greenlog.domain.dto.PontoColetaRequestDTO;
import com.greenlog.domain.dto.PontoColetaResponseDTO;
import com.greenlog.domain.entity.PontoColeta;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class PontoColetaMapper {

    private final BairroMapper bairroMapper;
    private final TipoResiduoMapper tipoResiduoMapper;

    public PontoColetaMapper(BairroMapper bairroMapper, TipoResiduoMapper tipoResiduoMapper) {
        this.bairroMapper = bairroMapper;
        this.tipoResiduoMapper = tipoResiduoMapper;
    }

    public PontoColetaResponseDTO toResponseDTO(PontoColeta ponto) {
        return new PontoColetaResponseDTO(
                ponto.getId(),
                ponto.getNomeResponsavel(),
                ponto.getContato(),
                ponto.getEndereco(),
                bairroMapper.toResponseDTO(ponto.getBairro()),
                ponto.getTiposResiduosAceitos() == null ? null
                : ponto.getTiposResiduosAceitos().stream()
                        .map(tipoResiduoMapper::toResponseDTO)
                        .collect(Collectors.toList())
        );
    }

    public PontoColeta toEntity(PontoColetaRequestDTO request) {
        if (request == null) {
            return null;
        }

        PontoColeta entity = new PontoColeta();
        entity.setNomeResponsavel(request.nomeResponsavel());
        entity.setContato(request.contato());
        entity.setEndereco(request.endereco());
        return entity;
    }

    public void updateEntityFromDTO(PontoColetaRequestDTO request, PontoColeta entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setNomeResponsavel(request.nomeResponsavel());
        entity.setContato(request.contato());
        entity.setEndereco(request.endereco());
    }
}
