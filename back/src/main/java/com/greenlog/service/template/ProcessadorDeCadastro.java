/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.template;

/**
 *
 * @author Kayqu
 */
public abstract class ProcessadorDeCadastro<T> {

    // O método template: Define a ordem fixa das etapas
    public final T processar(T entidade) {
        validarRegrasEspecificas(entidade);
        T entidadeSalva = salvarNoBanco(entidade);
        notificarSucesso(entidadeSalva);
        return entidadeSalva;
    }

    // Etapas abstratas que devem ser implementadas pelas subclasses
    protected abstract void validarRegrasEspecificas(T entidade);
    protected abstract T salvarNoBanco(T entidade);
    
    // Hook/Etapa padrão com implementação opcional ou genérica
    protected void notificarSucesso(T entidadeSalva) {
        System.out.println("Cadastro de " + entidadeSalva.getClass().getSimpleName() + " concluído e salvo.");
    }
}
