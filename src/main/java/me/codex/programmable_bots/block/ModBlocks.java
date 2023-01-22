package me.codex.programmable_bots.block;

import me.codex.programmable_bots.ProgrammableBots;
import me.codex.programmable_bots.item.ModItemGroup;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block BOT_BLOCK = registerBlock("bot",
        new BotBlock(FabricBlockSettings.of(Material.METAL).strength(4f).requiresTool()), ModItemGroup.PROGRAMMABLE_BOTS);

    public static void registerModBlocks() {
        ProgrammableBots.LOGGER.debug("Registering mod blocks for "+ProgrammableBots.MODID);
    }

    private static Block registerBlock(String id, Block block, ItemGroup tab) {
        registerBlockItem(id, block, tab);
        return Registry.register(Registries.BLOCK, new Identifier(ProgrammableBots.MODID, id), block);
    }

    private static Item registerBlockItem(String id, Block block, ItemGroup tab) {
        Item item = Registry.register(Registries.ITEM, new Identifier(ProgrammableBots.MODID, id),
            new BlockItem(block, new FabricItemSettings()));
        ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> entries.add(item));
        return item;
    }
}
