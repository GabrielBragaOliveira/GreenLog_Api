/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.template;

import com.greenlog.domain.entity.Bairro;
import com.greenlog.domain.entity.Rota;
import com.greenlog.domain.repository.RotaRepository;
import com.greenlog.exception.ConflitoException;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.util.ValidadorRegexSingleton;
import java.util.List;
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

        if (rota.getNome().trim() == null || rota.getNome().trim().isBlank()) {
            throw new RegraDeNegocioException("O nome da rota é obrigatório.");
        }

        if (!ValidadorRegexSingleton.getInstance().isRotaValida(rota.getNome().trim())) {
            throw new RegraDeNegocioException("Erro: Nome da Rota inválido. Deve conter apenas letras e numeros e ter no mínimo 3 caracteres.");
        }
        if (rota.getId() == null && rotaRepository.existsByNome(rota.getNome())) {
            throw new ConflitoException("Já existe uma rota com este nome.");
        }

        if (!rota.getPontoColetaDestino().isAtivo()) {
            throw new RegraDeNegocioException("O Ponto de Coleta de destino informado está inativo.");
        }
        List<Rota> rotasComMesmoDestino = rotaRepository.findByPontoColetaDestinoAndAtivoTrue(rota.getPontoColetaDestino());

        for (Rota r : rotasComMesmoDestino) {
            if (rota.getId() == null || !r.getId().equals(rota.getId())) {
                throw new ConflitoException(
                        "Já existe uma rota ativa (" + r.getNome() + ") definida para o ponto de coleta '"
                        + rota.getPontoColetaDestino().getNomePonto() + "'. Não é permitido rotas duplicadas para o mesmo destino."
                );
            }
        }
        for (Bairro b : rota.getListaDeBairros()) {
            if (!b.getAtivo()) {
                throw new RegraDeNegocioException("A rota não pode ser criada pois o bairro '" + b.getNome() + "' está inativo.");
            }
        }

        if (rota.getListaDeBairros().contains(rota.getPontoColetaDestino().getBairro())) {
            throw new RegraDeNegocioException("O Ponto de Coleta de destino deve pertencer a um dos bairros listados na rota.");
        }
    }

    @Override
    protected Rota salvarNoBanco(Rota rota) {
        return rotaRepository.save(rota);
    }

    @Override
    protected void notificarSucesso(Rota rota) {
        System.out.println("A rota " + rota.getNome() + " cadastrada com sucesso.");
    }
}
