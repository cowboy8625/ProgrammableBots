package me.codex.language;

import java.util.*;

public class Concative {
    private final Deque<Integer> stack = new ArrayDeque<>();
    private final Map<String, List<String>> words = new HashMap<>();
    private final Map<String, Runnable> builtins = new HashMap<>();
    private boolean compiling = false;
    private String currentWord = null;

    public Concative() {
        builtins.put("+", () -> stack.push(stack.pop() + stack.pop()));
        builtins.put("-", () -> {
            int b = stack.pop();
            int a = stack.pop();
            stack.push(a - b);
        });
        builtins.put("*", () -> stack.push(stack.pop() * stack.pop()));
        builtins.put("/", () -> {
            int b = stack.pop();
            int a = stack.pop();
            stack.push(a / b);
        });
        builtins.put("DUP", () -> stack.push(stack.peek()));
        builtins.put("DROP", stack::pop);
        builtins.put("SWAP", this::swap);
        builtins.put("OVER", () -> {
            Iterator<Integer> it = stack.iterator();
            int top = it.next();
            int second = it.next();
            stack.push(second);
        });
        builtins.put(".", () -> System.out.println(stack.pop()));
        builtins.put(".S", () -> System.out.println(stack.peek()));
    }

    public Integer peekStack() {
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    public Integer popStack() {
        if (stack.isEmpty()) {
            return null;
        }
        return stack.pop();
    }

    public void pushStack(int value) {
        stack.push(value);
    }

    public void registerBuiltin(String name, Runnable action) {
        builtins.put(name.toUpperCase(), action);
    }

    private void swap() {
        int a = stack.pop();
        int b = stack.pop();
        stack.push(a);
        stack.push(b);
    }

    public void executeWord(String word) {
        if (word.matches("-?\\d+")) {
            stack.push(Integer.parseInt(word));
        } else if (builtins.containsKey(word.toUpperCase())) {
            builtins.get(word.toUpperCase()).run();
        } else if (words.containsKey(word)) {
            for (String w : words.get(word)) {
                executeWord(w);
            }
        } else {
            throw new RuntimeException("Unknown word: " + word);
        }
    }

    public void interpret(String line) {
        String[] tokens = line.trim().split("\\s+");
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.equals(":")) {
                compiling = true;
                currentWord = tokens[++i];
                words.put(currentWord, new ArrayList<>());
            } else if (token.equals(";")) {
                compiling = false;
                currentWord = null;
            } else {
                if (compiling && currentWord != null) {
                    words.get(currentWord).add(token);
                } else {
                    executeWord(token);
                }
            }
        }
    }
}
