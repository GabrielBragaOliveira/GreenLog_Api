/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.domain.dto.TipoResiduoRequestDTO;
import com.greenlog.domain.dto.TipoResiduoResponseDTO;
import com.greenlog.domain.entity.TipoResiduo;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.mapper.TipoResiduoMapper;
import com.greenlog.domain.repository.TipoResiduoRepository;
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
public class TipoResiduoService {

    @Autowired
    private TipoResiduoRepository tipoResiduoRepository;
    @Autowired
    private TipoResiduoMapper tipoResiduoMapper;

    @Transactional(readOnly = true)
    public List<TipoResiduoResponseDTO> listar() {
        return tipoResiduoRepository.findAll().stream()
                .map(tipoResiduoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TipoResiduo buscarEntityPorId(Long id) {
        return tipoResiduoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de Resíduo não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public TipoResiduoResponseDTO buscarPorId(Long id) {
        return tipoResiduoMapper.toResponseDTO(buscarEntityPorId(id));
    }

    @Transactional
    public TipoResiduoResponseDTO salvar(TipoResiduoRequestDTO request) {
        TipoResiduo novoTipo = tipoResiduoMapper.toEntity(request);
        return tipoResiduoMapper.toResponseDTO(tipoResiduoRepository.save(novoTipo));
    }

    @Transactional
    public TipoResiduoResponseDTO atualizar(Long id, TipoResiduoRequestDTO request) {
        TipoResiduo tipoExistente = buscarEntityPorId(id);
        tipoResiduoMapper.updateEntityFromDTO(request, tipoExistente);
        return tipoResiduoMapper.toResponseDTO(tipoResiduoRepository.save(tipoExistente));
    }

    @Transactional
    public void excluir(Long id) {
        TipoResiduo tipo = buscarEntityPorId(id);
        tipoResiduoRepository.delete(tipo);
    }
}
