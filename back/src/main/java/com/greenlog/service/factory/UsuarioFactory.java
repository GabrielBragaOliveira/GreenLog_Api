/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.factory;

import com.greenlog.domain.entity.Usuario;
import com.greenlog.enums.Perfil;

/**
 *
 * @author Kayqu
 */
public class UsuarioFactory {

    public static Usuario criarNovoUsuario(String nome, String email, String senhaCriptografada, Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senhaCriptografada);
        usuario.setPerfil(perfil);
        return usuario;
    }
}
