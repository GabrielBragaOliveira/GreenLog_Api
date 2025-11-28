/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.mapper;

import com.greenlog.domain.dto.BairroRequestDTO;
import com.greenlog.domain.dto.BairroResponseDTO;
import com.greenlog.domain.entity.Bairro;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class BairroMapper {

    public BairroResponseDTO toResponseDTO(Bairro bairro) {
        return new BairroResponseDTO(
                bairro.getId(),
                bairro.getNome(),
                bairro.getDescricao());
    }

    public Bairro toEntity(BairroRequestDTO request) {
        if (request == null) {
            return null;
        }

        Bairro entity = new Bairro();
        entity.setNome(request.nome());
        entity.setDescricao(request.descricao());
        return entity;
    }

    public void updateEntityFromDTO(BairroRequestDTO request, Bairro entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setNome(request.nome());
        entity.setDescricao(request.descricao());
    }
}
