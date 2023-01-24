package me.codex.programmable_bots.gamerules;

import me.codex.programmable_bots.ProgrammableBots;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.Category;

public class ModGamerules {
    public static final GameRules.Key<GameRules.IntRule> BOT_EXECUTION_DELAY = GameRuleRegistry.register("botExecutionDelay",
        Category.UPDATES,
        GameRuleFactory.createIntRule(8));

    public static void registerModGamerules() {
        ProgrammableBots.LOGGER.debug("Registering mod gamerules for "+ProgrammableBots.MODID);
    }
}
