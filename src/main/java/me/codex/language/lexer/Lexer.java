package me.codex.language.lexer;
import me.codex.language.token.Token;
import me.codex.language.token.TokenKind;
import me.codex.language.Span;
import me.codex.either.Either;
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
    var s = (Span) this.span.clone();
    this.span.reset();
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
      var maybeChar = this.next_if((ch) -> Character.isLetter(ch) || Character.isDigit(ch) || ch == '_');
      if (maybeChar.isEmpty()) break;
      ident += maybeChar.get();
    }
    return new Token(ident, span(), TokenKind.Ident);
  }

  private Token number(char c) {
    String num = String.format("%c", c);
    while (this.isNotEnd()) {
      var maybeChar = this.next_if((ch) -> Character.isDigit(ch) || ch == '_');
      if (maybeChar.isEmpty()) break;
      num += maybeChar.get();
    }
    return new Token(num, span(), TokenKind.Number);
  }

  private Optional<Character> peek() {
    var isPeekEnd = this.idx >= this.src.length();
    if (isPeekEnd) return Optional.empty();
    return Optional.of(this.src.charAt(this.idx));
  }

  private Optional<Character> next() {
    if (this.isEnd()) return Optional.empty();
    this.span.shiftRight();
    var i = this.idx;
    this.idx += 1;
    return Optional.of(this.src.charAt(i));
  }

  public Either<LexerInvalidCharError, ArrayList<Token>> lex() {
    var tokens = new ArrayList<Token>();
    while (this.isNotEnd()) {
      var maybeChar = this.next();
      if(maybeChar.isEmpty()) break;
      var c = maybeChar.get();
      if (Character.isLetter(c)) {
        Token token = this.identifier(c);
        tokens.add(token);
      } else if (Character.isDigit(c)) {
        Token token = this.number(c);
        tokens.add(token);
      } else if (c == '+') {
        // TODO: All operators for one if statement
        Token token = new Token(c.toString(), span(), TokenKind.Plus);
        tokens.add(token);
      } else if (c == ' ') {
        this.span.shiftRight(); 
        this.span.reset(); 
      } else if (c == '\n') {
        this.span.shiftRight(); 
        this.span.newLine();
        this.span.reset(); 
      } else {
        return Either.ofLeft(new LexerInvalidCharError(c));
      }
    }
    
    tokens.add(new Token("", span(), TokenKind.EOF));
    return Either.ofRight(tokens);
  }
}
