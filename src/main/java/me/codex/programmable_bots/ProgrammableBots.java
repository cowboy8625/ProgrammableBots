package me.codex.programmable_bots;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.codex.programmable_bots.item.ModItemGroup;

public class ProgrammableBots implements ModInitializer {
    public static final String MODID = "programmable_bots";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        ModItemGroup.registerItemGroup();
    }
}
