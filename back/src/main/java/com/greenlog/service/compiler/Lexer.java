/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.compiler;

import com.greenlog.exception.QueryInvalidaException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cansei2
 */
public class Lexer {

    private final String input;
    private final int length;
    private int pos = 0;

    public Lexer(String input) {
        this.input = input != null ? input : "";
        this.length = this.input.length();
    }

    public List<Token> scan() {
        List<Token> tokens = new ArrayList<>();

        while (!isAtEnd()) {
            char c = peek();

            if (Character.isWhitespace(c)) {
                advance();
                continue;
            }

            if (Character.isLetter(c) || c == '_') {
                tokens.add(readIdentifierOrKeyword());
                continue;
            }

            if (Character.isDigit(c) || (c == '-' && isNextDigit())) {
                tokens.add(readNumber());
                continue;
            }

            if (c == '"') {
                tokens.add(readString());
                continue;
            }

            switch (c) {
                case '=': tokens.add(new Token(TokenType.EQ, "=")); advance(); break;
                case '!':
                    advance();
                    if (peek() == '=') {
                        advance();
                        tokens.add(new Token(TokenType.NE, "!="));
                    } else {
                        throw new QueryInvalidaException("Caractere '!' encontrado sozinho. Você quis dizer '!=' (diferente)?");
                    }
                    break;

                case '>':
                    advance();
                    if (peek() == '=') {
                        advance();
                        tokens.add(new Token(TokenType.GE, ">="));
                    } else tokens.add(new Token(TokenType.GT, ">"));
                    break;

                case '<':
                    advance();
                    if (peek() == '=') {
                        advance();
                        tokens.add(new Token(TokenType.LE, "<="));
                    } else tokens.add(new Token(TokenType.LT, "<"));
                    break;

                case '(':
                    tokens.add(new Token(TokenType.LPAREN, "(")); advance(); break;
                case ')':
                    tokens.add(new Token(TokenType.RPAREN, ")")); advance(); break;
                case '.':
                    tokens.add(new Token(TokenType.DOT, ".")); advance(); break;

                default:
                    throw new QueryInvalidaException("Caractere inválido ou não reconhecido na busca: '" + c + "'");
            }
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private Token readIdentifierOrKeyword() {
        int start = pos;
        while (!isAtEnd() && (Character.isLetterOrDigit(peek()) || peek() == '_')) advance();
        String word = input.substring(start, pos);

        if (word.equalsIgnoreCase("AND")) return new Token(TokenType.AND, word);
        if (word.equalsIgnoreCase("OR")) return new Token(TokenType.OR, word);
        
        if (word.equalsIgnoreCase("TRUE")) return new Token(TokenType.BOOLEAN, "true");
        if (word.equalsIgnoreCase("FALSE")) return new Token(TokenType.BOOLEAN, "false");

        return new Token(TokenType.IDENT, word);
    }

    private Token readNumber() {
        int start = pos;
        if (peek() == '-') advance();

        while (!isAtEnd() && Character.isDigit(peek())) advance();

        if (!isAtEnd() && peek() == '.') {
            advance();
            while (!isAtEnd() && Character.isDigit(peek())) advance();
        }

        return new Token(TokenType.NUMBER, input.substring(start, pos));
    }

    private Token readString() {
        advance();
        int start = pos;

        while (!isAtEnd() && peek() != '"') advance();

        if (isAtEnd()) throw new QueryInvalidaException("Erro de Sintaxe: Texto (String) iniciado com aspas, mas não foi fechado.");

        String value = input.substring(start, pos);
        advance();
        return new Token(TokenType.STRING, value);
    }

    private boolean isNextDigit() { return (pos + 1 < length) && Character.isDigit(input.charAt(pos + 1)); }
    private boolean isAtEnd() { return pos >= length; }
    private char peek() { return isAtEnd() ? '\0' : input.charAt(pos); }
    private void advance() { pos++; }
}

