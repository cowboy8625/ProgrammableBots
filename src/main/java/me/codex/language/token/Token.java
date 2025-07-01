package me.codex.language.token;
import me.codex.language.Span;

public record Token(String value, Span span, TokenKind kind){

  @Override
  public String toString() {
    return String.format("%s", value);
  }
}