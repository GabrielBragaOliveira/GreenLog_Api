/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.mapper;

import com.greenlog.domain.dto.ItinerarioRequestDTO;
import com.greenlog.domain.dto.ItinerarioResponseDTO;
import com.greenlog.domain.entity.Itinerario;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class ItinerarioMapper {

    private final CaminhaoMapper caminhaoMapper;
    private final RotaMapper rotaMapper;
    private final TipoResiduoMapper tipoResiduoMapper;

    public ItinerarioMapper(CaminhaoMapper caminhaoMapper, RotaMapper rotaMapper, TipoResiduoMapper tipoResiduoMapper) {
        this.caminhaoMapper = caminhaoMapper;
        this.rotaMapper = rotaMapper;
        this.tipoResiduoMapper = tipoResiduoMapper;
    }

    public ItinerarioResponseDTO toResponseDTO(Itinerario itinerario) {
        return new ItinerarioResponseDTO(
                itinerario.getId(),
                itinerario.getData(),
                caminhaoMapper.toResponseDTO(itinerario.getCaminhao()),
                rotaMapper.toResponseDTO(itinerario.getRota()),
                itinerario.getTipoResiduo() != null ? tipoResiduoMapper.toResponseDTO(itinerario.getTipoResiduo()) : null,
                itinerario.getStatusItinerarioEnum()
        );
    }

    public Itinerario toEntity(ItinerarioRequestDTO request) {
        if (request == null) {
            return null;
        }
        Itinerario entity = new Itinerario();
        entity.setData(request.data());
        return entity;
    }

    public void updateEntityFromDTO(ItinerarioRequestDTO request, Itinerario entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setData(request.data());
    }
}
