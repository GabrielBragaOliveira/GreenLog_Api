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
import java.time.LocalDate;
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

        if (request.data().isBefore(LocalDate.now())) {
            throw new RegraDeNegocioException("A data do itinerário não pode ser anterior à data de hoje.");
        }

        Caminhao caminhao = caminhaoService.buscarEntityPorId(request.caminhaoId());
        Rota rota = rotaService.buscarEntityPorId(request.rotaId());
        TipoResiduo tipoResiduo = tipoResiduoService.buscarEntityPorId(request.tipoResiduoId());

        if (!caminhao.getAtivo()) {
            throw new RegraDeNegocioException("Caminhão inativo.");
        }
        
        if (!rota.isAtivo()) {
            throw new RegraDeNegocioException("A rota selecionada está inativa.");
        }

        if (itinerarioRepository.findByCaminhaoAndData(caminhao, request.data()).isPresent()) {
            throw new RegraDeNegocioException("O caminhão " + caminhao.getPlaca() + " já possui um itinerário agendado para esta data.");
        }

        if (!caminhao.getTiposSuportados().contains(tipoResiduo)) {
            throw new RegraDeNegocioException(
                    "O caminhão " + caminhao.getPlaca() + " não suporta o tipo de resíduo selecionado: " + tipoResiduo.getNome()
            );
        }
        if (!rota.getPontoColetaDestino().getTiposResiduosAceitos().contains(tipoResiduo)) {
            throw new RegraDeNegocioException(
                    "O Ponto de Coleta de destino (" + rota.getPontoColetaDestino().getNomePonto() + 
                    ") não aceita o tipo de resíduo selecionado: " + tipoResiduo.getNome()
            );
        }

        Itinerario novoItinerario = itinerarioMapper.toEntity(request);
        novoItinerario.setCaminhao(caminhao);
        novoItinerario.setRota(rota);
        novoItinerario.setTipoResiduo(tipoResiduo);
        return itinerarioMapper.toResponseDTO(itinerarioRepository.save(novoItinerario));
    }

    @Transactional
    public ItinerarioResponseDTO atualizar(Long id, ItinerarioRequestDTO request) {
        Itinerario itinerarioExistente = buscarEntityPorId(id);

        if (!request.data().equals(itinerarioExistente.getData()) && request.data().isBefore(LocalDate.now())) {
            throw new RegraDeNegocioException("A nova data do itinerário não pode ser anterior à data de hoje.");
        }

        Caminhao novoCaminhao = caminhaoService.buscarEntityPorId(request.caminhaoId());
        Rota novaRota = rotaService.buscarEntityPorId(request.rotaId());
        TipoResiduo novoTipoResiduo = tipoResiduoService.buscarEntityPorId(request.tipoResiduoId());

        if (!itinerarioExistente.getCaminhao().getId().equals(novoCaminhao.getId()) || !itinerarioExistente.getData().equals(request.data())) {
            if (itinerarioRepository.findByCaminhaoAndData(novoCaminhao, request.data()).filter(i -> !i.getId().equals(id)).isPresent()) {
                throw new RegraDeNegocioException("O caminhão " + novoCaminhao.getPlaca() + " já possui outro itinerário agendado para esta data.");
            }
        }
        
        if (!novoCaminhao.getTiposSuportados().contains(novoTipoResiduo)) {
            throw new RegraDeNegocioException("O caminhão não suporta este tipo de resíduo.");
        }
        
        if (!novaRota.getPontoColetaDestino().getTiposResiduosAceitos().contains(novoTipoResiduo)) {
             throw new RegraDeNegocioException("O Ponto de Coleta de destino da nova rota não aceita este tipo de resíduo.");
        }

        itinerarioMapper.updateEntityFromDTO(request, itinerarioExistente);
        itinerarioExistente.setCaminhao(novoCaminhao);
        itinerarioExistente.setRota(novaRota);
        itinerarioExistente.setTipoResiduo(novoTipoResiduo);
        return itinerarioMapper.toResponseDTO(itinerarioRepository.save(itinerarioExistente));
    }

    @Transactional
    public void excluir(Long id) {
        Itinerario itinerario = buscarEntityPorId(id);
        itinerarioRepository.delete(itinerario);
    }
}