/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.dto;

import com.greenlog.enums.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Kayqu
 */
public record UsuarioRequestDTO(
    @NotBlank(message = "O nome é obrigatório.")
    String nome,

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Formato de email inválido.")
    String email,

    // Senha pode ser nula na atualização (se não for alterada)
    String senha,

    @NotNull(message = "O perfil é obrigatório.")
    Perfil perfil
) {}
