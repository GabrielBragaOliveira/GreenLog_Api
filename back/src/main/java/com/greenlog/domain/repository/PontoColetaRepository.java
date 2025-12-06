/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.repository;

import com.greenlog.domain.entity.Bairro;
import com.greenlog.domain.entity.PontoColeta;
import com.greenlog.domain.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kayqu
 */
public interface PontoColetaRepository extends JpaRepository<PontoColeta, Long>, JpaSpecificationExecutor<PontoColeta> {
    List<PontoColeta> findByBairro(Bairro bairro);
    boolean existsByBairro(Bairro bairro);
    Optional<PontoColeta> findBynomePonto(String nomePonto);
    List<Usuario> findByAtivo(Boolean ativo);
    Optional<PontoColeta> findByNomePonto(String nomePonto);
    List<PontoColeta> findByBairroIdAndAtivo(Long bairroId, Boolean ativo);
    List<PontoColeta> findByTiposResiduosAceitos_Id(Long tipoId);
    boolean existsByNomePontoAndIdNot(String nomePonto, Long id);
}
