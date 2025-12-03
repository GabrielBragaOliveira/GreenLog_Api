/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 *
 * @author Kayqu
 */
public record RotaRequestDTO(    
    @NotBlank(message = "O nome da rota é obrigatório.")
    String nome,

    @NotEmpty(message = "A rota deve incluir pelo menos um bairro.")
    List<Long> listaDeBairrosIds
     
) {}
