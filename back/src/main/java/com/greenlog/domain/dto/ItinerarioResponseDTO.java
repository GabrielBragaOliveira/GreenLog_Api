/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.dto;

import com.greenlog.enums.StatusItinerarioEnum;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Kayqu
 */
public record ItinerarioResponseDTO(
    Long id,
    LocalDate data,
    CaminhaoResponseDTO caminhao,
    RotaResponseDTO rota,
    TipoResiduoResponseDTO tipoResiduo,
    StatusItinerarioEnum statusItinerarioEnum
        
) {}
