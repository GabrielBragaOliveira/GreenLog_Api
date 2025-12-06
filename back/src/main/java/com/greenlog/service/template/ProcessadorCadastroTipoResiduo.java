/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.template;

import com.greenlog.domain.entity.TipoResiduo;
import com.greenlog.domain.repository.TipoResiduoRepository;
import com.greenlog.exception.ErroValidacaoException;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.util.ValidadorRegexSingleton;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class ProcessadorCadastroTipoResiduo extends ProcessadorDeCadastro<TipoResiduo> {

    private final TipoResiduoRepository tipoResiduoRepository;

    public ProcessadorCadastroTipoResiduo(TipoResiduoRepository tipoResiduoRepository) {
        this.tipoResiduoRepository = tipoResiduoRepository;
    }

    @Override
    protected void validarRegrasEspecificas(TipoResiduo tipoResiduo) {
        
        if (tipoResiduo.getNome().trim() == null || tipoResiduo.getNome().trim().isBlank()) throw new RegraDeNegocioException("O nome do Residuo é obrigatório.");
        
        if (tipoResiduoRepository.existsByNomeAndIdNot(tipoResiduo.getNome().trim(), tipoResiduo.getId())) throw new ErroValidacaoException("Já existe um tipo de resíduo com este nome.");
        if (!ValidadorRegexSingleton.getInstance().isNomeENumeroValida(tipoResiduo.getNome().trim())) throw new RegraDeNegocioException("Erro: Nome do Residuo inválido. Deve conter apenas letras e numeros e ter no mínimo 3 caracteres.");
    }

    @Override
    protected TipoResiduo salvarNoBanco(TipoResiduo tipoResiduo) {
        return tipoResiduoRepository.save(tipoResiduo);
    }
    
    @Override
    protected void notificarSucesso(TipoResiduo tipoResiduo) {
        System.out.println("O residuo " + tipoResiduo.getNome() + " cadastrado com sucesso.");
    }
}
