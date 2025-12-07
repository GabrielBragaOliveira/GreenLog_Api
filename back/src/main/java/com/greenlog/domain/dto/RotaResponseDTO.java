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
public record RotaResponseDTO(    
    Long id,
    String nome,
    List<BairroResponseDTO> listaDeBairros,
    PontoColetaResponseDTO pontoColetaDestino,
    Boolean ativo
        
) {}
