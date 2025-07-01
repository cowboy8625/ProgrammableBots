package me.codex.programmable_bots.block.entity;

import me.codex.programmable_bots.block.entity.BotCommand;
import me.codex.programmable_bots.block.entity.BotBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;


public enum MoveDirections implements BotCommand {
    FORWARD,
    BACK,
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public String toString() {
        return this.name().toLowerCase();
    }

    @Override
    public void execute(World world, BlockState state, BotBlockEntity entity) {
        entity.moveBot(world, entity, state, this);
    }
}
