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
public record TipoResiduoRequestDTO(    
    @NotBlank(message = "O nome do tipo de resíduo é obrigatório.")
    @Size(max = 50)
    String nome
        
) {}
