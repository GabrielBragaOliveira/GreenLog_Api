/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.template;

import com.greenlog.domain.entity.PontoColeta;
import com.greenlog.domain.repository.PontoColetaRepository;
import com.greenlog.exception.ErroValidacaoException;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.util.ValidadorRegexSingleton;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class ProcessadorCadastroPontoColeto extends ProcessadorDeCadastro<PontoColeta> {

    private final PontoColetaRepository pontoColetaRepository;

    public ProcessadorCadastroPontoColeto(PontoColetaRepository pontoColetaRepository) {
        this.pontoColetaRepository = pontoColetaRepository;
    }

    @Override
    protected void validarRegrasEspecificas(PontoColeta pontoColeta) {

        if (pontoColeta.getNomePonto().trim() == null || pontoColeta.getNomePonto().trim().isBlank()) {
            throw new RegraDeNegocioException("O nome do ponto é obrigatório.");
        }
        if (pontoColeta.getNomeResponsavel().trim() == null || pontoColeta.getNomeResponsavel().trim().isBlank()) {
            throw new RegraDeNegocioException("O nome do responsável é obrigatório.");
        }
        if (pontoColeta.getContato() == null || pontoColeta.getContato().isBlank()) {
            throw new RegraDeNegocioException("O contato é obrigatório.");
        }
        if (pontoColeta.getEmail().trim() == null || pontoColeta.getEmail().trim().isBlank()) {
            throw new RegraDeNegocioException("O email é obrigatório.");
        }
        if (pontoColeta.getEndereco() == null || pontoColeta.getEndereco().isBlank()) {
            throw new RegraDeNegocioException("O endereço é obrigatório.");
        }
        if (pontoColeta.getBairro() == null) {
            throw new RegraDeNegocioException("O bairro é obrigatório.");
        }
        if (pontoColeta.getBairro().getAtivo() != null && !pontoColeta.getBairro().getAtivo()) {
            throw new RegraDeNegocioException("O bairro informado está inativo e não pode ser utilizado.");
        }
        if (pontoColetaRepository.existsByNomePontoAndIdNot(pontoColeta.getNomePonto().trim(), pontoColeta.getId())) {
            throw new ErroValidacaoException("Já existe um ponto de coleta com este nome.");
        }
        if (!ValidadorRegexSingleton.getInstance().isNomeENumeroValida(pontoColeta.getNomePonto().trim())) {
            throw new RegraDeNegocioException("Erro: Nome do Ponto inválido. Deve conter apenas letras, números e ter no mínimo 3 caracteres.");
        }
        if (!ValidadorRegexSingleton.getInstance().isNomeValida(pontoColeta.getNomeResponsavel().trim())) {
            throw new RegraDeNegocioException("Erro: Nome do Responsável inválido. Deve conter apenas letras e ter no mínimo 3 caracteres.");
        }
        if (!ValidadorRegexSingleton.getInstance().isTelefoneValido(pontoColeta.getContato().trim())) {
            throw new RegraDeNegocioException("Erro: Telefone inválido.");
        }

    }

    @Override
    protected PontoColeta salvarNoBanco(PontoColeta bairro) {
        return pontoColetaRepository.save(bairro);
    }

    @Override
    protected void notificarSucesso(PontoColeta pontoColeta) {
        System.out.println("Ponto de Coleta  " + pontoColeta.getNomePonto() + " cadastrado com sucesso.");
    }
}
