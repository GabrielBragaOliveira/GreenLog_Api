/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author Kayqu
 */
public record LoginRequestDTO(    
    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Formato de email inválido.")
     String email,

    @NotBlank(message = "A senha é obrigatória.")
     String senha
) {}
