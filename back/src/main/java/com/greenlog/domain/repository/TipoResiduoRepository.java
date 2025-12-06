/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.repository;

import com.greenlog.domain.entity.TipoResiduo;
import com.greenlog.domain.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kayqu
 */
public interface TipoResiduoRepository extends JpaRepository<TipoResiduo, Long>, JpaSpecificationExecutor<TipoResiduo>{
    Optional<TipoResiduo> findByNome(String nome);
    boolean existsByNome(String nome);
    List<Usuario> findByAtivo(Boolean ativo);
    boolean existsByNomeAndIdNot(String nome, Long id);
}
