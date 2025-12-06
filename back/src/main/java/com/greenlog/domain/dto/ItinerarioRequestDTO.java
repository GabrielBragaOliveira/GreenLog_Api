/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 *
 * @author Kayqu
 */
public record ItinerarioRequestDTO(
    @NotNull(message = "A data é obrigatória.")
    LocalDate data,

    @NotNull(message = "O ID do caminhão é obrigatório.")
    Long caminhaoId,

    @NotNull(message = "O ID da rota é obrigatório.")
    Long rotaId,
    
    @NotNull(message = "Deve haver pelo menos um tipo de resíduo aceito.")
    Long tipoResiduoId
      
) {}
