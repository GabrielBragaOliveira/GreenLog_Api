/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.repository;

import com.greenlog.domain.entity.PontoColeta;
import com.greenlog.domain.entity.Rota;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kayqu
 */
public interface RotaRepository extends JpaRepository<Rota, Long>, JpaSpecificationExecutor<Rota> {
    Optional<Rota> findByNome(String nome);
    boolean existsByNome(String nome);
    @Query("SELECT COUNT(r) > 0 FROM Rota r JOIN r.listaDeBairros b WHERE b.id = :bairroId AND r.ativo = true")
    boolean existsByBairroIdAndAtivoTrue(@Param("bairroId") Long bairroId);
    List<Rota> findByPontoColetaDestinoAndAtivoTrue(PontoColeta pontoColeta);
    List<Rota> findByPontoColetaDestino(PontoColeta pontoColeta);
    List<Rota> findByAtivoTrue();
}