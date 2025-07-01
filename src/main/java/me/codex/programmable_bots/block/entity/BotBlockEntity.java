package me.codex.programmable_bots.block.entity;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ArrayDeque;
import java.util.Deque;

import me.codex.language.Concative;
import me.codex.language.interpreter.Interpreter;
import me.codex.language.lexer.Lexer;
import me.codex.either.Either;
import me.codex.language.parser.Parser;
import me.codex.language.token.Token;
import me.codex.programmable_bots.block.BotBlock;
import me.codex.programmable_bots.block.entity.BotCommand;
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
    private int bookLineIndex = 0;
    private long lastRan = 0;
    private long executionDelay = -1;
    private Concative concative = new Concative();
    private Deque<BotCommand> queue = new ArrayDeque<>();
    private List<String> lines = new ArrayList<>();
    private boolean isDirty = false;

    public BotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOT, pos, state);
        initConcative();
    }

    private void initConcative() {
        var directions = new ArrayList<MoveDirections>(){{
            add(MoveDirections.FORWARD);
            add(MoveDirections.BACK);
            add(MoveDirections.UP);
            add(MoveDirections.DOWN);
            add(MoveDirections.LEFT);
            add(MoveDirections.RIGHT);
        }};

        var directionSize = directions.size();
        for (int i = 0; i < directionSize; i++) {
            BotCommand direction = directions.get(i);
            concative.registerBuiltin(direction.toString(), () -> {
                Integer value = this.concative.popStack();
                if (value == null || value == 1) {
                    this.queue.addLast(direction);
                    return;
                }
                for (int j = 0; j < value; j++) {
                    this.queue.addLast(direction);
                }
            });
        }

        var turnDirections = new ArrayList<TurnDirections>(){{
            add(TurnDirections.RIGHT);
            add(TurnDirections.LEFT);
            add(TurnDirections.AROUND);
        }};
        var turnDirectionsSize = turnDirections.size();
        for (int i = 0; i < turnDirectionsSize; i++) {
            BotCommand turnDirection = turnDirections.get(i);
            concative.registerBuiltin(turnDirection.toString(), () -> {
                Integer value = this.concative.popStack();
                if (value == null || value == 1) {
                    this.queue.addLast(turnDirection);
                    return;
                }
                for (int j = 0; j < value; j++) {
                    this.queue.addLast(turnDirection);
                }
            });
        }
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

    private static List<String> bookIntoString(NbtElement nbt) {
        if (nbt.getType() != NbtElement.LIST_TYPE) {
            return new ArrayList<String>();
        }
        NbtList list = (NbtList) nbt;

        List<String> pages = list.stream().map(i -> {
            String line = i.toString();
            line = line.replace("\"", "");
            line = line.replace("'", "");
            return line;
        }).toList();
        List<String> result = new ArrayList<>();
        for (String page : pages) {
            var lines = page.split("\n");
            for (String line : lines) {
                result.add(line);
            }
        }
        return result;
    }

    private static void botDebugPrint(boolean canPrint, World world, Object object){
        if (!canPrint) {
            return;
        }
        for (PlayerEntity player : world.getPlayers()) {
            if (object != null) {
                player.sendMessage(Text.literal(object.toString()));
            } else {
                player.sendMessage(Text.literal("NULL"));
            }

        }
    }

    private boolean canRunCode() {
        return this.queue.isEmpty() && !this.lines.isEmpty() && this.bookLineIndex < this.lines.size();
    }

    private void executeCode() {
        String line = this.lines.get(this.bookLineIndex);
        this.bookLineIndex++;
        try {
            this.concative.interpret(line);
        } catch(Exception e) {
            botDebugPrint(world.getGameRules().getBoolean(ModGamerules.BOT_DEBUG_OUTPUT), world, line);
            botDebugPrint(world.getGameRules().getBoolean(ModGamerules.BOT_DEBUG_OUTPUT), world, e.getMessage());
            botDebugPrint(world.getGameRules().getBoolean(ModGamerules.BOT_DEBUG_OUTPUT), world, e.getStackTrace());
            botDebugPrint(world.getGameRules().getBoolean(ModGamerules.BOT_DEBUG_OUTPUT), world, e);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, BotBlockEntity entity) {
        if (world.isClient) {
            return;
        }

        if (world.getGameRules().getInt(ModGamerules.BOT_EXECUTION_DELAY) != entity.executionDelay) {
            entity.executionDelay = world.getGameRules().getInt(ModGamerules.BOT_EXECUTION_DELAY);
        }

        boolean canRunCode = entity.hasBook() && world.getTime() > entity.lastRan;
        if (canRunCode && entity.canRunCode()) {
            // RUN CODE
            entity.lastRan = world.getTime() + entity.executionDelay;

            entity.executeCode();
        } else if (canRunCode && entity.lines.isEmpty()) {
            // LOAD CODE
            entity.lastRan = world.getTime() + entity.executionDelay;
            entity.isDirty = true;

            ItemStack stack = entity.getStack(0);
            NbtElement pages = stack.getNbt().get("pages");
            entity.lines = bookIntoString(pages);
            entity.executeCode();
        } else if (canRunCode &&  !entity.queue.isEmpty()) {
            // RUN QUEUE COMMAND
            entity.lastRan = world.getTime() + entity.executionDelay;

            var command = entity.queue.pollFirst();
            command.execute(world, state, entity);
        } else if (!entity.hasBook() && entity.isDirty) {
            // END
            entity.reset();
        }
    }

    private void reset() {
        this.initConcative();
        this.bookLineIndex = 0;
        this.lastRan = 0;
        this.queue = new ArrayDeque<>();
        this.lines = new ArrayList<>();
    }

    public static void moveBot(World world, BotBlockEntity entity, BlockState state, MoveDirections direction) {
        BlockPos currentPos = entity.getPos();
        BlockPos moveTo = currentPos;

        String facing = state.get(BotBlock.FACING).toString();

        int xOffset = 0, zOffset = 0;

        switch (facing) {
            case "north":
                zOffset = (direction == MoveDirections.FORWARD) ? -1 : (direction == MoveDirections.BACK) ? 1 : 0;
                xOffset = (direction == MoveDirections.LEFT) ? -1 : (direction == MoveDirections.RIGHT) ? 1 : 0;
                break;
            case "south":
                zOffset = (direction == MoveDirections.FORWARD) ? 1 : (direction == MoveDirections.BACK) ? -1 : 0;
                xOffset = (direction == MoveDirections.LEFT) ? 1 : (direction == MoveDirections.RIGHT) ? -1 : 0;
                break;
            case "east":
                xOffset = (direction == MoveDirections.FORWARD) ? 1 : (direction == MoveDirections.BACK) ? -1 : 0;
                zOffset = (direction == MoveDirections.LEFT) ? -1 : (direction == MoveDirections.RIGHT) ? 1 : 0;
                break;
            case "west":
                xOffset = (direction == MoveDirections.FORWARD) ? -1 : (direction == MoveDirections.BACK) ? 1 : 0;
                zOffset = (direction == MoveDirections.LEFT) ? 1 : (direction == MoveDirections.RIGHT) ? -1 : 0;
                break;
        }

        if (direction == MoveDirections.UP) {
            moveTo = currentPos.add(0, 1, 0);
        } else if (direction == MoveDirections.DOWN) {
            moveTo = currentPos.add(0, -1, 0);
        } else {
            moveTo = currentPos.add(xOffset, 0, zOffset);
        }

        move(world, entity, state, moveTo);
    }

    // TODO: Figure out how to force GUI to close to prevent duping items.
    private static void move(World world, BotBlockEntity entity, BlockState state, BlockPos moveToPos) {
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
        newEntity.queue = entity.queue;
        newEntity.lines = entity.lines;
        newEntity.concative = entity.concative;
        newEntity.isDirty = entity.isDirty;

        world.removeBlockEntity(currentPos);
        world.removeBlock(currentPos, true);
    }

    public void turn(World world, BotBlockEntity entity, BlockState state, TurnDirections turn) {
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
                break;
        }
    }
}
