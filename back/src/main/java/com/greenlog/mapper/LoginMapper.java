/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.mapper;

import com.greenlog.domain.dto.UsuarioResponseDTO;
import com.greenlog.domain.entity.Usuario;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class LoginMapper {
    
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getPerfil(),
            usuario.getAtivo()
        );
    }
}
