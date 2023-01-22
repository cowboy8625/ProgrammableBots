package me.codex.programmable_bots.block.entity;

import me.codex.programmable_bots.ProgrammableBots;
import me.codex.programmable_bots.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<BotBlockEntity> BOT;

    public static void registerBlockEntities() {
        BOT = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ProgrammableBots.MODID, "bot"),
            FabricBlockEntityTypeBuilder.create(BotBlockEntity::new, ModBlocks.BOT_BLOCK).build(null));


    }
}
