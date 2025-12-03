/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Kayqu
 */
public record ConexaoBairroRequestDTO(    
    @NotNull(message = "O ID do bairro de origem é obrigatório.")
    Long bairroOrigemId,

    @NotNull(message = "O ID do bairro de destino é obrigatório.")
    Long bairroDestinoId,

    @NotNull(message = "A distância é obrigatória.")
    @DecimalMin(value = "0.0", inclusive = false, message = "A distância deve ser maior que zero.")
    Double distancia
) {}
