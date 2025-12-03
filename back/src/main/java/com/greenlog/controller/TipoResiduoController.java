/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.controller;

import com.greenlog.domain.dto.TipoResiduoRequestDTO;
import com.greenlog.domain.dto.TipoResiduoResponseDTO;
import com.greenlog.service.TipoResiduoService;
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
@RequestMapping("/api/tipos-residuo")
@CrossOrigin(origins = "http://localhost:4200")
public class TipoResiduoController {

    private final TipoResiduoService tipoResiduoService;

    public TipoResiduoController(TipoResiduoService tipoResiduoService) {
        this.tipoResiduoService = tipoResiduoService;
    }

    @GetMapping
    public ResponseEntity<List<TipoResiduoResponseDTO>> listar() {
        return ResponseEntity.ok(tipoResiduoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoResiduoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoResiduoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoResiduoResponseDTO> salvar(@Valid @RequestBody TipoResiduoRequestDTO request) {
        TipoResiduoResponseDTO response = tipoResiduoService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoResiduoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody TipoResiduoRequestDTO request) {
        TipoResiduoResponseDTO response = tipoResiduoService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(@PathVariable @Valid Long id) {
        tipoResiduoService.alterarStatus(id);
        return ResponseEntity.noContent().build();
    }
}