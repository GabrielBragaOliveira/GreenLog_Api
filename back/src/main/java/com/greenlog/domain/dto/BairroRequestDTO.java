/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * @author Kayqu
 */
public record BairroRequestDTO(
        @NotBlank(message = "O nome do bairro é obrigatório.")
        @Size(max = 100)
        String nome,
        
        @Size(max = 255)
        String descricao) 
{}
