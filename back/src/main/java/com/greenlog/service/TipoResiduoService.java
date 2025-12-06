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
import com.greenlog.exception.ConflitoException;
import com.greenlog.exception.ErroValidacaoException;
import com.greenlog.service.observer.TipoResiduoSubject;
import com.greenlog.service.template.ProcessadorCadastroTipoResiduo;
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
public class TipoResiduoService {

    @Autowired
    private TipoResiduoRepository tipoResiduoRepository;
    @Autowired
    private TipoResiduoMapper tipoResiduoMapper;
    @Autowired
    private BuscaAvancadaService buscaAvancadaService;
    @Autowired
    private TipoResiduoSubject tipoResiduoSubject;
    @Autowired
    private ProcessadorCadastroTipoResiduo processadorCadastroTipoResiduo;
    
    @Transactional(readOnly = true)
    public List<TipoResiduoResponseDTO> buscarAvancado(String query) {
        List<TipoResiduo> resultados;

        if (query == null || query.isBlank()) {
            resultados = tipoResiduoRepository.findAll();
        } else {
            resultados = buscaAvancadaService.executarBusca(query, tipoResiduoRepository);
        }

        return resultados.stream()
                .map(tipoResiduoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

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

        Optional<TipoResiduo> existente = tipoResiduoRepository.findByNome(request.nome());

        if (existente.isPresent()) {
            TipoResiduo tipo = existente.get();
            if (!tipo.isAtivo()) {
                tipo.setNome(request.nome());
                tipo.setAtivo(true);

                TipoResiduo response = processadorCadastroTipoResiduo.processar(tipo);
                return tipoResiduoMapper.toResponseDTO(response);
                
            } else throw new ConflitoException("Já existe um tipo de resíduo ativo com este nome.");
        }

        TipoResiduo novo = tipoResiduoMapper.toEntity(request);
        novo.setAtivo(true);

        TipoResiduo salvo = processadorCadastroTipoResiduo.processar(novo);
        return tipoResiduoMapper.toResponseDTO(salvo);
    }

    @Transactional
    public TipoResiduoResponseDTO atualizar(Long id, TipoResiduoRequestDTO request) {
        TipoResiduo tipoExistente = buscarEntityPorId(id);
        
        if (!tipoExistente.isAtivo()) throw new ErroValidacaoException("Não é possível atualizar os dados de um tipo de residuo inativo. Ative-o primeiro.");
        
        TipoResiduo salvo = processadorCadastroTipoResiduo.processar(tipoExistente);
        return tipoResiduoMapper.toResponseDTO(salvo);
        
    }

    @Transactional
    public void alterarStatus(Long id) {
        TipoResiduo tipo = buscarEntityPorId(id);

        tipo.setAtivo(!tipo.isAtivo());
        tipoResiduoRepository.save(tipo);

        tipoResiduoSubject.notifyObservers(tipo);
    }
}
