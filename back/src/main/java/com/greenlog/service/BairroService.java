/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.domain.dto.BairroRequestDTO;
import com.greenlog.domain.dto.BairroResponseDTO;
import com.greenlog.domain.entity.Bairro;
import com.greenlog.mapper.BairroMapper;
import com.greenlog.domain.repository.BairroRepository;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.exception.EntidadeEmUsoException;
import com.greenlog.domain.repository.ConexaoBairroRepository;
import com.greenlog.exception.ConflitoException;
import com.greenlog.exception.ErroValidacaoException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kayqu
 */
@Service
public class BairroService {

    @Autowired
    private BairroRepository bairroRepository;
    @Autowired
    private ConexaoBairroRepository conexaoBairroRepository;
    @Autowired
    private BairroMapper bairroMapper;

    @Transactional(readOnly = true)
    public List<BairroResponseDTO> listar() {
        return bairroRepository.findAll().stream()
                .map(bairroMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Bairro buscarEntityPorId(Long id) {
        return bairroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bairro não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public BairroResponseDTO buscarPorId(Long id) {
        Bairro bairro = buscarEntityPorId(id);
        return bairroMapper.toResponseDTO(bairro);
    }

    @Transactional
    public BairroResponseDTO salvar(BairroRequestDTO request) {
        if (request.nome() == null || request.nome().isBlank()) {
            throw new ErroValidacaoException("O nome do bairro é obrigatório.");
        }

        Optional<Bairro> existente = bairroRepository.findByNome(request.nome());

        if (existente.isPresent()) {
            Bairro bairro = existente.get();

            if (!bairro.isAtivo()) {
                bairro.setNome(request.nome());
                bairro.setDescricao(request.descricao());
                bairro.setAtivo(true);

                Bairro salvo = bairroRepository.save(bairro);
                return bairroMapper.toResponseDTO(salvo);

            } else {
                throw new ConflitoException("Já existe um bairro ativo com este nome.");
            }
        }

        Bairro novo = bairroMapper.toEntity(request);
        novo.setAtivo(true);

        Bairro salvo = bairroRepository.save(novo);
        return bairroMapper.toResponseDTO(salvo);
    }

    @Transactional
    public BairroResponseDTO atualizar(Long id, BairroRequestDTO request) {
        Bairro bairroExistente = buscarEntityPorId(id);
        bairroMapper.updateEntityFromDTO(request, bairroExistente);
        return bairroMapper.toResponseDTO(bairroRepository.save(bairroExistente));
    }

    @Transactional
    public void excluir(Long id) {
        Bairro bairro = buscarEntityPorId(id);
        if (conexaoBairroRepository.existsByBairroOrigemOrBairroDestino(bairro, bairro)) {
            throw new EntidadeEmUsoException("Bairro não pode ser excluído pois está associado a uma ou mais conexões.");
        }

        bairroRepository.delete(bairro);
    }

    @Transactional
    public void inativar(Long id) {
        Bairro bairro = bairroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bairro não encontrado."));

        if (conexaoBairroRepository.existsByBairroOrigemOrBairroDestino(bairro, bairro)) {
            throw new EntidadeEmUsoException("Bairro não pode ser inativado pois está associado a uma ou mais conexões.");
        }

        bairro.setAtivo(false);
        bairroRepository.save(bairro);
    }
}
