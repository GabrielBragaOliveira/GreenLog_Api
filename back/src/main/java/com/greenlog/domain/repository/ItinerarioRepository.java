/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.repository;

import com.greenlog.domain.entity.Caminhao;
import com.greenlog.domain.entity.Itinerario;
import com.greenlog.domain.entity.Rota;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kayqu
 */
public interface ItinerarioRepository extends JpaRepository<Itinerario, Long>, JpaSpecificationExecutor<Itinerario> {

    Optional<Itinerario> findByCaminhaoAndData(Caminhao caminhao, LocalDate data);

    boolean existsByCaminhao(Caminhao caminhao);

    boolean existsByRota(Rota rota);

    List<Itinerario> findByRota(Rota rota);

    List<Itinerario> findByTiposResiduosAceitos_Id(Long tipoResiduoId);
}
