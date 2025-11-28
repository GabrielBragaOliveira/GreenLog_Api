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

    private ValidadorRegexSingleton() {
        this.placaPattern = Pattern.compile(RegexConstants.PLACA_REGEX);
        this.telefonePattern = Pattern.compile(RegexConstants.TELEFONE_REGEX);
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
}
