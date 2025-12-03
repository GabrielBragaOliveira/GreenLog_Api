/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.dto;

import java.util.List;

/**
 *
 * @author Kayqu
 */
public record CaminhaoResponseDTO(     
     Long id,
     String placa,
     String motorista,
     Integer capacidadeKg,
     List<TipoResiduoResponseDTO> tiposSuportados,
     Boolean ativo
     
     ) 
{}
