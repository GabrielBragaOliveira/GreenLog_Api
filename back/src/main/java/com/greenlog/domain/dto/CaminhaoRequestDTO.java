/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.dto;

import com.greenlog.util.RegexConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

/**
 *
 * @author Kayqu
 */
public record CaminhaoRequestDTO(
    @Pattern(regexp = RegexConstants.PLACA_REGEX, message = "Formato de placa inválido (Ex: AAA-9999 ou ABC1D23).")
    @NotBlank(message = "A placa é obrigatória.")
    String placa,

    @NotBlank(message = "O nome do motorista é obrigatório.")
    String motorista,

    @NotNull(message = "A capacidade em Kg é obrigatória.")
    @Min(value = 1, message = "A capacidade deve ser maior que zero.")
    @Max(value = 100000, message = "A capacidade deve ser menor que 100.000Kg.")
    Integer capacidadeKg,
    
    @NotEmpty(message = "O caminhão deve suportar pelo menos um tipo de resíduo.")
    List<Long> tiposSuportadosIds
) 
{}
