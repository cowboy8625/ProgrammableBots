package me.codex.programmable_bots.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BotBlockScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public BotBlockScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(22));
    }

    public BotBlockScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.BOT_SCREEN_HANDLER, syncId);
        checkSize(inventory, 22);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        populateBotInventory();

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (originalStack.isOf(Items.WRITABLE_BOOK)) {
                if (!this.insertItem(originalStack, 0, 1, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 1, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void populateBotInventory() {
        this.addSlot(new Slot(inventory, 0, 8, 16) {
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.WRITABLE_BOOK);
            }
        });

        int index = 1;
        for (int c = 0; c < 3; c++) {
            for (int r = 0; r < 7; r++) {
                this.addSlot(new Slot(inventory, index, 44 + r * 18, 16 + c * 18));
                index++;
            }
        }
    }

    private void addPlayerInventory(PlayerInventory inventory) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                this.addSlot(new Slot(inventory, c + r * 9 + 9, 8 + c * 18, 84 + r * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory inventory) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }
}
