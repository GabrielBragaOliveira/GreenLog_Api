/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.observer;

import com.greenlog.domain.entity.Caminhao;
import com.greenlog.domain.entity.PontoColeta;
import com.greenlog.domain.entity.TipoResiduo;
import com.greenlog.domain.repository.CaminhaoRepository;
import com.greenlog.domain.repository.PontoColetaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author GabrielB
 */
@Service
public class TipoResiduoObserver implements StatusObserver {

    @Autowired
    private CaminhaoRepository caminhaoRepository;

    @Autowired
    private PontoColetaRepository pontoColetaRepository;

    @Override
    public void notificarAlteracaoStatus(Object entidade) {
        if (entidade instanceof TipoResiduo tipo && !tipo.isAtivo()) {
            inativarCaminhoes(tipo.getId());
            inativarPontos(tipo.getId());
        }
    }

    @Transactional
    public void inativarCaminhoes(Long tipoId) {

        List<Caminhao> caminhoes = caminhaoRepository.findByTiposSuportados_Id(tipoId);

        for (Caminhao caminhao : caminhoes) {

            boolean possuiAlgumTipoAtivo = caminhao.getTiposSuportados()
                    .stream()
                    .anyMatch(TipoResiduo::isAtivo);

            if (!possuiAlgumTipoAtivo) {
                caminhao.setAtivo(false);
                caminhaoRepository.save(caminhao);
                
            }
        }
    }

    @Transactional
    public void inativarPontos(Long tipoId) {

        List<PontoColeta> pontos = pontoColetaRepository.findByTiposResiduosAceitos_Id(tipoId);

        for (PontoColeta ponto : pontos) {

            boolean possuiTipoAtivo = ponto.getTiposResiduosAceitos()
                    .stream()
                    .anyMatch(TipoResiduo::isAtivo);

            if (!possuiTipoAtivo) {
                ponto.setAtivo(false);
                pontoColetaRepository.save(ponto);
            }
        }
    }
}