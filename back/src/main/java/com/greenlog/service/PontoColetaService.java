/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.domain.entity.PontoColeta;
import com.greenlog.domain.repository.PontoColetaRepository;
import com.greenlog.domain.dto.PontoColetaRequestDTO;
import com.greenlog.domain.dto.PontoColetaResponseDTO;
import com.greenlog.domain.entity.Bairro;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.mapper.PontoColetaMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Kayqu
 */
@Service
public class PontoColetaService {

    @Autowired
    private PontoColetaRepository pontoColetaRepository;
    @Autowired
    private BairroService bairroService;
    @Autowired
    private TipoResiduoService tipoResiduoService;
    @Autowired
    private PontoColetaMapper pontoColetaMapper;
    
    @Transactional(readOnly = true)
    public List<PontoColetaResponseDTO> listar() {
        return pontoColetaRepository.findAll().stream()
                .map(pontoColetaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PontoColeta> buscarPontosPorBairro(Bairro bairro) {
        return pontoColetaRepository.findByBairro(bairro);
    }

    @Transactional(readOnly = true)
    public PontoColeta buscarEntityPorId(Long id) {
        return pontoColetaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ponto de Coleta não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public PontoColetaResponseDTO buscarPorId(Long id) {
        return pontoColetaMapper.toResponseDTO(buscarEntityPorId(id));
    }

    @Transactional
    public PontoColetaResponseDTO salvar(PontoColetaRequestDTO request) {
        PontoColeta novoPonto = pontoColetaMapper.toEntity(request);
        novoPonto.setBairro(bairroService.buscarEntityPorId(request.bairroId()));
        novoPonto.setTiposResiduosAceitos(request.tiposResiduosIds().stream()
                .map(tipoResiduoService::buscarEntityPorId)
                .collect(Collectors.toList()));

        return pontoColetaMapper.toResponseDTO(pontoColetaRepository.save(novoPonto));
    }

    @Transactional
    public PontoColetaResponseDTO atualizar(Long id, PontoColetaRequestDTO request) {
        PontoColeta pontoExistente = buscarEntityPorId(id);

        pontoColetaMapper.updateEntityFromDTO(request, pontoExistente);
        pontoExistente.setBairro(bairroService.buscarEntityPorId(request.bairroId()));
        pontoExistente.setTiposResiduosAceitos(request.tiposResiduosIds().stream()
                .map(tipoResiduoService::buscarEntityPorId)
                .collect(Collectors.toList()));

        return pontoColetaMapper.toResponseDTO(pontoColetaRepository.save(pontoExistente));
    }

    @Transactional
    public void excluir(Long id) {
        PontoColeta ponto = buscarEntityPorId(id);
        pontoColetaRepository.delete(ponto);
    }
}
