/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.domain.dto.CaminhaoRequestDTO;
import com.greenlog.domain.dto.CaminhaoResponseDTO;
import com.greenlog.domain.entity.Caminhao;
import com.greenlog.exception.EntidadeEmUsoException;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.mapper.CaminhaoMapper;
import com.greenlog.domain.repository.CaminhaoRepository;
import com.greenlog.domain.repository.ItinerarioRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.greenlog.service.template.ProcessadorCadastroCaminhao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Kayqu
 */
@Service
public class CaminhaoService {

    @Autowired
    private CaminhaoRepository caminhaoRepository;
    @Autowired
    private ItinerarioRepository itinerarioRepository;
    @Autowired
    private TipoResiduoService tipoResiduoService;
    @Autowired
    private CaminhaoMapper caminhaoMapper;
    @Autowired
    private ProcessadorCadastroCaminhao processadorCadastroCaminhao;

    @Transactional(readOnly = true)
    public List<CaminhaoResponseDTO> listar() {
        return caminhaoRepository.findAll().stream()
                .map(caminhaoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Caminhao buscarEntityPorId(Long id) {
        return caminhaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Caminhão não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public CaminhaoResponseDTO buscarPorId(Long id) {
        return caminhaoMapper.toResponseDTO(buscarEntityPorId(id));
    }

    @Transactional
    public CaminhaoResponseDTO salvar(CaminhaoRequestDTO request) {
        Caminhao novoCaminhao = caminhaoMapper.toEntity(request);
        novoCaminhao.setTiposSuportados(request.tiposSuportadosIds().stream()
                .map(tipoResiduoService::buscarEntityPorId)
                .collect(Collectors.toList()));

        Caminhao caminhaoSalvo = processadorCadastroCaminhao.processar(novoCaminhao);
        return caminhaoMapper.toResponseDTO(caminhaoSalvo);
    }

    @Transactional
    public CaminhaoResponseDTO atualizar(Long id, CaminhaoRequestDTO request) {
        Caminhao caminhaoExistente = buscarEntityPorId(id);
        caminhaoMapper.updateEntityFromDTO(request, caminhaoExistente);
        caminhaoExistente.setTiposSuportados(request.tiposSuportadosIds().stream()
                .map(tipoResiduoService::buscarEntityPorId)
                .collect(Collectors.toList()));

        return caminhaoMapper.toResponseDTO(caminhaoRepository.save(caminhaoExistente));
    }

    @Transactional
    public void excluir(Long id) {
        Caminhao caminhao = buscarEntityPorId(id);

        if (itinerarioRepository.existsByCaminhao(caminhao)) {
            throw new EntidadeEmUsoException("Caminhão não pode ser excluído pois está associado a um ou mais itinerários.");
        }

        caminhaoRepository.delete(caminhao);
    }
}
