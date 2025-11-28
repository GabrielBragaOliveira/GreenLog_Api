/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.repository;

import com.greenlog.domain.entity.Caminhao;
import com.greenlog.domain.entity.Itinerario;
import com.greenlog.domain.entity.Rota;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kayqu
 */
public interface ItinerarioRepository extends JpaRepository<Itinerario, Long> {
// Implementa a regra de negócio: Caminhão só pode ter 1 itinerário por dia.
Optional<Itinerario> findByCaminhaoAndData(Caminhao caminhao, LocalDate data);

// Usado na regra de negócio para impedir a exclusão do Caminhão/Rota se estiver em uso
boolean existsByCaminhao(Caminhao caminhao);
boolean existsByRota(Rota rota);
}
