/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.compiler;

import com.greenlog.exception.QueryInvalidaException;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cansei2
 */
public class QueryParser<T> {

    private final List<Token> tokens;
    private int index = 0;

    public QueryParser(List<Token> tokens) {
        this.tokens = tokens != null ? tokens : new ArrayList<>();
    }

    public Specification<T> parse() {
        index = 0;
        Specification<T> spec = parseExpression();
        expect(TokenType.EOF, "Esperado fim da expressão");
        return spec;
    }

    private Specification<T> parseExpression() {
        Specification<T> left = parseTerm();

        while (match(TokenType.AND) || match(TokenType.OR)) {
            Token op = previous();
            Specification<T> right = parseTerm();

            if (op.getType() == TokenType.AND) left = and(left, right);
            else left = or(left, right);
        }
        return left;
    }

    private Specification<T> parseTerm() {
        if (match(TokenType.LPAREN)) {
            Specification<T> expr = parseExpression();
            expect(TokenType.RPAREN, "Esperado ')'");
            return expr;
        }
        return parseComparison();
    }

    private Specification<T> parseComparison() {
        String field = parseField();

        Token operator = consumeAny("Esperado operador",
                TokenType.EQ, TokenType.NE, TokenType.GT, TokenType.LT, TokenType.GE, TokenType.LE);

        Token value = consumeAny("Esperado valor (texto, número ou booleano)", 
                TokenType.STRING, TokenType.NUMBER, TokenType.BOOLEAN);

        return buildPredicate(field, operator, value);
    }

    private Specification<T> buildPredicate(String field, Token op, Token value) {
        return (root, query, cb) -> {

            Path<?> rawPath;
            try {
                 rawPath = navigatePath(root, field);
            } catch (Exception e) {
                throw new QueryInvalidaException("Erro ao acessar campo: " + field);
            }

            switch (value.getType()) {
                
                case BOOLEAN: {
                    Boolean boolVal = Boolean.valueOf(value.getLexeme());
                    Expression<Boolean> p = rawPath.as(Boolean.class);

                    return switch (op.getType()) {
                        case EQ -> cb.equal(p, boolVal);
                        case NE -> cb.notEqual(p, boolVal);
                        default -> throw new QueryInvalidaException("Operador inválido para BOOLEAN (use apenas = ou !=)");
                    };
                }
                
                case STRING: {
                    String lit = value.getLexeme();
                    Expression<String> p = rawPath.as(String.class);

                    return switch (op.getType()) {
                        case EQ -> cb.equal(p, lit);
                        case NE -> cb.notEqual(p, lit);
                        case GT -> cb.greaterThan(p, lit);
                        case LT -> cb.lessThan(p, lit);
                        case GE -> cb.greaterThanOrEqualTo(p, lit);
                        case LE -> cb.lessThanOrEqualTo(p, lit);
                        default -> throw new RuntimeException("Operador inválido para STRING");
                    };
                }

                case NUMBER: {
                    Number num = parseNumber(value.getLexeme());
                    Expression<Number> p = rawPath.as(Number.class);

                    return switch (op.getType()) {
                        case EQ -> cb.equal(p, num);
                        case NE -> cb.notEqual(p, num);
                        case GT -> cb.gt(p, num);
                        case LT -> cb.lt(p, num);
                        case GE -> cb.ge(p, num);
                        case LE -> cb.le(p, num);
                        default -> throw new RuntimeException("Operador inválido para NUMBER");
                    };
                }
            }

            throw new RuntimeException("Tipo de valor não suportado");
        };
    }

    private String parseField() {
        Token ident = consumeAny("Esperado identificador", TokenType.IDENT);
        StringBuilder sb = new StringBuilder(ident.getLexeme());

        while (match(TokenType.DOT)) {
            Token next = consumeAny("Esperado identificador após '.'", TokenType.IDENT);
            sb.append(".").append(next.getLexeme());
        }
        return sb.toString();
    }

    private Path<?> navigatePath(Root<T> root, String field) {
        Path<?> p = root;
        
        for (String part : field.split("\\.")) {
            try {
                p = p.get(part);
            } catch (IllegalArgumentException | IllegalStateException e) {
                throw new QueryInvalidaException(
                    String.format("O campo '%s' não existe na busca solicitada (ou o caminho '%s' está incorreto). Verifique se digitou o nome do atributo corretamente.", part, field)
                );
            }
        }
        return p;
    }

    // helpers de Specification
    private Specification<T> and(Specification<T> a, Specification<T> b) { return a == null ? b : a.and(b); }
    private Specification<T> or(Specification<T> a, Specification<T> b) { return a == null ? b : a.or(b); }

    // helpers de tokens
    private boolean match(TokenType type) {
        if (check(type)) { advance(); return true; }
        return false;
    }

    private Token consumeAny(String msg, TokenType... types) {
        for (TokenType t : types) {
            if (check(t)) return advance();
        }
        throw new QueryInvalidaException(
            String.format("Erro de Sintaxe: %s. Mas foi encontrado: '%s' (%s)", 
                msg, 
                peek().getLexeme(), 
                traduzirTipo(peek().getType()))
        );
    }

    private void expect(TokenType type, String msg) {
        if (!check(type)) throw new RuntimeException(msg + " — encontrado: " + peek().getType());
        advance();
    }
    
    private String traduzirTipo(TokenType type) {
        return switch (type) {
            case EOF -> "Fim da busca";
            case IDENT -> "Texto/Campo";
            case STRING -> "Texto entre aspas";
            case NUMBER -> "Número";
            case EQ, NE, GT, LT, GE, LE -> "Operador lógico";
            default -> type.toString();
        };
    }

    private boolean check(TokenType type) { return peek().getType() == type; }
    private Token advance() { index++; return previous(); }
    private Token peek() { return tokens.get(index); }
    private Token previous() { return tokens.get(index - 1); }
    private boolean isAtEnd() { return peek().getType() == TokenType.EOF; }

    private Number parseNumber(String lexeme) {
        return lexeme.contains(".")
                ? Double.parseDouble(lexeme)
                : Long.parseLong(lexeme);
    }
}


