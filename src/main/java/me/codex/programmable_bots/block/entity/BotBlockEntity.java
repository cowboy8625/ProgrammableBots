package me.codex.programmable_bots.block.entity;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import me.codex.programmable_bots.block.BotBlock;
import me.codex.programmable_bots.screen.BotBlockScreenHandler;
import net.minecraft.block.Block;
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
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BotBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(22, ItemStack.EMPTY);
    private boolean executingBook = false;
    private int bookLineIndex = -1;

    public BotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOT, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    public void setItems(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Bot");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new BotBlockScreenHandler(syncId, inventory, this);
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

    private static ArrayList<String> toLines(NbtElement nbt) {
        ArrayList<String> output = new ArrayList<>();
        if (nbt.getType() == 9) {
            NbtList list = (NbtList) nbt;

            for (int i = 0; i < list.size(); i++) {
                String[] page = list.get(i).toString().replace("\"", "").replace("'", "").split("\n");

                for (String line : page) {
                    if (!line.isEmpty()) {
                        output.add(line);
                    }
                }
            }
            return output;
        }
        return new ArrayList<>();
    }

    // Runs every tick
    public static void tick(World world, BlockPos pos, BlockState state, BotBlockEntity entity) {
        if (world.isClient) {
            return;
        }

        if (entity.hasBook() && !entity.executingBook) {
            entity.executingBook = true;
            ItemStack stack = entity.getStack(0);
            NbtElement pages = stack.getNbt().get("pages");
            ArrayList<String> content = toLines(pages);

            if (entity.bookLineIndex < content.size()) {
                int i = entity.bookLineIndex++;
                String line = content.get(i);

                switch (line) {
                    case "forward":
                        world.getServer().sendMessage(Text.literal("Line "+i+" = "+line));
                        entity.moveBot(world, entity, state, MoveDirections.FORWARD, entity.bookLineIndex);
                        break;
                    case "back":
                        world.getServer().sendMessage(Text.literal("Line "+i+" = "+line));
                        entity.moveBot(world, entity, state, MoveDirections.BACK, entity.bookLineIndex);
                        break;
                    case "up":
                        world.getServer().sendMessage(Text.literal("Line "+i+" = "+line));
                        entity.moveBot(world, entity, state, MoveDirections.UP, entity.bookLineIndex);
                        break;
                    case "down":
                        world.getServer().sendMessage(Text.literal("Line "+i+" = "+line));
                        entity.moveBot(world, entity, state, MoveDirections.DOWN, entity.bookLineIndex);
                        break;
                    case "left":
                        world.getServer().sendMessage(Text.literal("Line "+i+" = "+line));
                        entity.moveBot(world, entity, state, MoveDirections.LEFT, entity.bookLineIndex);
                        break;
                    case "right":
                        world.getServer().sendMessage(Text.literal("Line "+i+" = "+line));
                        entity.moveBot(world, entity, state, MoveDirections.RIGHT, entity.bookLineIndex);
                        break;
                    case "turn left":
                        world.getServer().sendMessage(Text.literal("Line "+i+" = "+line));
                        entity.turn(world, entity, state, TurnDirections.LEFT);
                        break;
                    case "turn right":
                        world.getServer().sendMessage(Text.literal("Line "+i+" = "+line));
                        break;
                    case "turn around":
                        world.getServer().sendMessage(Text.literal("Line "+i+" = "+line));
                        break;
                    default:
                        break;
                }
            }
        } else if (!entity.hasBook()) {
            entity.executingBook = false;
            entity.bookLineIndex = 0;
        }
    }

/*
 *  North = -Z
 *  South = +Z
 *  East = +X
 *  West = -X
 */

    private void moveBot(World world, BotBlockEntity entity, BlockState state, MoveDirections direction, int bookLineIndex) {
        BlockPos currentPos = entity.getPos();
        BlockPos moveTo;
        switch (state.get(BotBlock.FACING).toString()) {
            case "north":
                switch (direction) {
                    case FORWARD:
                        moveTo = currentPos.add(0, 0, -1);
                        break;
                    case BACK:
                        moveTo = currentPos.add(0, 0, 1);
                        break;
                    case UP:
                        moveTo = currentPos.add(0, 1, 0);
                        break;
                    case DOWN:
                        moveTo = currentPos.add(0, -1, 0);
                        break;
                    case LEFT:
                        moveTo = currentPos.add(-1, 0, 0);
                        break;
                    case RIGHT:
                        moveTo = currentPos.add(1, 0, 0);
                        break;
                    default:
                        moveTo = currentPos.add(0, 0, 0);
                        break;
                }
                move(world, entity, state, moveTo);
                break;
            case "south":
                switch (direction) {
                    case FORWARD:
                        moveTo = currentPos.add(0, 0, 1);
                        break;
                    case BACK:
                        moveTo = currentPos.add(0, 0, -1);
                        break;
                    case UP:
                        moveTo = currentPos.add(0, 1, 0);
                        break;
                    case DOWN:
                        moveTo = currentPos.add(0, -1, 0);
                        break;
                    case LEFT:
                        moveTo = currentPos.add(1, 0, 0);
                        break;
                    case RIGHT:
                        moveTo = currentPos.add(-1, 0, 0);
                        break;
                    default:
                        moveTo = currentPos.add(0, 0, 0);
                        break;
                }
                move(world, entity, state, moveTo);
                break;
            case "east":
                switch (direction) {
                    case FORWARD:
                        moveTo = currentPos.add(1, 0, 0);
                        break;
                    case BACK:
                        moveTo = currentPos.add(-1, 0, 0);
                        break;
                    case UP:
                        moveTo = currentPos.add(0, 1, 0);
                        break;
                    case DOWN:
                        moveTo = currentPos.add(0, -1, 0);
                        break;
                    case LEFT:
                        moveTo = currentPos.add(0, 0, -1);
                        break;
                    case RIGHT:
                        moveTo = currentPos.add(0, 0, 1);
                        break;
                    default:
                        moveTo = currentPos.add(0, 0, 0);
                        break;
                }
                move(world, entity, state, moveTo);
                break;
            case "west":
                switch (direction) {
                    case FORWARD:
                        moveTo = currentPos.add(-1, 0, 0);
                        break;
                    case BACK:
                        moveTo = currentPos.add(1, 0, 0);
                        break;
                    case UP:
                        moveTo = currentPos.add(0, 1, 0);
                        break;
                    case DOWN:
                        moveTo = currentPos.add(0, -1, 0);
                        break;
                    case LEFT:
                        moveTo = currentPos.add(0, 0, 1);
                        break;
                    case RIGHT:
                        moveTo = currentPos.add(0, 0, -1);
                        break;
                    default:
                        moveTo = currentPos.add(0, 0, 0);
                        break;
                }
                move(world, entity, state, moveTo);
                break;
        }
    }

    // TODO: Figure out how to force GUI to close to prevent duping items.
    private void move(World world, BotBlockEntity entity, BlockState state, BlockPos moveToPos) {
        BlockPos currentPos = entity.getPos();

        if (!world.isAir(moveToPos)) {
            return;
        }

        world.setBlockState(moveToPos, state);

        BotBlockEntity newEntity = (BotBlockEntity) world.getBlockEntity(moveToPos);
        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);
        newEntity.readNbt(nbt);
        newEntity.bookLineIndex = bookLineIndex;

        world.removeBlockEntity(currentPos);
        world.removeBlock(currentPos, true);
    }

    private void turn(World world, BotBlockEntity entity, BlockState state, TurnDirections turn) {
        switch (state.get(BotBlock.FACING).toString()) {
            case "north":
                switch (turn) {
                    case LEFT:
                        world.removeBlock(entity.pos, true);
                        world.setBlockState(entity.pos, state.rotate(BlockRotation.COUNTERCLOCKWISE_90));
                    case RIGHT:
                        world.removeBlock(entity.pos, true);
                        world.setBlockState(entity.pos, state.rotate(BlockRotation.CLOCKWISE_90));
                    case AROUND:
                        world.removeBlock(entity.pos, true);
                        world.setBlockState(entity.pos, state.rotate(BlockRotation.CLOCKWISE_180));
                }
                break;
            case "south":
                break;
            case "east":
                break;
            case "west":
                break;
        }
    }
}
