package me.codex.language;

public class Span {
  private int line;
  private int start;
  private int end;
  public Span(int line, int start, int end) {
    this.line = line;
    this.start = start;
    this.end = end;
  }

  @Override
  public String toString() {
    return String.format("(%d:%d..%d)", this.line, this.start, this.end);
  }

  public void shiftRight() {
    this.end += 1;
  }

  public void newLine() {
    this.line += 1;
  }

  public void reset() {
    this.start = this.end;
  }

  public int getLine() {
    return this.line;
  }

  public int getStart() {
    return this.start;
  }

  public int getEnd() {
    return this.end;
  }
}
