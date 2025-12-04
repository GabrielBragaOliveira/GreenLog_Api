/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.repository;

import com.greenlog.domain.entity.Bairro;
import com.greenlog.domain.entity.ConexaoBairro;
import com.greenlog.domain.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kayqu
 */
public interface ConexaoBairroRepository extends JpaRepository<ConexaoBairro, Long>, JpaSpecificationExecutor<ConexaoBairro> {
    List<ConexaoBairro> findByBairroOrigem(Bairro bairroOrigem);
    boolean existsByBairroOrigemOrBairroDestino(Bairro bairroOrigem, Bairro bairroDestino);
    Optional<ConexaoBairro> findByBairroOrigemAndBairroDestino(Bairro origem, Bairro destino);
    List<Usuario> findByAtivo(Boolean ativo);
}
