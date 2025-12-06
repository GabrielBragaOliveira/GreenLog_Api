/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.mapper;

import com.greenlog.domain.dto.TipoResiduoRequestDTO;
import com.greenlog.domain.dto.TipoResiduoResponseDTO;
import com.greenlog.domain.entity.TipoResiduo;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class TipoResiduoMapper {

    public TipoResiduoResponseDTO toResponseDTO(TipoResiduo tipoResiduo) {
        return new TipoResiduoResponseDTO(
                tipoResiduo.getId(),
                tipoResiduo.getNome(),
                tipoResiduo.getAtivo()
        );
    }

    public TipoResiduo toEntity(TipoResiduoRequestDTO request) {
        if (request == null) {
            return null;
        }

        TipoResiduo entity = new TipoResiduo();
        entity.setNome(request.nome());
        return entity;
    }

    public void updateEntityFromDTO(TipoResiduoRequestDTO request, TipoResiduo entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setNome(request.nome().trim());
    }
}
