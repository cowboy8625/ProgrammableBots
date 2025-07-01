package me.codex.language.interpreter;

import me.codex.language.parser.*;
import me.codex.language.token.Token;

public class Interpreter implements Expr.Visitor<Object> {

    public Object interpret(Expr expr) {
        try {
            return evaluate(expr);
        } catch (RuntimeException e) {
            System.err.println("Runtime error: " + e.getMessage());
            return null;
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.value()) {
            case "-":
                checkNumberOperand(expr.operator, right);
                return -1 * (double) right;
            case "!":
                return !isTruthy(right);
        }

        throw new RuntimeException("Unknown unary operator: " + expr.operator.value());
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        try {
            System.out.println(String.format("Right %s, Left %s", right, left));
            System.out.println(String.format("Right %s, Left %s", right.getClass().getName(), left.getClass().getName()));
        } catch(Exception e) {
            System.out.println(e);
        }
        switch (expr.operator.value()) {
            case "+":
                if (left instanceof Double && right instanceof Double)
                    return (double) left + (double) right;
                if (left instanceof String || right instanceof String)
                    return String.valueOf(left) + String.valueOf(right);
                throw new RuntimeException("Operands must be numbers or strings.");
            case "-":
                checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            case "*":
                checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
            case "/":
                checkNumberOperands(expr.operator, left, right);
                return (double) left / (double) right;
            case "==":
                return isEqual(left, right);
            case "!=":
                return !isEqual(left, right);
            case "<":
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case "<=":
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case ">":
                checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            case ">=":
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
        }

        throw new RuntimeException("Unknown binary operator: " + expr.operator.value());
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeException("Operand must be a number for " + operator.value());
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeException("Operands must be numbers for " + operator.value());
    }

    private boolean isTruthy(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Boolean) return (Boolean) obj;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }
}