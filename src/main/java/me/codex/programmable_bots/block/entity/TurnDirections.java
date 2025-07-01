package me.codex.programmable_bots.block.entity;

import me.codex.programmable_bots.block.entity.BotCommand;
import me.codex.programmable_bots.block.entity.BotBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;

public enum TurnDirections implements BotCommand {
    LEFT,
    RIGHT,
    AROUND;

    @Override
    public void execute(World world, BlockState state, BotBlockEntity entity) {
        entity.turn(world, entity, state, this);
    }
}
