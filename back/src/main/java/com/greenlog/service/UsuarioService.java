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
import com.greenlog.exception.ConflitoException;
import com.greenlog.exception.ErroValidacaoException;
import com.greenlog.exception.RecursoNaoEncontradoException;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.mapper.UsuarioMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import com.greenlog.service.template.ProcessadorCadastroUsuario;
import com.greenlog.util.HashUtil;
import com.greenlog.util.ValidadorRegexSingleton;
import java.util.Optional;
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
    @Autowired
    private BuscaAvancadaService buscaAvancadaService;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarAvancado(String query) {
        List<Usuario> resultados;

        if (query == null || query.isBlank()) {
            resultados = usuarioRepository.findAll();
        } else {
            resultados = buscaAvancadaService.executarBusca(query, usuarioRepository);
        }

        return resultados.stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

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

        if (request.senha() == null || request.senha().isBlank()) {
            throw new ErroValidacaoException("A senha do usuário é obrigatória.");
        }

        if (!ValidadorRegexSingleton.getInstance().isSenhaValida(request.senha())) {
            throw new RegraDeNegocioException("Erro: Formato da Senha inválido. Ultilizar pelo menos um maiusculo, um minusculo, um numero e um caractere especial");
        }
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(request.email());

        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();

            if (!usuario.isAtivo()) {
                usuario.setNome(request.nome());
                usuario.setSenha(HashUtil.hash(request.senha()));
                usuario.setPerfil(request.perfil());
                usuario.setAtivo(true);

                Usuario usuarioSalvo = usuarioRepository.save(usuario);
                return usuarioMapper.toResponseDTO(usuarioSalvo);
            } else throw new ConflitoException("Já existe um usuário ativo com este e-mail.");
        }

        Usuario novoUsuario = usuarioMapper.toEntity(request);

        novoUsuario.setSenha(HashUtil.hash(request.senha()));
        novoUsuario.setAtivo(true);
        Usuario usuarioSalvo = processadorCadastroUsuario.processar(novoUsuario);

        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO request) {
        Usuario usuarioExistente = buscarEntityPorId(id);

        if (request.senha() != null && !request.senha().isEmpty()) {
            if (!ValidadorRegexSingleton.getInstance().isSenhaValida(request.senha())) throw new RegraDeNegocioException("Erro: Formato da Senha inválido...");
            usuarioExistente.setSenha(HashUtil.hash(request.senha()));
        }

        usuarioMapper.updateEntityFromDTO(request, usuarioExistente);
        if (!usuarioExistente.isAtivo()) throw new ErroValidacaoException("Não é possível atualizar inativo.");

        Usuario salvo = processadorCadastroUsuario.processar(usuarioExistente);
        return usuarioMapper.toResponseDTO(salvo);
    }

    @Transactional
    public void alterarStatus(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario não encontrado."));

        if (usuario.isAtivo()) {
            usuario.setAtivo(false);
        } else {
            usuario.setAtivo(true);
        }

        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com este e-mail."));
        if (!usuario.getSenha().equals(request.senha())) {
            if (!HashUtil.matches(request.senha(), usuario.getSenha())) {
                throw new RegraDeNegocioException("Senha incorreta.");
            }
        }
        return usuarioMapper.toResponseDTO(usuario);
    }
}
