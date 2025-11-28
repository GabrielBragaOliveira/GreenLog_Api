/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.domain.dto.ConexaoBairroRequestDTO;
import com.greenlog.domain.dto.ConexaoBairroResponseDTO;
import com.greenlog.domain.entity.Bairro;
import com.greenlog.domain.entity.ConexaoBairro;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.mapper.ConexaoBairroMapper;
import com.greenlog.domain.repository.ConexaoBairroRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kayqu
 */
@Service
public class ConexaoBairroService {

    @Autowired
    private ConexaoBairroRepository conexaoBairroRepository;
    @Autowired
    private BairroService bairroService;
    @Autowired
    private ConexaoBairroMapper conexaoBairroMapper;

    @Transactional(readOnly = true)
    public List<ConexaoBairroResponseDTO> listar() {
        return conexaoBairroRepository.findAll().stream()
                .map(conexaoBairroMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConexaoBairro buscarEntityPorId(Long id) {
        return conexaoBairroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conexão não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public ConexaoBairroResponseDTO buscarPorId(Long id) {
        ConexaoBairro conexao = buscarEntityPorId(id);
        return conexaoBairroMapper.toResponseDTO(conexao);
    }

    @Transactional
    public ConexaoBairroResponseDTO salvar(ConexaoBairroRequestDTO request) {
        Bairro origem = bairroService.buscarEntityPorId(request.bairroOrigemId());
        Bairro destino = bairroService.buscarEntityPorId(request.bairroDestinoId());
        ConexaoBairro novaConexao = conexaoBairroMapper.toEntity(request);
        novaConexao.setBairroOrigem(origem);
        novaConexao.setBairroDestino(destino);

        return conexaoBairroMapper.toResponseDTO(conexaoBairroRepository.save(novaConexao));
    }

    @Transactional
    public ConexaoBairroResponseDTO atualizar(Long id, ConexaoBairroRequestDTO request) {
        ConexaoBairro conexaoExistente = buscarEntityPorId(id);

        Bairro origem = bairroService.buscarEntityPorId(request.bairroOrigemId());
        Bairro destino = bairroService.buscarEntityPorId(request.bairroDestinoId());
        conexaoBairroMapper.updateEntityFromDTO(request, conexaoExistente);
        conexaoExistente.setBairroOrigem(origem);
        conexaoExistente.setBairroDestino(destino);

        return conexaoBairroMapper.toResponseDTO(conexaoBairroRepository.save(conexaoExistente));
    }

    @Transactional
    public void excluir(Long id) {
        ConexaoBairro conexao = buscarEntityPorId(id);
        conexaoBairroRepository.delete(conexao);
    }
}
