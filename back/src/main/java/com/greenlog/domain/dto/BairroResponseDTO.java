/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.dto;

/**
 *
 * @author Kayqu
 */
public record BairroResponseDTO(
        Long id,
        String nome,
        String descricao,
        Boolean ativo

) {}
