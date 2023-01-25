package me.codex.programmable_bots.block.entity;

import java.util.ArrayList;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import me.codex.language.lexer.Lexer;
import me.codex.language.token.Token;
import me.codex.programmable_bots.block.BotBlock;
import me.codex.programmable_bots.gamerules.ModGamerules;
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
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.world.World;

public class BotBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(22, ItemStack.EMPTY);
    private boolean executingBook = false;
    private int bookLineIndex = 0;
    private long lastRan = 0;
    private long executionDelay = -1;

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

    private static Optional<String> maybeString(NbtElement nbt) {
        if (nbt.getType() != NbtElement.LIST_TYPE) {
            return Optional.empty();
        }
        NbtList list = (NbtList) nbt;

        return Optional.of(String.join("\n", list.stream().map(i -> {
            String line = i.toString();
            line = line.replace("\"", "");
            line = line.replace("'", "");
            return line;
        }).toList()));
    }

    // Runs every tick
    public static void tick(World world, BlockPos pos, BlockState state, BotBlockEntity entity) {
        if (world.isClient) {
            return;
        }

        if (world.getGameRules().getInt(ModGamerules.BOT_EXECUTION_DELAY) != entity.executionDelay) {
            entity.executionDelay = world.getGameRules().getInt(ModGamerules.BOT_EXECUTION_DELAY);
        }

        boolean canRunCode = entity.hasBook() && !entity.executingBook && world.getTime() > entity.lastRan;
        if (canRunCode) {
            entity.lastRan = world.getTime() + entity.executionDelay;
            entity.executingBook = true;
            ItemStack stack = entity.getStack(0);
           NbtElement pages = stack.getNbt().get("pages");
            Optional<String> content = maybeString(pages);
            if (content.isEmpty()) return;
            ArrayList<Token> tokens = new Lexer(content.get()).lex();

            if (entity.bookLineIndex < tokens.size()) {
                // for (PlayerEntity player : world.getPlayers()) {
                //     player.sendMessage(Text.literal(tokens.toString()));
                // }
                entity.runCommand(world, pos, state, entity, tokens);
            }
        } else if (!entity.hasBook() && entity.executingBook && entity.bookLineIndex > 0) {
            entity.executingBook = false;
            entity.bookLineIndex = 0;
        }
    }
    private void runCommand(World world, BlockPos pos, BlockState state, BotBlockEntity entity, ArrayList<Token> tokens) {
        int i = entity.bookLineIndex++;
        Token token = tokens.get(i);

        switch (token.value()) {
            case "forward":
                entity.moveBot(world, entity, state, MoveDirections.FORWARD);
                break;
            case "back":
                entity.moveBot(world, entity, state, MoveDirections.BACK);
                break;
            case "up":
                entity.moveBot(world, entity, state, MoveDirections.UP);
                break;
            case "down":
                entity.moveBot(world, entity, state, MoveDirections.DOWN);
                break;
            case "left":
                entity.moveBot(world, entity, state, MoveDirections.LEFT);
                break;
            case "right":
                entity.moveBot(world, entity, state, MoveDirections.RIGHT);
                break;
            case "turn left":
                entity.turn(world, entity, state, TurnDirections.LEFT);
                break;
            case "turn right":
                entity.turn(world, entity, state, TurnDirections.RIGHT);
                break;
            case "turn around":
                entity.turn(world, entity, state, TurnDirections.AROUND);
                break;
            default:
                break;
        }
    }

    private void moveBot(World world, BotBlockEntity entity, BlockState state, MoveDirections direction) {
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
        newEntity.bookLineIndex = entity.bookLineIndex;
        newEntity.lastRan = entity.lastRan;

        world.removeBlockEntity(currentPos);
        world.removeBlock(currentPos, true);
    }

    private void turn(World world, BotBlockEntity entity, BlockState state, TurnDirections turn) {
        switch (state.get(BotBlock.FACING).toString()) {
            case "north":
                switch (turn) {
                    case LEFT:
                        world.setBlockState(entity.pos, state.with(BotBlock.FACING, Direction.get(AxisDirection.NEGATIVE, Axis.X)));
                        break;
                    case RIGHT:
                        world.setBlockState(entity.pos, state.with(BotBlock.FACING, Direction.get(AxisDirection.POSITIVE, Axis.X)));
                        break;
                    case AROUND:
                        world.setBlockState(entity.pos, state.with(BotBlock.FACING, Direction.get(AxisDirection.POSITIVE, Axis.Z)));
                        break;
                }
                entity.executingBook = false;
                break;
            case "south":
                switch (turn) {
                    case LEFT:
                        world.setBlockState(entity.pos, state.with(BotBlock.FACING, Direction.get(AxisDirection.POSITIVE, Axis.X)));
                        break;
                    case RIGHT:
                        world.setBlockState(entity.pos, state.with(BotBlock.FACING, Direction.get(AxisDirection.NEGATIVE, Axis.X)));
                        break;
                    case AROUND:
                        world.setBlockState(entity.pos, state.with(BotBlock.FACING, Direction.get(AxisDirection.NEGATIVE, Axis.Z)));
                        break;
                }
                entity.executingBook = false;
                break;
            case "east":
                switch (turn) {
                    case LEFT:
                        world.setBlockState(entity.pos, state.with(BotBlock.FACING, Direction.get(AxisDirection.NEGATIVE, Axis.Z)));
                        break;
                    case RIGHT:
                        world.setBlockState(entity.pos, state.with(BotBlock.FACING, Direction.get(AxisDirection.POSITIVE, Axis.Z)));
                        break;
                    case AROUND:
                        world.setBlockState(entity.pos, state.with(BotBlock.FACING, Direction.get(AxisDirection.NEGATIVE, Axis.X)));
                        break;
                }
                entity.executingBook = false;
                break;
            case "west":
                switch (turn) {
                    case LEFT:
                        world.setBlockState(entity.pos, state.with(BotBlock.FACING, Direction.get(AxisDirection.POSITIVE, Axis.Z)));
                        break;
                    case RIGHT:
                        world.setBlockState(entity.pos, state.with(BotBlock.FACING, Direction.get(AxisDirection.NEGATIVE, Axis.Z)));
                        break;
                    case AROUND:
                        world.setBlockState(entity.pos, state.with(BotBlock.FACING, Direction.get(AxisDirection.POSITIVE, Axis.X)));
                        break;
                }
                entity.executingBook = false;
                break;
        }
    }
}
