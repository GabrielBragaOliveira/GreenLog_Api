/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.observer;

import com.greenlog.domain.entity.Itinerario;
import com.greenlog.domain.entity.Rota;
import com.greenlog.domain.repository.ItinerarioRepository;
import com.greenlog.enums.StatusItinerarioEnum;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author GabrielB
 */
@Service
public class RotaObserver implements StatusObserver {

    @Autowired
    private ItinerarioRepository itinerarioRepository;

    @Override
    public void notificarAlteracaoStatus(Object entidade) {

        if (entidade instanceof Rota rota && !rota.isAtivo()) {

            List<Itinerario> itinerarios = itinerarioRepository.findByRota(rota);

            for (Itinerario itinerario : itinerarios) {
                StatusItinerarioEnum statusAtual = itinerario.getStatusItinerarioEnum();

                if (statusAtual == StatusItinerarioEnum.PENDENTE) {
                    itinerario.setStatusItinerarioEnum(StatusItinerarioEnum.CANCELADO);
                } else if (statusAtual == StatusItinerarioEnum.CONCLUIDO) {
                    itinerario.setStatusItinerarioEnum(StatusItinerarioEnum.CONCLUIDO);
                }
                itinerarioRepository.save(itinerario);
            }
        }
    }
}
