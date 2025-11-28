/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.template;

import com.greenlog.domain.entity.Caminhao;
import com.greenlog.domain.repository.CaminhaoRepository;
import com.greenlog.util.ValidadorRegexSingleton;
import org.springframework.stereotype.Component;
import com.greenlog.exception.RegraDeNegocioException;

/**
 *
 * @author Kayqu
 */
@Component
public class ProcessadorCadastroCaminhao extends ProcessadorDeCadastro<Caminhao> {

    private final CaminhaoRepository caminhaoRepository;

    public ProcessadorCadastroCaminhao(CaminhaoRepository caminhaoRepository) {
        this.caminhaoRepository = caminhaoRepository;
    }

    @Override
    protected void validarRegrasEspecificas(Caminhao caminhao) {
        if (caminhaoRepository.existsByPlaca(caminhao.getPlaca())) {
            throw new RegraDeNegocioException("Erro: Placa já cadastrada no sistema.");
        }
        if (!ValidadorRegexSingleton.getInstance().isPlacaValida(caminhao.getPlaca())) {
            throw new RegraDeNegocioException("Erro: Formato de placa inválido (Validação Singleton).");
        }
    }

    @Override
    protected Caminhao salvarNoBanco(Caminhao caminhao) {
        return caminhaoRepository.save(caminhao);
    }
    
    @Override
    protected void notificarSucesso(Caminhao caminhao) {
        // Simulação de notificação
        System.out.println("LOG: Caminhão " + caminhao.getPlaca() + " cadastrado com sucesso.");
    }
}