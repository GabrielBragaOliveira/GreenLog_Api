/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.observer;

import com.greenlog.domain.entity.Rota;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author GabrielB
 */
@Component
public class RotaSubject {

    private List<StatusObserver> observers = new ArrayList<>();

    public void addObserver(StatusObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(Rota rota) {
        observers.forEach(o -> o.notificarAlteracaoStatus(rota));
    }
}