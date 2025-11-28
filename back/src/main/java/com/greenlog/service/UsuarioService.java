/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.domain.dto.LoginRequestDTO;
import com.greenlog.domain.dto.UsuarioRequestDTO;
import com.greenlog.domain.dto.UsuarioResponseDTO;
import com.greenlog.domain.entity.Usuario;
import com.greenlog.domain.repository.UsuarioRepository;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.mapper.UsuarioMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import com.greenlog.service.template.ProcessadorCadastroUsuario;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Kayqu
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private UsuarioMapper usuarioMapper;
    
    @Autowired
    private ProcessadorCadastroUsuario processadorCadastroUsuario;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntityPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return usuarioMapper.toResponseDTO(buscarEntityPorId(id));
    }

    @Transactional
    public UsuarioResponseDTO salvar(UsuarioRequestDTO request) {
        Usuario novoUsuario = usuarioMapper.toEntity(request);
        novoUsuario.setSenha(request.senha());

        Usuario usuarioSalvo = processadorCadastroUsuario.processar(novoUsuario);

        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO request) {
        Usuario usuarioExistente = buscarEntityPorId(id);
        usuarioMapper.updateEntityFromDTO(request, usuarioExistente);
        if (request.senha()!= null && !request.senha().isEmpty()) {
            usuarioExistente.setSenha(request.senha());
        }

        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuarioExistente));
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarEntityPorId(id);
        usuarioRepository.delete(usuario);
    }
    
    @Transactional(readOnly = true)
    public UsuarioResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com este e-mail."));

        if (!usuario.getSenha().equals(request.senha())) {
            throw new RegraDeNegocioException("Senha incorreta.");
        }

        return usuarioMapper.toResponseDTO(usuario);
    }
}
