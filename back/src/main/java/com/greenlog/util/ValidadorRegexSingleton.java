/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Kayqu
 */
public final class ValidadorRegexSingleton {
    
    private static final ValidadorRegexSingleton INSTANCE = new ValidadorRegexSingleton();
    
    private final Pattern placaPattern;
    private final Pattern telefonePattern;
    private final Pattern senhaPattern;
    private final Pattern nomePattern;
    private final Pattern capacidadePattern;
    private final Pattern nomeNumeroPattern;
    private final Pattern rotaPattern;

    private ValidadorRegexSingleton() {
        this.placaPattern = Pattern.compile(RegexConstants.PLACA_REGEX);
        this.telefonePattern = Pattern.compile(RegexConstants.TELEFONE_REGEX);
        this.senhaPattern = Pattern.compile(RegexConstants.SENHA_REGEX);
        this.nomePattern = Pattern.compile(RegexConstants.NOME_REGEX);
        this.capacidadePattern = Pattern.compile(RegexConstants.CAPACIDADE_REGEX);
        this.nomeNumeroPattern = Pattern.compile(RegexConstants.NOME_NUMERO);
        this.rotaPattern = Pattern.compile(RegexConstants.NOME_ROTA);
    }
    
    public static ValidadorRegexSingleton getInstance() {
        return INSTANCE;
    }

    public boolean isPlacaValida(String placa) {
        if (placa == null) return false;
        Matcher matcher = placaPattern.matcher(placa);
        return matcher.matches();
    }

    public boolean isTelefoneValido(String telefone) {
        if (telefone == null) return false;
        Matcher matcher = telefonePattern.matcher(telefone);
        return matcher.matches();
    }
    
    public boolean isSenhaValida(String senha){
        if (senha == null) return false;
        Matcher matcher = senhaPattern.matcher(senha);
        return matcher.matches();
    }
    
    public boolean isNomeValida(String nome){
        if (nome == null) return false;
        Matcher matcher = nomePattern.matcher(nome);
        return matcher.matches();
    }
    
    public boolean isCapacidadeValida(Integer valor) {
        if (valor == null) return false;
        Matcher matcher = capacidadePattern.matcher(String.valueOf(valor));
        return matcher.matches();
    }
    
    public boolean isNomeENumeroValida(String entrada){
        if (entrada == null) return false;
        Matcher matcher = nomeNumeroPattern.matcher(entrada);
        return matcher.matches();
    }
    
    public boolean isRotaValida(String entrada){
        if (entrada == null) return false;
        Matcher matcher = rotaPattern.matcher(entrada);
        return matcher.matches();
    }
}
