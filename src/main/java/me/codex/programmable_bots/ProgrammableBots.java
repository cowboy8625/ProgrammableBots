package me.codex.programmable_bots;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.codex.programmable_bots.block.ModBlocks;
import me.codex.programmable_bots.block.entity.ModBlockEntities;
import me.codex.programmable_bots.gamerules.ModGamerules;
import me.codex.programmable_bots.item.ModItemGroup;
import me.codex.programmable_bots.screen.ModScreenHandlers;

public class ProgrammableBots implements ModInitializer {
    public static final String MODID = "programmable_bots";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        // Creative Tabs
        ModItemGroup.registerItemGroup();

        // Blocks and Items
        ModBlocks.registerModBlocks();

        // Gamerules
        ModGamerules.registerModGamerules();

        // Other
        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();
    }
}
