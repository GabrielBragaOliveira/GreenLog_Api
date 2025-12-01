/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.exception;

/**
 *
 * @author GabrielB
 */
public class ConflitoException extends RuntimeException {

    //Erro 409
    //Usado quando existe conflito de estado, ex: email já cadastrado.
    public ConflitoException(String message) {
        super(message);
    }

    public ConflitoException(String message, Throwable cause) {
        super(message, cause);
    }
}