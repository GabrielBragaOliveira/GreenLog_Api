/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.controller;

import com.greenlog.domain.dto.PontoColetaRequestDTO;
import com.greenlog.domain.dto.PontoColetaResponseDTO;
import com.greenlog.service.PontoColetaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kayqu
 */
@RestController
@RequestMapping("/api/pontos-coleta")
@CrossOrigin(origins = "http://localhost:4200")
public class PontoColetaController {

    private final PontoColetaService pontoColetaService;

    public PontoColetaController(PontoColetaService pontoColetaService) {
        this.pontoColetaService = pontoColetaService;
    }

    @GetMapping
    public ResponseEntity<List<PontoColetaResponseDTO>> listar(@RequestParam(required = false) Long bairroId) {
        if (bairroId != null) {
            return ResponseEntity.ok(pontoColetaService.listarPorBairro(bairroId));
        }
        return ResponseEntity.ok(pontoColetaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColetaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pontoColetaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PontoColetaResponseDTO> salvar(@Valid @RequestBody PontoColetaRequestDTO request) {
        PontoColetaResponseDTO response = pontoColetaService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoColetaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PontoColetaRequestDTO request) {
        PontoColetaResponseDTO response = pontoColetaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(@PathVariable @Valid Long id) {
        pontoColetaService.alterarStatus(id);
        return ResponseEntity.noContent().build();
    }
}