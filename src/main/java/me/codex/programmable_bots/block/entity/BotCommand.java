package me.codex.programmable_bots.block.entity;

import me.codex.programmable_bots.block.entity.BotBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;

public interface BotCommand {
    void execute(World world, BlockState state, BotBlockEntity entity);
}
