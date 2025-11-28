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
    // AAA-9999 ou ABC1D23
    public static final String PLACA_REGEX = "^[A-Z]{3}-?\\d{4}|^[A-Z]{3}[0-9]{1}[A-Z]{1}[0-9]{2}$";
    
    // (99) 99999-9999 ou 9999-9999
    public static final String TELEFONE_REGEX = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$";
    
    private RegexConstants() {}
}
