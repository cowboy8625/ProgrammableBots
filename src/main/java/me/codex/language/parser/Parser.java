package me.codex.language.parser;
import java.util.ArrayList;

import me.codex.language.token.Token;
import me.codex.language.token.TokenKind;

public class Parser {
    private static class ParseError extends RuntimeException {}
    private final ArrayList<Token> tokens;
    private int current = 0;
    public Parser(ArrayList<Token> tokens) {    
        this.tokens = tokens;
    }
    
    // public Expr parse() {
    //     
    // }
    private boolean isAtEnd() {
        return peek().kind() == TokenKind.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
    private boolean check(TokenKind kind) {
        if (isAtEnd()) return false;
        return peek().kind() == kind;
    }
    private Expr expression() {
        return equality();
    }
    
    private Expr equality() {
        Expr expr = comparison();

        while (match(TokenKind.BangEqual, TokenKind.EqualEqual)) {
        Token operator = previous();
        Expr right = comparison();
        expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }
    
    private boolean match(TokenKind ... kinds) {
        for (TokenKind kind : kinds) {
        if (check(kind)) {
            advance();
            return true;
        }
        }

        return false;
    }
  
    private Expr comparison() {
        Expr expr = term();

        while (match(TokenKind.Greater, TokenKind.GreaterEqual, TokenKind.Less, TokenKind.LessEqual)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }
    
    private Expr term() {
        Expr expr = factor();

        while (match(TokenKind.Minus, TokenKind.Plus)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }
    private Expr factor() {
        Expr expr = unary();

        while (match(TokenKind.Slash, TokenKind.Star)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }
    
    private Expr unary() {
        if (match(TokenKind.Bang, TokenKind.Minus)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }
    
    private Expr primary() {
        if (match(TokenKind.False)) return new Expr.Literal(false);
        if (match(TokenKind.True)) return new Expr.Literal(true);
        if (match(TokenKind.Nil)) return new Expr.Literal(null);

        if (match(TokenKind.String)) {
            return new Expr.Literal(previous().value());
        } else if (match(TokenKind.Number)) {
            return new Expr.Literal(Double.parseDouble(previous().value()));
        }

        if (match(TokenKind.LeftParen)) {
            Expr expr = expression();
            consume(TokenKind.RightParen, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        
        throw error(peek(), "Expect expression.");
    }
    
    private Token consume(TokenKind kind, String message) {
        if (check(kind)) return advance();

        throw error(peek(), message);
    }
    
    private ParseError error(Token token, String message) {
        // Lox.error(token, message);
        return new ParseError();
    }
    
    private void synchronize() {
        advance();

        while (!isAtEnd()) {
        if (previous().kind() == TokenKind.Semicolon) return;

        switch (peek().kind()) {
            // case CLASS:
            case Fn:
            // case VAR:
            // case FOR:
            // case IF:
            // case WHILE:
            // case PRINT:
            // case RETURN:
            return;
        }

        advance();
        }
    }
    
    public Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }
}
