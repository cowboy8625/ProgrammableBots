package me.codex.language.parser;
import me.codex.language.token.Token;

public abstract class Expr {
  public interface Visitor<R> {
    R visitBinaryExpr(Binary expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitUnaryExpr(Unary expr);
  }
  
  public static class Grouping extends Expr {
    Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }

    public final Expr expression;
  }
  public static class Literal extends Expr {
    Literal(Object value) {
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }

    @Override
    public String toString() {
        return String.format("%s", this.value);
    }

    public final Object value;
  }

  public static class Binary extends Expr {
    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }
    
    @Override
    public String toString() {
        return String.format("(%s %s %s)",operator, left, right);
    }

    public final Expr left;
    public final Token operator;
    public final Expr right;
  }
  
public static class Unary extends Expr {
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }

    @Override
    public String toString() {
        return String.format("(%s %s)",operator, right);
    }

    public final Token operator;
    public final Expr right;
  }


  public abstract <R> R accept(Visitor<R> visitor);
}
