/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.controller;

import com.greenlog.domain.dto.ResultadoRotaDTO;
import com.greenlog.service.RoteamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kayqu
 */
@RestController
@RequestMapping("/api/roteamento")
@CrossOrigin(origins = "http://localhost:4200")
public class RoteamentoController {

    private final RoteamentoService roteamentoService;

    public RoteamentoController(RoteamentoService roteamentoService) {
        this.roteamentoService = roteamentoService;
    }
    @GetMapping("/calcular")
    public ResponseEntity<ResultadoRotaDTO> calcularRota(
            @RequestParam(name = "origemId") Long origemId,
            @RequestParam(name = "destinoId") Long destinoId) {

        ResultadoRotaDTO resultado = roteamentoService.calcularMelhorRota(origemId, destinoId);
        return ResponseEntity.ok(resultado);
    }
}