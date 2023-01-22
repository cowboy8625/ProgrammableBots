package me.codex.programmable_bots.block.entity;

import org.jetbrains.annotations.Nullable;

import me.codex.programmable_bots.screen.BotBlockScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BotBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(22, ItemStack.EMPTY);

    protected final PropertyDelegate delegate;
    private int running = 0;

    public BotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOT, pos, state);

        this.delegate = new PropertyDelegate() {
            public int get(int index) {
                return BotBlockEntity.this.running;
            }

            public void set(int index, int value) {
                BotBlockEntity.this.running = value;
            }

            public int size() {
                return 21;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Bot");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new BotBlockScreenHandler(syncId, inventory, this, this.delegate);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
    }

    public boolean hasBook() {
        ItemStack stack = this.getStack(0);
        return !stack.isEmpty();
    }

    // Next update thing.
    private static String fromList(NbtElement nbt) {
        String output = "";
        if (nbt.getType() == 9) {
            NbtList list = (NbtList) nbt;

            for (int i = 0; i < list.size(); i++) {
                output += list.get(i).toString().replace("\"", "").replace("'", "");
                if (i < list.size() - 1) {
                    output += "\n";
                }
            }
            return output;
        }
        return "";
    }

    // Runs every tick
    public static void tick(World world, BlockPos pos, BlockState state, BotBlockEntity entity) {
        if (world.isClient) {
            return;
        }
        world.getServer().sendMessage(Text.literal("Hello from server"));

        // // Next update thing.
        if (entity.hasBook()) {
            ItemStack stack = entity.getStack(0);
            NbtElement pages = stack.getNbt().get("pages");
            String content = fromList(pages);
            for (PlayerEntity player : world.getPlayers()) {
                player.sendMessage(Text.literal(content), true);
            }
        }
    }
}
