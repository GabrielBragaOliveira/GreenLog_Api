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
        
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Erro: O email já está em uso.");
        }
        
    }

    @Override
    protected Usuario salvarNoBanco(Usuario usuario) {
        // O Service já teria criptografado a senha antes de chamar o Template
        return usuarioRepository.save(usuario);
    }
    
    @Override
    protected void notificarSucesso(Usuario usuarioSalvo) {
        System.out.println("Usuário " + usuarioSalvo.getNome() + " cadastrado com sucesso.");
        // Implementação real: Enviar email de boas-vindas
    }
}
