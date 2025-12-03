/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.controller;

import com.greenlog.domain.dto.CaminhaoRequestDTO;
import com.greenlog.domain.dto.CaminhaoResponseDTO;
import com.greenlog.service.CaminhaoService;
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
@RequestMapping("/api/caminhoes")
@CrossOrigin(origins = "http://localhost:4200")
public class CaminhaoController {

    private final CaminhaoService caminhaoService;

    public CaminhaoController(CaminhaoService caminhaoService) {
        this.caminhaoService = caminhaoService;
    }
    
    @GetMapping("/busca")
    public ResponseEntity<List<CaminhaoResponseDTO>> buscaAvancada(@RequestParam("q") String query) {
        List<CaminhaoResponseDTO> resultado = caminhaoService.buscarAvancado(query);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping
    public ResponseEntity<List<CaminhaoResponseDTO>> listar() {
        return ResponseEntity.ok(caminhaoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaminhaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(caminhaoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CaminhaoResponseDTO> salvar(@Valid @RequestBody CaminhaoRequestDTO request) {
        CaminhaoResponseDTO response = caminhaoService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaminhaoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody CaminhaoRequestDTO request) {
        CaminhaoResponseDTO response = caminhaoService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }
 
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(@PathVariable @Valid Long id) {
        caminhaoService.alternarStatus(id);
        return ResponseEntity.noContent().build();
    }
}
