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
import com.greenlog.exception.ConflitoException;
import com.greenlog.exception.ErroValidacaoException;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.mapper.PontoColetaMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
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

        if (request.nomePonto() == null || request.nomePonto().isBlank()) {
            throw new ErroValidacaoException("O nome do ponto é obrigatório.");
        }
        if (request.nomeResponsavel() == null || request.nomeResponsavel().isBlank()) {
            throw new ErroValidacaoException("O nome do responsável é obrigatório.");
        }
        if (request.contato() == null || request.contato().isBlank()) {
            throw new ErroValidacaoException("O contato é obrigatório.");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new ErroValidacaoException("O email é obrigatório.");
        }
        if (request.endereco() == null || request.endereco().isBlank()) {
            throw new ErroValidacaoException("O endereço é obrigatório.");
        }
        if (request.bairroId() == null) {
            throw new ErroValidacaoException("O bairro é obrigatório.");
        }

        Optional<PontoColeta> existente = pontoColetaRepository.findByNomePonto(request.nomePonto());

        if (existente.isPresent()) {
            PontoColeta ponto = existente.get();

            if (!ponto.isAtivo()) {
                ponto.setNomePonto(request.nomePonto());
                ponto.setNomeResponsavel(request.nomeResponsavel());
                ponto.setContato(request.contato());
                ponto.setEmail(request.email());
                ponto.setEndereco(request.endereco());
                ponto.setBairro(bairroService.buscarEntityPorId(request.bairroId()));
                ponto.setTiposResiduosAceitos(
                        request.tiposResiduosIds().stream()
                                .map(tipoResiduoService::buscarEntityPorId)
                                .collect(Collectors.toList())
                );
                ponto.setAtivo(true);

                pontoColetaRepository.save(ponto);
                return pontoColetaMapper.toResponseDTO(ponto);
            } else {
                throw new ConflitoException("Já existe um ponto de coleta ativo com este nome.");
            }
        }

        PontoColeta novo = pontoColetaMapper.toEntity(request);
        novo.setBairro(bairroService.buscarEntityPorId(request.bairroId()));
        novo.setTiposResiduosAceitos(
                request.tiposResiduosIds().stream()
                        .map(tipoResiduoService::buscarEntityPorId)
                        .collect(Collectors.toList())
        );
        novo.setAtivo(true);

        PontoColeta salvo = pontoColetaRepository.save(novo);
        return pontoColetaMapper.toResponseDTO(salvo);
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

    @Transactional
    public void inativar(Long id) {
        PontoColeta ponto = pontoColetaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ponto de coleta não encontrado."));

        ponto.setAtivo(false);
        pontoColetaRepository.save(ponto);
    }
    
    @Transactional(readOnly = true)
    public List<PontoColetaResponseDTO> listarPorBairro(Long bairroId) {
        return pontoColetaRepository.findByBairroId(bairroId).stream()
                .map(pontoColetaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
