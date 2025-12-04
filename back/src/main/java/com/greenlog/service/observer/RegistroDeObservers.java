/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.observer;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author GabrielB
 */
@Service
public class RegistroDeObservers {

    @Autowired
    private TipoResiduoSubject tipoResiduoSubject;
    @Autowired
    private BairroSubject bairroSubject;

    @Autowired
    private TipoResiduoObserver tipoResiduoObserver;
    @Autowired
    private BairroObserver bairroObserver;

    @PostConstruct
    public void registrar() {
        tipoResiduoSubject.addObserver(tipoResiduoObserver);
        bairroSubject.addObserver(bairroObserver);
    }
}