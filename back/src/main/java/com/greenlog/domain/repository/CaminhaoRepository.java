/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.repository;

import com.greenlog.domain.entity.Caminhao;
import com.greenlog.domain.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kayqu
 */
public interface CaminhaoRepository extends JpaRepository<Caminhao, Long> {
    Optional<Caminhao> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
    List<Usuario> findByAtivo(Boolean ativo);
}