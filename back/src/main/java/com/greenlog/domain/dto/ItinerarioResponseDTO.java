/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.dto;
import java.time.LocalDate;

/**
 *
 * @author Kayqu
 */
public record ItinerarioResponseDTO(
    Long id,
    LocalDate data,
    CaminhaoResponseDTO caminhao,
    RotaResponseDTO rota,
    TipoResiduoResponseDTO tipoResiduo        
) {}
