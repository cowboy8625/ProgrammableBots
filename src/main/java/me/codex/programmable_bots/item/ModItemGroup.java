package me.codex.programmable_bots.item;

import me.codex.programmable_bots.ProgrammableBots;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static ItemGroup PROGRAMMABLE_BOTS;

    public static void registerItemGroup() {
        ProgrammableBots.LOGGER.debug("Registering ModItemGroup for "+ProgrammableBots.MODID);

        PROGRAMMABLE_BOTS = FabricItemGroup.builder(new Identifier(ProgrammableBots.MODID, "tab"))
            .displayName(Text.translatable("itemGroup.programmable_bots.tab"))
            .icon(() -> new ItemStack(Blocks.COMMAND_BLOCK))
            .build();
    }
}
