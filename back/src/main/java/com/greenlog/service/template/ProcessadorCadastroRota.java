/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.template;

import com.greenlog.domain.entity.Rota;
import com.greenlog.domain.repository.RotaRepository;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.util.ValidadorRegexSingleton;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class ProcessadorCadastroRota extends ProcessadorDeCadastro<Rota> {

    private final RotaRepository rotaRepository;

    public ProcessadorCadastroRota(RotaRepository rotaRepository) {
        this.rotaRepository = rotaRepository;
    }

    @Override
    protected void validarRegrasEspecificas(Rota rota) {
        
        if (rota.getNome().trim() == null || rota.getNome().trim().isBlank()) throw new RegraDeNegocioException("O nome da rota é obrigatório.");
        
        if (!ValidadorRegexSingleton.getInstance().isNomeENumeroValida(rota.getNome().trim())) throw new RegraDeNegocioException("Erro: Nome da Rota inválido. Deve conter apenas letras e numeros e ter no mínimo 3 caracteres.");
        
    }

    @Override
    protected Rota salvarNoBanco(Rota rota) {
        return rotaRepository.save(rota);
    }
    
    @Override
    protected void notificarSucesso(Rota rota) {
        System.out.println("A rota " + rota.getNome() + " cadastrado com sucesso.");
    }
}
