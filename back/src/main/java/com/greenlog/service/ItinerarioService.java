/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.domain.dto.ItinerarioRequestDTO;
import com.greenlog.domain.dto.ItinerarioResponseDTO;
import com.greenlog.domain.entity.Caminhao;
import com.greenlog.domain.entity.Itinerario;
import com.greenlog.domain.entity.Rota;
import com.greenlog.domain.entity.TipoResiduo;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.mapper.ItinerarioMapper;
import com.greenlog.domain.repository.ItinerarioRepository;
import com.greenlog.enums.StatusItinerarioEnum;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kayqu
 */
@Service
public class ItinerarioService {

    @Autowired
    private ItinerarioRepository itinerarioRepository;
    @Autowired
    private CaminhaoService caminhaoService;
    @Autowired
    private RotaService rotaService;
    @Autowired
    private TipoResiduoService tipoResiduoService;
    @Autowired
    private PontoColetaService pontoColetaService;
    @Autowired
    private ItinerarioMapper itinerarioMapper;
    @Autowired
    private BuscaAvancadaService buscaAvancadaService;

    @Transactional(readOnly = true)
    public List<ItinerarioResponseDTO> buscarAvancado(String query) {
        List<Itinerario> resultados;

        if (query == null || query.isBlank()) {
            resultados = itinerarioRepository.findAll();
        } else {
            resultados = buscaAvancadaService.executarBusca(query, itinerarioRepository);
        }

        return resultados.stream()
                .map(itinerarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItinerarioResponseDTO> listar() {
        return itinerarioRepository.findAll().stream()
                .map(itinerarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Itinerario buscarEntityPorId(Long id) {
        return itinerarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Itinerário não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public ItinerarioResponseDTO buscarPorId(Long id) {
        return itinerarioMapper.toResponseDTO(buscarEntityPorId(id));
    }

    @Transactional
    public ItinerarioResponseDTO salvar(ItinerarioRequestDTO request) {
        Caminhao caminhao = caminhaoService.buscarEntityPorId(request.caminhaoId());
        Rota rota = rotaService.buscarEntityPorId(request.rotaId());

        if (!caminhao.isAtivo()) {
            throw new RegraDeNegocioException(
                    "O caminhão " + caminhao.getPlaca() + " está inativo e não pode ser utilizado em itinerários."
            );
        }

        if (itinerarioRepository.findByCaminhaoAndData(caminhao, request.data()).isPresent()) {
            throw new RegraDeNegocioException("O caminhão " + caminhao.getPlaca() + " já possui um itinerário agendado para esta data.");
        }

        Itinerario novoItinerario = itinerarioMapper.toEntity(request);
        novoItinerario.setCaminhao(caminhao);
        novoItinerario.setRota(rota);
        novoItinerario.setTiposResiduosAceitos(
                request.tiposResiduosIds().stream()
                        .map(tipoResiduoService::buscarEntityPorId)
                        .collect(Collectors.toList())
        );
        novoItinerario.setStatusItinerarioEnum(StatusItinerarioEnum.PENDENTE);

        return itinerarioMapper.toResponseDTO(itinerarioRepository.save(novoItinerario));
    }

    @Transactional
    public ItinerarioResponseDTO atualizar(Long id, ItinerarioRequestDTO request) {
        Itinerario itinerarioExistente = buscarEntityPorId(id);

        Caminhao novoCaminhao = caminhaoService.buscarEntityPorId(request.caminhaoId());
        Rota novaRota = rotaService.buscarEntityPorId(request.rotaId());

        if (!itinerarioExistente.getCaminhao().getId().equals(novoCaminhao.getId()) || !itinerarioExistente.getData().equals(request.data())) {
            if (itinerarioRepository.findByCaminhaoAndData(novoCaminhao, request.data()).filter(i -> !i.getId().equals(id)).isPresent()) {
                throw new RegraDeNegocioException("O caminhão " + novoCaminhao.getPlaca() + " já possui outro itinerário agendado para esta data.");
            }
        }

        itinerarioMapper.updateEntityFromDTO(request, itinerarioExistente);
        itinerarioExistente.setCaminhao(novoCaminhao);
        itinerarioExistente.setRota(novaRota);
        return itinerarioMapper.toResponseDTO(itinerarioRepository.save(itinerarioExistente));
    }

    @Transactional
    public void excluir(Long id) {
        Itinerario itinerario = buscarEntityPorId(id);
        itinerarioRepository.delete(itinerario);
    }

    private void validarCompatibilidadeCaminhaoRota(Caminhao caminhao, Rota rota) {
        Set<TipoResiduo> tiposSuportadosCaminhao = caminhao.getTiposSuportados().stream().collect(Collectors.toSet());

        Set<TipoResiduo> tiposRequeridosNaRota = rota.getListaDeBairros().stream()
                .flatMap(bairro -> pontoColetaService.buscarPontosPorBairro(bairro).stream())
                .flatMap(ponto -> ponto.getTiposResiduosAceitos().stream())
                .collect(Collectors.toSet());

        boolean compativel = tiposRequeridosNaRota.stream()
                .allMatch(tiposSuportadosCaminhao::contains);

        if (!compativel) {
            String tiposNaoSuportados = tiposRequeridosNaRota.stream()
                    .filter(tipo -> !tiposSuportadosCaminhao.contains(tipo))
                    .map(TipoResiduo::getNome)
                    .collect(Collectors.joining(", "));

            throw new RegraDeNegocioException(
                    String.format("O caminhão (%s) não suporta os tipos de resíduo exigidos pela rota. Resíduos não suportados: %s",
                            caminhao.getPlaca(), tiposNaoSuportados)
            );
        }
    }
}
