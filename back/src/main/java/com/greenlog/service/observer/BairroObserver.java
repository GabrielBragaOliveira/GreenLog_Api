/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.observer;

import com.greenlog.domain.entity.Bairro;
import com.greenlog.domain.repository.PontoColetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author GabrielB
 */
@Service
public class BairroObserver implements StatusObserver {

    @Autowired
    private PontoColetaRepository pontoColetaRepository;

    @Override
    public void notificarAlteracaoStatus(Object entidade) {
        if (entidade instanceof Bairro bairro && !bairro.getAtivo()) {
            pontoColetaRepository.findByBairro(bairro)
                .forEach(p -> {
                    p.setAtivo(false);
                    pontoColetaRepository.save(p);
                });
        }
    }
}