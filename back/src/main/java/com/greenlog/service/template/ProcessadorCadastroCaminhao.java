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
        
        if (caminhao.getPlaca().trim() == null || caminhao.getPlaca().trim().isBlank()) throw new RegraDeNegocioException("A placa do caminhão é obrigatória.");
        if (caminhao.getMotorista().trim() == null || caminhao.getMotorista().trim().isBlank()) throw new RegraDeNegocioException("O nome do motorista é obrigatório.");
        if (caminhao.getCapacidadeKg() == null || caminhao.getCapacidadeKg() <= 0) throw new RegraDeNegocioException("A capacidade deve ser maior que zero.");
        if (caminhao.getTiposSuportados() == null || caminhao.getTiposSuportados().isEmpty()) throw new RegraDeNegocioException("O caminhão deve suportar pelo menos um tipo de resíduo.");
     
        var caminhaoExistente = caminhaoRepository.findByPlaca(caminhao.getPlaca().trim());
        
        if (caminhaoExistente.isPresent()) {
            if (!caminhaoExistente.get().getId().equals(caminhao.getId())) throw new RegraDeNegocioException("Erro: Placa já cadastrada no sistema.");
        }
        
        if (!ValidadorRegexSingleton.getInstance().isPlacaValida(caminhao.getPlaca().trim())) throw new RegraDeNegocioException("Erro: Formato de placa inválido (Validação Singleton).");
        if (!ValidadorRegexSingleton.getInstance().isNomeValida(caminhao.getMotorista().trim())) throw new RegraDeNegocioException("Erro: Nome do motorista inválido.");
        if (!ValidadorRegexSingleton.getInstance().isCapacidadeValida(caminhao.getCapacidadeKg())) throw new RegraDeNegocioException("Erro: Capacidade inválida.");
        
    }

    @Override
    protected Caminhao salvarNoBanco(Caminhao caminhao) {
        return caminhaoRepository.save(caminhao);
    }

    @Override
    protected void notificarSucesso(Caminhao caminhao) {
        System.out.println("LOG: Caminhão " + caminhao.getPlaca() + " cadastrado com sucesso.");
    }
}
