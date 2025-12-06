/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.domain.dto.CaminhaoRequestDTO;
import com.greenlog.domain.dto.CaminhaoResponseDTO;
import com.greenlog.domain.entity.Bairro;
import com.greenlog.domain.entity.Caminhao;
import com.greenlog.domain.entity.PontoColeta;
import com.greenlog.domain.entity.Rota;
import com.greenlog.domain.entity.TipoResiduo;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.mapper.CaminhaoMapper;
import com.greenlog.domain.repository.CaminhaoRepository;
import com.greenlog.exception.ConflitoException;
import com.greenlog.exception.ErroValidacaoException;
import com.greenlog.exception.RegraDeNegocioException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.greenlog.service.template.ProcessadorCadastroCaminhao;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
    private TipoResiduoService tipoResiduoService;
    @Autowired
    private CaminhaoMapper caminhaoMapper;
    @Autowired
    private ProcessadorCadastroCaminhao processadorCadastroCaminhao;
    @Autowired
    private BuscaAvancadaService buscaAvancadaService;
    @Autowired
    private RotaService rotaService;
    @Autowired
    private PontoColetaService pontoColetaService;

    @Transactional(readOnly = true)
    public List<CaminhaoResponseDTO> buscarAvancado(String query) {
        List<Caminhao> resultados;

        if (query == null || query.isBlank()) {
            resultados = caminhaoRepository.findAll();
        } else {
            resultados = buscaAvancadaService.executarBusca(query, caminhaoRepository);
        }

        return resultados.stream()
                .map(caminhaoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

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
                caminhao.setTiposSuportados(buscarTiposResiduoAtivos(request.tiposSuportadosIds()));
                caminhao.setAtivo(true);

                Caminhao salvo = caminhaoRepository.save(caminhao);
                return caminhaoMapper.toResponseDTO(salvo);

            } else {
                throw new ConflitoException("Já existe um caminhão ativo com esta placa.");
            }
        }

        Caminhao novoCaminhao = caminhaoMapper.toEntity(request);
        novoCaminhao.setTiposSuportados(buscarTiposResiduoAtivos(request.tiposSuportadosIds()));
        novoCaminhao.setAtivo(true);

        Caminhao caminhaoSalvo = processadorCadastroCaminhao.processar(novoCaminhao);

        return caminhaoMapper.toResponseDTO(caminhaoSalvo);
    }

    @Transactional
    public CaminhaoResponseDTO atualizar(Long id, CaminhaoRequestDTO request) {
        Caminhao caminhaoExistente = buscarEntityPorId(id);
        caminhaoMapper.updateEntityFromDTO(request, caminhaoExistente);
        caminhaoExistente.setTiposSuportados(buscarTiposResiduoAtivos(request.tiposSuportadosIds()));

        return caminhaoMapper.toResponseDTO(caminhaoRepository.save(caminhaoExistente));
    }

    @Transactional
    public void alternarStatus(Long id) {
        Caminhao caminhao = caminhaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Caminhão não encontrado."));

        boolean novoStatus = !caminhao.getAtivo();

        if (novoStatus) {
            boolean possuiTiposInativos = false;

            for (TipoResiduo t : caminhao.getTiposSuportados()) {
                if (!t.isAtivo()) {
                    possuiTiposInativos = true;
                    break;
                }
            }

            if (possuiTiposInativos) {
                throw new RegraDeNegocioException(
                        "Não é possível ativar o caminhão: ele possui tipos de resíduo inativos."
                );
            }
        }

        caminhao.setAtivo(novoStatus);
        caminhaoRepository.save(caminhao);
    }

    @Transactional(readOnly = true)
    public List<CaminhaoResponseDTO> buscarCompativeisComRota(Long rotaId) {
        Rota rota = rotaService.buscarEntityPorId(rotaId);

        Set<TipoResiduo> residuosDaRota = new HashSet<>();
        for (Bairro bairro : rota.getListaDeBairros()) {
            List<PontoColeta> pontos = pontoColetaService.buscarPontosPorBairro(bairro);
            for (PontoColeta ponto : pontos) {
                residuosDaRota.addAll(ponto.getTiposResiduosAceitos());
            }
        }

        if (residuosDaRota.isEmpty()) {
            return Collections.emptyList();
        }

        List<Caminhao> caminhõesAtivos = caminhaoRepository.findByAtivo(true);

        return caminhõesAtivos.stream()
                .filter(c -> !Collections.disjoint(c.getTiposSuportados(), residuosDaRota))
                .map(caminhaoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    private List<TipoResiduo> buscarTiposResiduoAtivos(List<Long> ids) {
    return ids.stream()
            .map(tipoResiduoService::buscarEntityPorId)
            .peek(tipo -> {
                if (!tipo.isAtivo()) {
                    throw new ErroValidacaoException("O tipo de resíduo '" + tipo.getNome() + "' está inativo e não pode ser vinculado ao caminhão.");
                }
            })
            .collect(Collectors.toList());
}
}
