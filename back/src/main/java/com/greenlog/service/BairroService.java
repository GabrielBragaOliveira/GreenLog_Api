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
import com.greenlog.domain.repository.RotaRepository;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.exception.ConflitoException;
import com.greenlog.exception.ErroValidacaoException;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.service.observer.BairroSubject;
import com.greenlog.service.template.ProcessadorCadastroBairro;
import com.greenlog.util.ValidadorRegexSingleton;
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
    private RotaRepository rotaRepository;
    @Autowired
    private BairroMapper bairroMapper;
    @Autowired
    private BuscaAvancadaService buscaAvancadaService;
    @Autowired
    private BairroSubject bairroSubject;
    @Autowired
    private ProcessadorCadastroBairro processadorCadastroBairro;

    @Transactional(readOnly = true)
    public List<BairroResponseDTO> buscarAvancado(String query) {
        List<Bairro> resultados;

        if (query == null || query.isBlank()) {
            resultados = bairroRepository.findAll();
        } else {
            resultados = buscaAvancadaService.executarBusca(query, bairroRepository);
        }

        return resultados.stream()
                .map(bairroMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

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
    public Bairro buscarEntityPorNome(String nome) {
        return bairroRepository.findByNome(nome)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bairro não encontrado: " + nome));
    }

    @Transactional(readOnly = true)
    public BairroResponseDTO buscarPorId(Long id) {
        Bairro bairro = buscarEntityPorId(id);
        return bairroMapper.toResponseDTO(bairro);
    }

    @Transactional
    public BairroResponseDTO salvar(BairroRequestDTO request) {
        String nome = request.nome() != null ? request.nome().trim() : null;
        if (nome == null || nome.isBlank()) {
            throw new ErroValidacaoException("O nome do bairro é obrigatório.");
        }

        if (!ValidadorRegexSingleton.getInstance().isNomeValida(nome)) {
            throw new ConflitoException("O nome do bairro tem que ter no minimo 3 caracteres e tem que ter 1 espaço de distancia entre os nomes");
        }
        Optional<Bairro> existente = bairroRepository.findByNome(nome);

        if (existente.isPresent()) {
            Bairro bairro = existente.get();
            if (!bairro.getAtivo()) {
                bairro.setNome(nome);
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

        Bairro salvo = processadorCadastroBairro.processar(novo);
        return bairroMapper.toResponseDTO(salvo);
    }

    @Transactional
    public BairroResponseDTO atualizar(Long id, BairroRequestDTO request) {
        String nome = request.nome() != null ? request.nome().trim() : null;
        Bairro bairroExistente = buscarEntityPorId(id);

        if (bairroRepository.existsByNomeAndIdNot(nome, id)) {
            throw new ErroValidacaoException("Já existe um bairro cadastrado com este nome.");
        }

        bairroMapper.updateEntityFromDTO(request, bairroExistente);

        if (!bairroExistente.getAtivo()) {
            throw new ErroValidacaoException("Não é possível atualizar os dados de um bairro inativo. Ative-o primeiro.");
        }

        Bairro salvo = processadorCadastroBairro.processar(bairroExistente);
        return bairroMapper.toResponseDTO(salvo);
    }

    @Transactional
    public void alterarStatus(Long id) {
        Bairro bairro = buscarEntityPorId(id);

        if (Boolean.TRUE.equals(bairro.getAtivo())) {
            boolean existeRotaAtiva = rotaRepository.existsByBairroIdAndAtivoTrue(id);
            if (existeRotaAtiva) {
                throw new RegraDeNegocioException(
                        "Não é possível inativar o bairro '" + bairro.getNome()
                        + "' pois ele faz parte de uma ou mais Rotas ativas. Edite ou inative as rotas primeiro."
                );
            }
        }

        bairro.setAtivo(!bairro.getAtivo());
        bairroRepository.save(bairro);
        bairroSubject.notifyObservers(bairro);
    }
}