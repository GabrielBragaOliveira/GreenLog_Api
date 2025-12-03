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
import com.greenlog.exception.ConflitoException;
import com.greenlog.exception.ErroValidacaoException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.greenlog.service.template.ProcessadorCadastroCaminhao;
import java.util.Optional;
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
        if (request.placa() == null || request.placa().isBlank()) {
            throw new ErroValidacaoException("A placa do caminhão é obrigatória.");
        }
        if (request.motorista() == null || request.motorista().isBlank()) {
            throw new ErroValidacaoException("O nome do motorista é obrigatório.");
        }
        if (request.capacidadeKg() == null || request.capacidadeKg() <= 0) {
            throw new ErroValidacaoException("A capacidade deve ser maior que zero.");
        }
        if (request.tiposSuportadosIds() == null || request.tiposSuportadosIds().isEmpty()) {
            throw new ErroValidacaoException("O caminhão deve suportar pelo menos um tipo de resíduo.");
        }

        Optional<Caminhao> existente = caminhaoRepository.findByPlaca(request.placa());

        if (existente.isPresent()) {
            Caminhao caminhao = existente.get();

            if (!caminhao.isAtivo()) {
                caminhao.setPlaca(request.placa());
                caminhao.setMotorista(request.motorista());
                caminhao.setCapacidadeKg(request.capacidadeKg());
                caminhao.setTiposSuportados(
                        request.tiposSuportadosIds().stream()
                                .map(tipoResiduoService::buscarEntityPorId)
                                .collect(Collectors.toList())
                );
                caminhao.setAtivo(true);

                Caminhao salvo = caminhaoRepository.save(caminhao);
                return caminhaoMapper.toResponseDTO(salvo);

            } else {
                throw new ConflitoException("Já existe um caminhão ativo com esta placa.");
            }
        }

        Caminhao novoCaminhao = caminhaoMapper.toEntity(request);
        novoCaminhao.setTiposSuportados(
                request.tiposSuportadosIds().stream()
                        .map(tipoResiduoService::buscarEntityPorId)
                        .collect(Collectors.toList())
        );
        novoCaminhao.setAtivo(true);

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
    public void alternarStatus(Long id) {
        Caminhao caminhao = caminhaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Caminhão não encontrado."));
        if (caminhao.getAtivo()) {
            caminhao.setAtivo(false);

        } else {
            caminhao.setAtivo(true);
        }
        caminhaoRepository.save(caminhao);
    }
}