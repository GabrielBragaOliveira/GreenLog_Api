/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.factory;

import com.greenlog.domain.entity.Caminhao;
import com.greenlog.domain.entity.TipoResiduo;
import java.util.List;

/**
 *
 * @author Kayqu
 */
public class CaminhaoFactory {

    public static Caminhao criarNovoCaminhao(String placa, String motorista, Integer capacidadeKg, List<TipoResiduo> tiposSuportados) {
        Caminhao caminhao = new Caminhao();
        caminhao.setPlaca(placa);
        caminhao.setMotorista(motorista);
        caminhao.setCapacidadeKg(capacidadeKg);
        caminhao.setTiposSuportados(tiposSuportados);
        return caminhao;
    }

}
