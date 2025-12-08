/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.observer;

import com.greenlog.domain.entity.Bairro;
import com.greenlog.domain.entity.PontoColeta;
import com.greenlog.service.PontoColetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author GabrielB
 */
@Service
public class BairroObserver implements StatusObserver {

    @Autowired
    private PontoColetaService pontoColetaService;

    @Override
    public void notificarAlteracaoStatus(Object entidade) {
        if (entidade instanceof Bairro bairro && !bairro.getAtivo()) {
            for (PontoColeta p : pontoColetaService.buscarPontosPorBairro(bairro)) {
                if (p.getAtivo())pontoColetaService.alterarStatus(p.getId());
            }
        }
    }
}
