package me.codex.language.lexer;
import me.codex.language.token.Ident;
import me.codex.language.token.Token;
import me.codex.language.Span;
import java.util.ArrayList;
import java.util.Optional;

public class Lexer {
  interface NextChar {
    public boolean next_if(char c);
  }
  private final String src;
  private int idx = 0;
  private Span span = new Span(0,0,0);
  public Lexer(String src) {
    this.src = src;
  }

  private boolean isNotEnd() {
    return idx < src.length();
  }

  private boolean isEnd() {
    return idx >= src.length();
  }
  
  private Span span() {
    var s = this.span;
    return s;
  }
  
  private Optional<Character> next_if(NextChar func) {
    var maybeChar = this.peek();
    if (maybeChar.isEmpty()) return maybeChar;
    if (func.next_if(maybeChar.get())) return this.next();
    return Optional.empty();
  }

  private Token identifier(char c) {
    String ident = String.format("%c", c);
    while (this.isNotEnd()) {
      var maybeChar = this.next_if((ch) -> Character.isLetter(ch) || Character.isDigit(c) || ch == '_');
      if (maybeChar.isEmpty()) break;
      ident += maybeChar.get();
    }
    return new Ident(ident, span());
  }

  private Optional<Character> peek() {
    var isPeekEnd = this.idx >= this.src.length();
    if (isPeekEnd) return Optional.empty();
    return Optional.of(this.src.charAt(this.idx));
  }

  private Optional<Character> next() {
    if (this.isEnd()) return Optional.empty();
    var i = this.idx;
    this.idx += 1;
    return Optional.of(this.src.charAt(i));
  }

  public ArrayList<Token> lex() {
    var tokens = new ArrayList<Token>();
    while (this.isNotEnd()) {
      var maybeChar = this.next();
      if(maybeChar.isEmpty()) break;
      var c = maybeChar.get();
      if (Character.isLetter(c)) {
        var token = this.identifier(c);
        tokens.add(token);
      }
    }
    return tokens;
  }
}
