/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.mapper;

import com.greenlog.domain.dto.CaminhaoRequestDTO;
import com.greenlog.domain.dto.CaminhaoResponseDTO;
import com.greenlog.domain.entity.Caminhao;
import com.greenlog.service.factory.CaminhaoFactory;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class CaminhaoMapper {

    private final TipoResiduoMapper tipoResiduoMapper;

    public CaminhaoMapper(TipoResiduoMapper tipoResiduoMapper) {
        this.tipoResiduoMapper = tipoResiduoMapper;
    }

    public CaminhaoResponseDTO toResponseDTO(Caminhao caminhao) {
        return new CaminhaoResponseDTO(
                caminhao.getId(),
                caminhao.getPlaca(),
                caminhao.getMotorista(),
                caminhao.getCapacidadeKg(),
                caminhao.getTiposSuportados() == null ? null
                : caminhao.getTiposSuportados().stream()
                        .map(tipoResiduoMapper::toResponseDTO)
                        .collect(Collectors.toList())
        );
    }

    public Caminhao toEntity(CaminhaoRequestDTO request) {
        if (request == null) {
            return null;
        }

        return CaminhaoFactory.criarNovoCaminhao(
                request.placa(),
                request.motorista(),
                request.capacidadeKg(),
                new ArrayList<>()
        );
    }

    public void updateEntityFromDTO(CaminhaoRequestDTO request, Caminhao entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setPlaca(request.placa());
        entity.setMotorista(request.motorista());
        entity.setCapacidadeKg(request.capacidadeKg());
    }
}
