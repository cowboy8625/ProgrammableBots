package me.codex.language.lexer;

public class LexerInvalidCharError extends Exception {
    public LexerInvalidCharError(char c) {
        super(String.format("unknown char %c", c));
    }
}
