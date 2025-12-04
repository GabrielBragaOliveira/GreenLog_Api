/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.controller;

import com.greenlog.domain.dto.RotaRequestDTO;
import com.greenlog.domain.dto.RotaResponseDTO;
import com.greenlog.service.RotaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/rotas")
@CrossOrigin(origins = "http://localhost:4200")
public class RotaController {

    private final RotaService rotaService;

    public RotaController(RotaService rotaService) {
        this.rotaService = rotaService;
    }
    
    @GetMapping("/busca")
    public ResponseEntity<List<RotaResponseDTO>> buscaAvancada(@RequestParam("q") String query) {
        List<RotaResponseDTO> resultado = rotaService.buscarAvancado(query);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping
    public ResponseEntity<List<RotaResponseDTO>> listar() {
        return ResponseEntity.ok(rotaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RotaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(rotaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<RotaResponseDTO> salvar(@Valid @RequestBody RotaRequestDTO request) {
        RotaResponseDTO response = rotaService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RotaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody RotaRequestDTO request) {
        RotaResponseDTO response = rotaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        rotaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}