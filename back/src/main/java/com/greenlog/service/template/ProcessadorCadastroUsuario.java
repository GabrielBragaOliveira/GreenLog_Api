/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.template;

import com.greenlog.domain.entity.Usuario;
import com.greenlog.domain.repository.UsuarioRepository;
import com.greenlog.exception.RegraDeNegocioException;
import com.greenlog.util.ValidadorRegexSingleton;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kayqu
 */
@Component
public class ProcessadorCadastroUsuario extends ProcessadorDeCadastro<Usuario> {

    private final UsuarioRepository usuarioRepository;

    public ProcessadorCadastroUsuario(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void validarRegrasEspecificas(Usuario usuario) {
        
        if (usuario.getNome().trim() == null || usuario.getNome().trim().isBlank()) throw new RegraDeNegocioException("O nome do usuário é obrigatório.");
        if (usuario.getEmail().trim() == null || usuario.getEmail().trim().isBlank()) throw new RegraDeNegocioException("O email do usuário é obrigatório.");
       
        if (!ValidadorRegexSingleton.getInstance().isNomeValida(usuario.getNome().trim())) throw new RegraDeNegocioException("Erro: Nome do Usuario inválido. Deve conter apenas letras e ter no mínimo 3 caracteres.");
        if (usuarioRepository.existsByEmail(usuario.getEmail().trim())) throw new IllegalArgumentException("Erro: O email já está em uso.");  
        
    }

    @Override
    protected Usuario salvarNoBanco(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    @Override
    protected void notificarSucesso(Usuario usuarioSalvo) {
        System.out.println("Usuário " + usuarioSalvo.getNome() + " cadastrado com sucesso.");
    }
}
