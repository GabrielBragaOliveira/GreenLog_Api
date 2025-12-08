/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.domain.dto.ConexaoBairroRequestDTO;
import com.greenlog.domain.dto.ConexaoBairroResponseDTO;
import com.greenlog.domain.entity.Bairro;
import com.greenlog.domain.entity.ConexaoBairro;
import com.greenlog.domain.entity.Itinerario;
import com.greenlog.domain.entity.Rota;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.mapper.ConexaoBairroMapper;
import com.greenlog.domain.repository.ConexaoBairroRepository;
import com.greenlog.domain.repository.ItinerarioRepository;
import com.greenlog.domain.repository.RotaRepository;
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
public class ConexaoBairroService {

    @Autowired
    private ConexaoBairroRepository conexaoBairroRepository;
    @Autowired
    private BairroService bairroService;
    @Autowired
    private ItinerarioRepository itinerarioRepository;
    @Autowired
    private ConexaoBairroMapper conexaoBairroMapper;
    @Autowired
    private BuscaAvancadaService buscaAvancadaService;
    @Autowired
    private RotaRepository rotaRepository;

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
        else {
            validarUsoEmItinerariosFuturos(conexao);
            inativarRotasDependentes(conexao);
        }

        conexao.setAtivo(!conexao.isAtivo());
        conexaoBairroRepository.save(conexao);
    }
    
    private void validarUsoEmItinerariosFuturos(ConexaoBairro conexao) {
        List<Itinerario> itinerariosFuturos = itinerarioRepository.findByDataGreaterThanEqual(LocalDate.now());

        for (Itinerario itinerario : itinerariosFuturos) {
            if (rotaUtilizaConexao(itinerario.getRota(), conexao)) {
                throw new RegraDeNegocioException(
                        "Não é possível inativar esta conexão/rua. Ela faz parte da rota '" + itinerario.getRota().getNome() +
                        "' que possui itinerário agendado para " + itinerario.getData()
                );
            }
        }
    }

    private void inativarRotasDependentes(ConexaoBairro conexao) {
        List<Rota> rotasAtivas = rotaRepository.findByAtivoTrue(); 

        for (Rota rota : rotasAtivas) {
            if (rotaUtilizaConexao(rota, conexao)) {
                rota.setAtivo(false);
                rotaRepository.save(rota);
                System.out.println("LOG: Rota " + rota.getNome() + " inativada automaticamente pois a conexão (" 
                        + conexao.getBairroOrigem().getNome() + " -> " + conexao.getBairroDestino().getNome() + ") foi inativada.");
            }
        }
    }

    private boolean rotaUtilizaConexao(Rota rota, ConexaoBairro conexao) {
        List<Bairro> bairrosDaRota = rota.getListaDeBairros();
        if (bairrosDaRota == null || bairrosDaRota.size() < 2) return false;

        for (int i = 0; i < bairrosDaRota.size() - 1; i++) {
            Bairro atual = bairrosDaRota.get(i);
            Bairro proximo = bairrosDaRota.get(i + 1);

            if (atual.getId().equals(conexao.getBairroOrigem().getId()) &&
                proximo.getId().equals(conexao.getBairroDestino().getId())) {
                return true;
            }
        }
        return false;
    }
}