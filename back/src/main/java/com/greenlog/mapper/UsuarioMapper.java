/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.mapper;

import com.greenlog.domain.entity.Usuario;
import com.greenlog.domain.dto.UsuarioRequestDTO;
import com.greenlog.domain.dto.UsuarioResponseDTO;
import org.springframework.stereotype.Component;
import com.greenlog.service.factory.UsuarioFactory;

/**
 *
 * @author Kayqu
 */
@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getPerfil()
        );
    }

    public Usuario toEntity(UsuarioRequestDTO request) {
        if (request == null) {
            return null;
        }

        return UsuarioFactory.criarNovoUsuario(
                request.nome(),
                request.email(),
                request.senha(),
                request.perfil()
        );
    }

    public void updateEntityFromDTO(UsuarioRequestDTO request, Usuario entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setNome(request.nome());
        entity.setEmail(request.email());
        entity.setPerfil(request.perfil());
    }
}
