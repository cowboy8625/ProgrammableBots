package me.codex.programmable_bots;

import me.codex.programmable_bots.screen.BotScreen;
import me.codex.programmable_bots.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ProgrammableBotsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.BOT_SCREEN_HANDLER, BotScreen::new);
    }
}
