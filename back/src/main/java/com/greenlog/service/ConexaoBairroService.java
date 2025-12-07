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
import com.greenlog.exception.RegraDeNegocioException;
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
    @Autowired
    private BuscaAvancadaService buscaAvancadaService;

    @Transactional(readOnly = true)
    public List<ConexaoBairroResponseDTO> buscarAvancado(String query) {
        List<ConexaoBairro> resultados;

        if (query == null || query.isBlank()) {
            resultados = conexaoBairroRepository.findAll();
        } else {
            resultados = buscaAvancadaService.executarBusca(query, conexaoBairroRepository);
        }

        return resultados.stream()
                .map(conexaoBairroMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

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
        
        if(!origem.getAtivo() || !destino.getAtivo()) {
            throw new RegraDeNegocioException("Não é possível criar conexão: um ou ambos os bairros estão inativos.");
        }
        
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
        
        if(!origem.getAtivo() || !destino.getAtivo()) {
            throw new RegraDeNegocioException("Não é possível atualizar conexão: um ou ambos os bairros estão inativos.");
        }
        
        conexaoBairroMapper.updateEntityFromDTO(request, conexaoExistente);
        conexaoExistente.setBairroOrigem(origem);
        conexaoExistente.setBairroDestino(destino);

        return conexaoBairroMapper.toResponseDTO(conexaoBairroRepository.save(conexaoExistente));
    }

    @Transactional
    public void alterarStatus(Long id) {
        ConexaoBairro conexao = conexaoBairroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conexão de bairro não encontrada."));

        if(!conexao.isAtivo()) {
             if(!conexao.getBairroOrigem().getAtivo() || !conexao.getBairroDestino().getAtivo()) {
                 throw new RegraDeNegocioException("Não é possível ativar conexão: os bairros conectados devem estar ativos.");
             }
        }

        if (conexao.isAtivo()) {
            conexao.setAtivo(false);
        } else {
            conexao.setAtivo(true);
        }

        conexaoBairroRepository.save(conexao);
    }
}