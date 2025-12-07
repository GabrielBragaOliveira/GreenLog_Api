/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.template;

import com.greenlog.domain.entity.Bairro;
import com.greenlog.domain.repository.BairroRepository;
import com.greenlog.exception.ErroValidacaoException;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.util.ValidadorRegexSingleton;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class ProcessadorCadastroBairro extends ProcessadorDeCadastro<Bairro> {

    private final BairroRepository bairroRepository;

    public ProcessadorCadastroBairro(BairroRepository bairroRepository) {
        this.bairroRepository = bairroRepository;
    }

    @Override
    protected void validarRegrasEspecificas(Bairro bairro) {
        
        if (bairro.getNome().trim() == null || bairro.getNome().trim().isBlank()) throw new RegraDeNegocioException("O nome do bairro é obrigatório.");
        if (bairro.getNome().trim().equals("Centro")) throw new RegraDeNegocioException("O Bairro é fixo e nao pode ser modificado");
        
        if (!ValidadorRegexSingleton.getInstance().isNomeValida(bairro.getNome().trim())) throw new RegraDeNegocioException("Erro: Nome do Bairro inválido. Deve conter apenas letras e ter no mínimo 3 caracteres.");
        if (bairroRepository.existsByNomeAndIdNot(bairro.getNome().trim(), bairro.getId())) throw new ErroValidacaoException("Já existe um bairro cadastrado com este nome.");
    }

    @Override
    protected Bairro salvarNoBanco(Bairro bairro) {
        return bairroRepository.save(bairro);
    }
    
    @Override
    protected void notificarSucesso(Bairro bairro) {
        System.out.println("Bairro " + bairro.getNome() + " cadastrado com sucesso.");
    }
}
