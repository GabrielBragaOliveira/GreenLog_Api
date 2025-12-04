/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.controller;

import com.greenlog.domain.dto.BairroRequestDTO;
import com.greenlog.domain.dto.BairroResponseDTO;
import com.greenlog.service.BairroService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("/api/bairros")
@CrossOrigin(origins = "http://localhost:4200")
public class BairroController {

    private final BairroService bairroService;

    public BairroController(BairroService bairroService) {
        this.bairroService = bairroService;
    }
    
    @GetMapping("/busca")
    public ResponseEntity<List<BairroResponseDTO>> buscaAvancada(@RequestParam("q") String query) {
        List<BairroResponseDTO> resultado = bairroService.buscarAvancado(query);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping
    public ResponseEntity<List<BairroResponseDTO>> listar() {
        return ResponseEntity.ok(bairroService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BairroResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(bairroService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<BairroResponseDTO> salvar(@Valid @RequestBody BairroRequestDTO request) {
        BairroResponseDTO response = bairroService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BairroResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody BairroRequestDTO request) {
        BairroResponseDTO response = bairroService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(@PathVariable @Valid Long id) {
        bairroService.alterarStatus(id);
        return ResponseEntity.noContent().build();
    }
}