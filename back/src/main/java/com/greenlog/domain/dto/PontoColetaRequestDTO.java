/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.dto;

import com.greenlog.util.RegexConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 *
 * @author Kayqu
 */
public record PontoColetaRequestDTO(    
        
    @NotBlank(message = "O nome do Ponto é obrigatório.")
    @Size(max = 100)
    String nomePonto,   
        
    @NotBlank(message = "O nome do responsável é obrigatório.")
    @Size(max = 100)
    String nomeResponsavel,

    @Pattern(regexp = RegexConstants.TELEFONE_REGEX, message = "Formato de contato inválido.")
    String contato,
    
    @NotBlank(message = "O email do responsável é obrigatório.")
    @Email
    String email,

    @NotBlank(message = "O endereço é obrigatório.")
    @Size(max = 255)
    String endereco,

    @NotNull(message = "O ID do bairro é obrigatório.")
    Long bairroId,

    @NotEmpty(message = "Deve haver pelo menos um tipo de resíduo aceito.")
    List<Long> tiposResiduosIds
        
) {}