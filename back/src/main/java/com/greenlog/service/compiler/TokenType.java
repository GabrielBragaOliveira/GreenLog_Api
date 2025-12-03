/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.greenlog.service.compiler;

/**
 *
 * @author Cansei2
 */
public enum TokenType {
    // Literals
    IDENT,
    STRING,
    NUMBER,
    BOOLEAN,
    
    // Operadores
    EQ,
    NE,
    GT,
    LT,
    GE,
    LE,
    
    // Lógicos
    AND,
    OR,
    
    // Pontuação
    LPAREN,
    RPAREN,
    DOT,

    EOF
}
