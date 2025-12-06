/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * @author Kayqu
 */
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    // Tratamento para Recurso Não Encontrado (404 Not Found)
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Object> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex, WebRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }

    // Tratamento para Regras de Negócio (400 Bad Request)
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Object> handleRegraDeNegocio(RegraDeNegocioException ex, WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }

    // Tratamento para Entidades em Uso (409 Conflict)
    @ExceptionHandler(EntidadeEmUsoException.class)
    public ResponseEntity<Object> handleEntidadeEmUso(EntidadeEmUsoException ex, WebRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }

    // Tratamento para Erros de Validação (422 Unprocessable Entity)
    @ExceptionHandler(ErroValidacaoException.class)
    public ResponseEntity<Object> handleErroValidacao(ErroValidacaoException ex, WebRequest request) {

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // 422

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }

    // Tratamento para Conflitos (409 Conflict)
    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<Object> handleConflito(ConflitoException ex, WebRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }
    
    @ExceptionHandler(QueryInvalidaException.class)
    public ResponseEntity<Object> handleQueryInvalida(QueryInvalidaException ex, WebRequest request) {
        
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("erro", "Erro de Sintaxe na Busca");
        body.put("mensagem", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }
    
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
        HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        FieldError firstError = ex.getBindingResult().getFieldError();
        String mensagemErro = (firstError != null) ? firstError.getDefaultMessage() : "Erro de validação";

        HttpStatus code = HttpStatus.BAD_REQUEST; 

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", code.value());
        
        body.put("mensagem", mensagemErro); 

        return new ResponseEntity<>(body, code);
    }
}
