package me.codex.language.token;
import me.codex.language.Span;

public interface Token {
  public String value();
  public Span span();
}
