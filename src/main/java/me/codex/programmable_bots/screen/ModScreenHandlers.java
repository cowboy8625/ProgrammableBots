package me.codex.programmable_bots.screen;

import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static ScreenHandlerType<BotBlockScreenHandler> BOT_SCREEN_HANDLER;

    public static void registerScreenHandlers() {
        BOT_SCREEN_HANDLER = new ScreenHandlerType<>(BotBlockScreenHandler::new);
    }
}
