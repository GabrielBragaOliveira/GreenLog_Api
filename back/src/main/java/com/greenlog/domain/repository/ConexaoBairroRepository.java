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

/**
 *
 * @author Kayqu
 */
public interface ConexaoBairroRepository extends JpaRepository<ConexaoBairro, Long> {

    // Usado no algoritmo de Dijkstra para montar o grafo (arestas de saída)
    List<ConexaoBairro> findByBairroOrigem(Bairro bairroOrigem);
    
    // Usado na regra de negócio para impedir a exclusão de um Bairro que está em uso
    boolean existsByBairroOrigemOrBairroDestino(Bairro bairroOrigem, Bairro bairroDestino);
    
    // Usado para garantir que não haja conexões duplicadas (ex: A -> B e A -> B)
    Optional<ConexaoBairro> findByBairroOrigemAndBairroDestino(Bairro origem, Bairro destino);
    
    List<Usuario> findByAtivo(Boolean ativo);
}
