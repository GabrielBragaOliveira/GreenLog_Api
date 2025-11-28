/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.util;

/**
 *
 * @author Kayqu
 */
public class Adjacente {

    private Long idBairroDestino;
    private double distancia;

    public Adjacente(Long idBairroDestino, double distancia) {
        this.idBairroDestino = idBairroDestino;
        this.distancia = distancia;
    }

    public Long getIdBairroDestino() {
        return idBairroDestino;
    }

    public double getDistancia() {
        return distancia;
    }
}
