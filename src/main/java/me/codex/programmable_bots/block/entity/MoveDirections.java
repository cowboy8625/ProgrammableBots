package me.codex.programmable_bots.block.entity;

public enum MoveDirections {
    FORWARD(0),
    BACK(1),
    UP(2),
    DOWN(3),
    LEFT(4),
    RIGHT(5);

    private int value;

    private MoveDirections(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
