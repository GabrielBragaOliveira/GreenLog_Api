/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.controller;

import com.greenlog.domain.dto.ConexaoBairroRequestDTO;
import com.greenlog.domain.dto.ConexaoBairroResponseDTO;
import com.greenlog.service.ConexaoBairroService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kayqu
 */
@RestController
@RequestMapping("/api/conexoes")
@CrossOrigin(origins = "http://localhost:4200")
public class ConexaoBairroController {

    private final ConexaoBairroService conexaoBairroService;

    public ConexaoBairroController(ConexaoBairroService conexaoBairroService) {
        this.conexaoBairroService = conexaoBairroService;
    }

    @GetMapping
    public ResponseEntity<List<ConexaoBairroResponseDTO>> listar() {
        return ResponseEntity.ok(conexaoBairroService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConexaoBairroResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(conexaoBairroService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ConexaoBairroResponseDTO> salvar(@Valid @RequestBody ConexaoBairroRequestDTO request) {
        ConexaoBairroResponseDTO response = conexaoBairroService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConexaoBairroResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ConexaoBairroRequestDTO request) {
        ConexaoBairroResponseDTO response = conexaoBairroService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(@PathVariable @Valid Long id) {
        conexaoBairroService.alterarStatus(id);
        return ResponseEntity.noContent().build();
    }
}
