/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.controller;

import com.greenlog.domain.dto.ItinerarioRequestDTO;
import com.greenlog.domain.dto.ItinerarioResponseDTO;
import com.greenlog.service.ItinerarioService;
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
@RequestMapping("/api/itinerarios")
@CrossOrigin(origins = "http://localhost:4200")
public class ItinerarioController {

    private final ItinerarioService itinerarioService;

    public ItinerarioController(ItinerarioService itinerarioService) {
        this.itinerarioService = itinerarioService;
    }
    
    @GetMapping("/busca")
    public ResponseEntity<List<ItinerarioResponseDTO>> buscaAvancada(@RequestParam("q") String query) {
        List<ItinerarioResponseDTO> resultado = itinerarioService.buscarAvancado(query);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping
    public ResponseEntity<List<ItinerarioResponseDTO>> listar() {
        return ResponseEntity.ok(itinerarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItinerarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itinerarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ItinerarioResponseDTO> salvar(@Valid @RequestBody ItinerarioRequestDTO request) {
        ItinerarioResponseDTO response = itinerarioService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItinerarioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ItinerarioRequestDTO request) {
        ItinerarioResponseDTO response = itinerarioService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        itinerarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}