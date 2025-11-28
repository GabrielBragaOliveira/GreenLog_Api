/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.dto;

import com.greenlog.enums.Perfil;

/**
 *
 * @author Kayqu
 */
public record UsuarioResponseDTO (
    Long id,
    String nome,
    String email,
    Perfil perfil
) {}
