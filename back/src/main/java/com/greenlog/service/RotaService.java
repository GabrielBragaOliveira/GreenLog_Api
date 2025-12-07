/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.domain.dto.RotaRequestDTO;
import com.greenlog.domain.dto.RotaResponseDTO;
import com.greenlog.domain.entity.PontoColeta;
import com.greenlog.domain.entity.Rota;
import com.greenlog.exception.ConflitoException;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.mapper.RotaMapper;
import com.greenlog.domain.repository.RotaRepository;
import com.greenlog.service.template.ProcessadorCadastroRota;
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
public class RotaService {

    @Autowired
    private RotaRepository rotaRepository;
    @Autowired
    private BairroService bairroService;
    @Autowired
    private PontoColetaService pontoColetaService;
    @Autowired
    private RotaMapper rotaMapper;
    @Autowired
    private BuscaAvancadaService buscaAvancadaService;
    @Autowired
    private ProcessadorCadastroRota processadorCadastroRota;

    @Transactional(readOnly = true)
    public List<RotaResponseDTO> buscarAvancado(String query) {
        List<Rota> resultados;

        if (query == null || query.isBlank()) {
            resultados = rotaRepository.findAll();
        } else {
            resultados = buscaAvancadaService.executarBusca(query, rotaRepository);
        }

        return resultados.stream()
                .map(rotaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RotaResponseDTO> listar() {
        return rotaRepository.findAll().stream()
                .map(rotaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Rota buscarEntityPorId(Long id) {
        return rotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Rota não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public RotaResponseDTO buscarPorId(Long id) {
        return rotaMapper.toResponseDTO(buscarEntityPorId(id));
    }

    @Transactional
    public RotaResponseDTO salvar(RotaRequestDTO request) {
        Optional<Rota> rotaExistenteOpt = rotaRepository.findByNome(request.nome().trim());

        if (rotaExistenteOpt.isPresent()) {
            Rota rotaExistente = rotaExistenteOpt.get();

            if (rotaExistente.isAtivo()) {
                throw new ConflitoException("Já existe uma rota ativa com o nome '" + request.nome() + "'.");
            } else {
                rotaMapper.updateEntityFromDTO(request, rotaExistente);
                rotaExistente.setListaDeBairros(request.listaDeBairrosIds().stream()
                        .map(bairroService::buscarEntityPorId)
                        .collect(Collectors.toList()));
                PontoColeta pontoDestino = pontoColetaService.buscarEntityPorId(request.pontoColetaDestinoId());
                rotaExistente.setPontoColetaDestino(pontoDestino);
                rotaExistente.setAtivo(true);
                Rota rotaSalva = processadorCadastroRota.processar(rotaExistente);
                return rotaMapper.toResponseDTO(rotaSalva);
            }
        }

        Rota novaRota = rotaMapper.toEntity(request);
        novaRota.setListaDeBairros(request.listaDeBairrosIds().stream()
                .map(bairroService::buscarEntityPorId)
                .collect(Collectors.toList()));
        PontoColeta pontoDestino = pontoColetaService.buscarEntityPorId(request.pontoColetaDestinoId());
        novaRota.setPontoColetaDestino(pontoDestino);
        novaRota.setAtivo(true);
        novaRota = processadorCadastroRota.processar(novaRota);
        return rotaMapper.toResponseDTO(novaRota);
    }

    @Transactional
    public RotaResponseDTO atualizar(Long id, RotaRequestDTO request) {
        Rota rotaExistente = buscarEntityPorId(id);

        rotaMapper.updateEntityFromDTO(request, rotaExistente);

        rotaExistente.setListaDeBairros(request.listaDeBairrosIds().stream()
                .map(bairroService::buscarEntityPorId)
                .collect(Collectors.toList()));

        PontoColeta pontoDestino = pontoColetaService.buscarEntityPorId(request.pontoColetaDestinoId());
        rotaExistente.setPontoColetaDestino(pontoDestino);
        Rota save = processadorCadastroRota.processar(rotaExistente);
        return rotaMapper.toResponseDTO(save);
    }

    @Transactional
    public void excluir(Long id) {
        Rota rota = buscarEntityPorId(id);
        rotaRepository.delete(rota);
    }

    @Transactional
    public void alterarStatus(Long id) {
        Rota rota = rotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Rota não encontrada."));

        if (!rota.isAtivo() && !rota.getPontoColetaDestino().isAtivo()) {
            throw new RecursoNaoEncontradoException("Não é possível ativar esta rota pois o Ponto de Coleta de destino está inativo.");
        }

        rota.setAtivo(!rota.isAtivo());
        rotaRepository.save(rota);
    }
}
