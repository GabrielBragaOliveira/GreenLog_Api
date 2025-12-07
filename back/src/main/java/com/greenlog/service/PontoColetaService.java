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
import com.greenlog.domain.entity.TipoResiduo;
import com.greenlog.domain.repository.ItinerarioRepository;
import com.greenlog.exception.ConflitoException;
import com.greenlog.exception.ErroValidacaoException;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.mapper.PontoColetaMapper;
import com.greenlog.service.template.ProcessadorCadastroPontoColeto;
import java.time.LocalDate;
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
    @Autowired
    private BuscaAvancadaService buscaAvancadaService;
    @Autowired
    private ProcessadorCadastroPontoColeto processadorCadastroPontoColeto;
    @Autowired
    private ItinerarioRepository itinerarioRepository;

    @Transactional(readOnly = true)
    public List<PontoColetaResponseDTO> buscarAvancado(String query) {
        List<PontoColeta> resultados;

        if (query == null || query.isBlank()) {
            resultados = pontoColetaRepository.findAll();
        } else {
            resultados = buscaAvancadaService.executarBusca(query, pontoColetaRepository);
        }

        return resultados.stream()
                .map(pontoColetaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

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
        String nomePonto = request.nomePonto() != null ? request.nomePonto().trim() : null;

        if (nomePonto  == null || nomePonto.isBlank()) {
            throw new ErroValidacaoException("O nome do ponto é obrigatório.");
        }
        Optional<PontoColeta> existente = pontoColetaRepository.findByNomePonto(nomePonto);

        if (existente.isPresent()) {
            PontoColeta ponto = existente.get();

            if (!ponto.isAtivo()) {
                ponto.setNomePonto(request.nomePonto().trim());
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

                processadorCadastroPontoColeto.processar(ponto);
                return pontoColetaMapper.toResponseDTO(ponto);
                
            } else throw new ConflitoException("Já existe um ponto de coleta ativo com este nome.");
        }

        PontoColeta novo = pontoColetaMapper.toEntity(request);
        novo.setBairro(bairroService.buscarEntityPorId(request.bairroId()));
        novo.setTiposResiduosAceitos(
                request.tiposResiduosIds().stream()
                        .map(tipoResiduoService::buscarEntityPorId)
                        .collect(Collectors.toList())
        );
        novo.setAtivo(true);
        PontoColeta salvo = processadorCadastroPontoColeto.processar(novo);
        return pontoColetaMapper.toResponseDTO(salvo);
    }

   @Transactional
    public PontoColetaResponseDTO atualizar(Long id, PontoColetaRequestDTO request) {
        String nomePonto = request.nomePonto() != null ? request.nomePonto().trim() : null;
        PontoColeta pontoExistente = buscarEntityPorId(id);

        if (pontoColetaRepository.existsByNomePontoAndIdNot(nomePonto, id)) {
            throw new ErroValidacaoException("Já existe um ponto de coleta com este nome.");
        }

        pontoColetaMapper.updateEntityFromDTO(request, pontoExistente);
        
        pontoExistente.setNomePonto(nomePonto);
        pontoExistente.setEmail(request.email());
        pontoExistente.setBairro(bairroService.buscarEntityPorId(request.bairroId()));
        pontoExistente.setTiposResiduosAceitos(request.tiposResiduosIds().stream()
                .map(tipoResiduoService::buscarEntityPorId)
                .collect(Collectors.toList()));

        return pontoColetaMapper.toResponseDTO(pontoColetaRepository.save(pontoExistente));
    }

    @Transactional
    public void alterarStatus(Long id) {
        PontoColeta ponto = pontoColetaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ponto de coleta não encontrado."));

        boolean novoStatus = !ponto.isAtivo();

        if (novoStatus) {
            if (!ponto.getBairro().isAtivo()) {
                throw new RegraDeNegocioException(
                        "Não é possível ativar o ponto de coleta: o bairro associado está inativo."
                );
            }
            boolean possuiTipoInativo = false;

            for (TipoResiduo t : ponto.getTiposResiduosAceitos()) {
                if (!t.isAtivo()) {
                    possuiTipoInativo = true;
                    break;
                }
            }

            if (possuiTipoInativo) {
                throw new RegraDeNegocioException(
                        "Não é possível ativar o ponto de coleta: ele possui tipos de resíduo inativos."
                );
            }
        }else{
            if (itinerarioRepository.isBairroEmUsoNoFuturo(ponto.getBairro().getId(), LocalDate.now())) {
                throw new RegraDeNegocioException(
                    "Não é possível desativar este ponto de coleta. O bairro dele (" + ponto.getBairro().getNome() + ") faz parte de um itinerário agendado."
                );
            }
        }

        ponto.setAtivo(novoStatus);
        pontoColetaRepository.save(ponto);
    }

    @Transactional(readOnly = true)
    public List<PontoColetaResponseDTO> listarPorBairro(Long bairroId) {
        if (!bairroService.buscarPorId(bairroId).ativo()) {
            throw new RegraDeNegocioException(
                    "O Bairro selecionado esta Inativo"
            );
        }
        return pontoColetaRepository.findByBairroIdAndAtivo(bairroId, true).stream()
                .map(pontoColetaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}