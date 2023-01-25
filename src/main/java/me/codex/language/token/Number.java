package me.codex.language.token;
import me.codex.language.Span;

public record Number(String value, Span span) implements Token {
  public String value() {
    return this.value;
  }
  public Span span() {
    return this.span;
  }
}
