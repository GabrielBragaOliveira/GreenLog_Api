/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.mapper;

import com.greenlog.domain.dto.ConexaoBairroRequestDTO;
import com.greenlog.domain.dto.ConexaoBairroResponseDTO;
import com.greenlog.domain.entity.ConexaoBairro;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class ConexaoBairroMapper {

    private final BairroMapper bairroMapper;

    public ConexaoBairroMapper(BairroMapper bairroMapper) {
        this.bairroMapper = bairroMapper;
    }

    public ConexaoBairroResponseDTO toResponseDTO(ConexaoBairro conexao) {
        return new ConexaoBairroResponseDTO(
                conexao.getId(),
                bairroMapper.toResponseDTO(conexao.getBairroOrigem()),
                bairroMapper.toResponseDTO(conexao.getBairroDestino()),
                conexao.getDistancia()
        );
    }

    public ConexaoBairro toEntity(ConexaoBairroRequestDTO request) {
        if (request == null) {
            return null;
        }

        ConexaoBairro entity = new ConexaoBairro();
        entity.setDistancia(request.distancia());
        return entity;
    }

    public void updateEntityFromDTO(ConexaoBairroRequestDTO request, ConexaoBairro entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setDistancia(request.distancia());
    }
}
