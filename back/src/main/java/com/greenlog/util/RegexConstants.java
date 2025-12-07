/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.util;

/**
 *
 * @author Kayqu
 */
public final class RegexConstants {

    public static final String PLACA_REGEX = "^[A-Z]{3}-?\\d{4}|^[A-Z]{3}[0-9]{1}[A-Z]{1}[0-9]{2}$";

    public static final String TELEFONE_REGEX = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$";

    public static final String SENHA_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{9,}$";

    public static final String NOME_REGEX = "^(?!.*\\s\\s)[a-zA-Z\\u00C0-\\u00FF][a-zA-Z\\u00C0-\\u00FF ]{1,}[a-zA-Z\\u00C0-\\u00FF]$";
    
    public static final String CAPACIDADE_REGEX = "^\\d{1,7}$";

    public static final String NOME_NUMERO = "^(?!.*\\s\\s)[a-zA-Z0-9\\u00C0-\\u00FF][a-zA-Z0-9\\u00C0-\\u00FF ]{1,}[a-zA-Z0-9\\u00C0-\\u00FF]$";

    private RegexConstants() {
    }
}
