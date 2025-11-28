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
public record PontoColetaResponseDTO(    
    Long id,
    String nomeResponsavel,
    String contato,
    String endereco,
    BairroResponseDTO bairro,
    List<TipoResiduoResponseDTO> tiposResiduosAceitos
        
) {}
