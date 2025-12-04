/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service;

import com.greenlog.service.compiler.Lexer;
import com.greenlog.service.compiler.QueryParser;
import com.greenlog.service.compiler.Token;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Cansei2
 */

@Service
public class BuscaAvancadaService {
    
    @Transactional(readOnly = true)
    public <T> List<T> executarBusca(String query, JpaSpecificationExecutor<T> repository) {
        if (query == null || query.trim().isEmpty()) {
            return repository.findAll(null); 
        }

        Lexer lexer = new Lexer(query);
        List<Token> tokens = lexer.scan();

        QueryParser<T> parser = new QueryParser<>(tokens);
        Specification<T> spec = parser.parse();

        return repository.findAll(spec);
    }
}
