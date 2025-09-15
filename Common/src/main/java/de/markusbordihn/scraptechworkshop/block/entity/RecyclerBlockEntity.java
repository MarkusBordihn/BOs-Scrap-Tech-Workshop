/*
 * Copyright 2025 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.scraptechworkshop.block.entity;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.RecyclerBlock;
import de.markusbordihn.scraptechworkshop.config.RecyclerConfig;
import de.markusbordihn.scraptechworkshop.data.recycler.RecyclerStatus;
import de.markusbordihn.scraptechworkshop.menu.RecyclerMenu;
import de.markusbordihn.scraptechworkshop.recipe.recycler.RecyclerRecipe;
import de.markusbordihn.scraptechworkshop.recipe.recycler.RecyclerRecipeSelector;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecyclerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {

  public static final int INPUT_SLOTS = 1;
  public static final int OUTPUT_SLOTS = 9;
  public static final int UPGRADE_SLOTS = 2;
  public static final int TOTAL_SLOTS = INPUT_SLOTS + OUTPUT_SLOTS + UPGRADE_SLOTS;
  public static final int INPUT_SLOT = 0;
  public static final int FIRST_OUTPUT_SLOT = 1;
  public static final int LAST_OUTPUT_SLOT = 9;
  public static final String ID = "recycler";
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String PROGRESS_TAG = "Progress";
  private static final String MAX_PROGRESS_TAG = "MaxProgress";
  private static final String NO_RECIPE_TIMER_TAG = "NoRecipeTimer";
  private static final String DONE_TIMER_TAG = "DoneTimer";
  private static final String ITEM_TAG_PREFIX = "Item";
  private static final int PROGRESS_DATA_INDEX = 0;
  private static final int MAX_PROGRESS_DATA_INDEX = 1;
  private static final String TRANSLATION_KEY = "container.scrap_tech_workshop.recycler";
  private static final int NO_RECIPE_COOLDOWN = 40;
  private static final int DONE_DISPLAY_TIME = 20;
  public static BlockEntityType<RecyclerBlockEntity> TYPE;
  private final ItemStack[] items = new ItemStack[TOTAL_SLOTS];
  private int progress = 0;
  private int maxProgress = RecyclerConfig.processTime;
  private final ContainerData containerData =
      new ContainerData() {
        @Override
        public int get(int index) {
          return switch (index) {
            case PROGRESS_DATA_INDEX -> progress;
            case MAX_PROGRESS_DATA_INDEX -> maxProgress;
            default -> 0;
          };
        }

        @Override
        public void set(int index, int value) {
          switch (index) {
            case PROGRESS_DATA_INDEX -> progress = value;
            case MAX_PROGRESS_DATA_INDEX -> maxProgress = value;
          }
        }

        @Override
        public int getCount() {
          return 2;
        }
      };
  private int noRecipeTimer = 0;
  private int doneTimer = 0;
  private RecyclerRecipe currentRecipe = null;
  private int tickCounter = 0;

  public RecyclerBlockEntity(BlockPos pos, BlockState blockState) {
    super(TYPE, pos, blockState);
    Arrays.fill(items, ItemStack.EMPTY);
  }

  public static void tick(
      Level level, BlockPos pos, BlockState state, RecyclerBlockEntity blockEntity) {
    if (level.isClientSide) {
      return;
    }

    blockEntity.tickCounter++;
    RecyclerStatus currentStatus = state.getValue(RecyclerBlock.STATUS);

    TickResult result =
        switch (currentStatus) {
          case NO_RECIPE -> blockEntity.handleNoRecipeStatus();
          case DONE -> blockEntity.handleDoneStatus();
          case IDLE, WORKING, ERROR -> blockEntity.handleActiveStatus(level, pos, state);
        };

    // Update block status if changed
    if (result.newStatus != currentStatus) {
      RecyclerBlock.updateStatus(level, pos, result.newStatus);
    }

    // Sync to client if needed
    if (result.hasChanged && blockEntity.tickCounter % RecyclerConfig.progressUpdateInterval == 0) {
      blockEntity.setChanged();
    }
  }

  private TickResult handleNoRecipeStatus() {
    noRecipeTimer--;
    if (noRecipeTimer <= 0) {
      return new TickResult(RecyclerStatus.IDLE, true);
    }
    return new TickResult(RecyclerStatus.NO_RECIPE, false);
  }

  private TickResult handleDoneStatus() {
    doneTimer--;
    if (doneTimer <= 0) {
      currentRecipe = findRecipe();
      if (currentRecipe != null && canProcessCurrentRecipe()) {
        progress = 0;
        return new TickResult(RecyclerStatus.WORKING, true);
      } else {
        return new TickResult(RecyclerStatus.IDLE, true);
      }
    }
    return new TickResult(RecyclerStatus.DONE, false);
  }

  private TickResult handleActiveStatus(Level level, BlockPos pos, BlockState state) {
    boolean hasChanged = false;
    RecyclerStatus newStatus = state.getValue(RecyclerBlock.STATUS);

    // Check if we need to find a new recipe or if current recipe is no longer valid
    if (currentRecipe == null || !canProcessCurrentRecipe()) {
      currentRecipe = findRecipe();

      // If no recipe found but we have input, eject it
      if (currentRecipe == null && !getInputStack().isEmpty()) {
        ejectInputItem(level, pos, state);
        noRecipeTimer = NO_RECIPE_COOLDOWN;
        return new TickResult(RecyclerStatus.NO_RECIPE, true);
      }

      progress = 0;
      hasChanged = true;
    }

    // Process current recipe if possible
    if (currentRecipe != null && canProcessCurrentRecipe()) {
      progress++;
      newStatus = RecyclerStatus.WORKING;
      hasChanged = true;

      // Recipe completed
      if (progress >= maxProgress) {
        processRecipe();
        progress = 0;
        currentRecipe = null;
        newStatus = RecyclerStatus.DONE;
        doneTimer = DONE_DISPLAY_TIME;
      }
    } else if (currentRecipe != null && !canProcessCurrentRecipe()) {
      // Have recipe but can't process (likely output full)
      ItemStack inputStack = getInputStack();
      if (!inputStack.isEmpty() && currentRecipe.matchesInput(inputStack)) {
        newStatus = RecyclerStatus.ERROR; // Output full
      } else {
        newStatus = RecyclerStatus.IDLE;
      }
      progress = 0;
      hasChanged = true;
    } else if (progress > 0) {
      // No recipe but still have progress - decay it
      if (findRecipe() == null) {
        newStatus = RecyclerStatus.IDLE;
      }
      progress = Math.max(0, progress - 2);
      hasChanged = true;

      if (progress == 0) {
        newStatus = RecyclerStatus.IDLE;
      }
    } else {
      newStatus = RecyclerStatus.IDLE;
    }

    return new TickResult(newStatus, hasChanged);
  }

  private RecyclerRecipe findRecipe() {
    if (level == null || getInputStack().isEmpty()) {
      return null;
    }

    return RecyclerRecipeSelector.selectBestRecipe(level, getInputStack()).orElse(null);
  }

  private boolean canProcessCurrentRecipe() {
    if (currentRecipe == null) {
      return false;
    }

    ItemStack inputStack = getInputStack();
    if (inputStack.isEmpty()) {
      return false;
    }

    if (!currentRecipe.matchesInput(inputStack)) {
      return false;
    }

    return canInsertOutputs(currentRecipe.getOutputsForInput(inputStack));
  }

  private void processRecipe() {
    if (currentRecipe == null || level == null) {
      return;
    }

    ItemStack inputStack = getInputStack();
    if (inputStack.isEmpty()) {
      return;
    }

    List<ItemStack> outputs = currentRecipe.getOutputsForInput(inputStack);
    outputs.forEach(this::insertOutput);
    inputStack.shrink(1);
    setChanged();

    log.debug("Processed item with recipe: {} -> {} outputs", inputStack.getItem(), outputs.size());
  }

  private void ejectInputItem(Level level, BlockPos pos, BlockState state) {
    ItemStack inputStack = getInputStack();
    if (inputStack.isEmpty()) {
      return;
    }

    Direction backDirection = state.getValue(RecyclerBlock.FACING).getOpposite();
    BlockPos ejectPos = pos.relative(backDirection);

    ItemStack itemToEject = inputStack.copy();
    itemToEject.setCount(1);
    getInputStack().shrink(1);

    Containers.dropItemStack(
        level, ejectPos.getX() + 0.5, ejectPos.getY() + 0.5, ejectPos.getZ() + 0.5, itemToEject);

    log.debug("Ejected item (no recipe): {} at position {}", itemToEject.getItem(), ejectPos);

    addEjectionFeedback(level, pos, ejectPos);

    setChanged();
  }

  private void addEjectionFeedback(Level level, BlockPos recyclerPos, BlockPos ejectPos) {
    if (level.isClientSide) {
      return;
    }

    level.playSound(null, recyclerPos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 0.5f, 1.2f);

    if (level instanceof ServerLevel serverLevel) {
      serverLevel.sendParticles(
          ParticleTypes.SMOKE,
          ejectPos.getX() + 0.5,
          ejectPos.getY() + 0.5,
          ejectPos.getZ() + 0.5,
          3,
          0.2,
          0.1,
          0.2,
          0.02);
    }
  }

  private ItemStack getInputStack() {
    return getItem(INPUT_SLOT);
  }

  private boolean canInsertOutputs(List<ItemStack> outputs) {
    // Simulate insertion
    ItemStack[] simulatedItems = new ItemStack[TOTAL_SLOTS];
    for (int i = 0; i < TOTAL_SLOTS; i++) {
      simulatedItems[i] = items[i].copy();
    }

    for (ItemStack output : outputs) {
      if (!tryInsertOutput(simulatedItems, output, true)) {
        return false;
      }
    }
    return true;
  }

  private boolean tryInsertOutput(ItemStack[] itemArray, ItemStack output, boolean isSimulation) {
    ItemStack remaining = output.copy();

    for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT && !remaining.isEmpty(); i++) {
      ItemStack slotStack = itemArray[i];

      if (slotStack.isEmpty()) {
        itemArray[i] = remaining.copy();
        remaining = ItemStack.EMPTY;
      } else if (ItemStack.isSameItemSameTags(slotStack, remaining)) {
        int maxStackSize = slotStack.getMaxStackSize();
        int canAdd = maxStackSize - slotStack.getCount();
        int toAdd = Math.min(canAdd, remaining.getCount());

        if (isSimulation) {
          slotStack.grow(toAdd);
        } else {
          slotStack.grow(toAdd);
        }
        remaining.shrink(toAdd);
      }
    }

    return remaining.isEmpty();
  }

  private void insertOutput(ItemStack output) {
    tryInsertOutput(items, output, false);
  }

  @Override
  public void load(CompoundTag compoundTag) {
    super.load(compoundTag);

    for (int i = 0; i < TOTAL_SLOTS; i++) {
      if (compoundTag.contains(ITEM_TAG_PREFIX + i)) {
        items[i] = ItemStack.of(compoundTag.getCompound(ITEM_TAG_PREFIX + i));
      } else {
        items[i] = ItemStack.EMPTY;
      }
    }

    progress = compoundTag.getInt(PROGRESS_TAG);
    maxProgress = compoundTag.getInt(MAX_PROGRESS_TAG);
    noRecipeTimer = compoundTag.getInt(NO_RECIPE_TIMER_TAG);
    doneTimer = compoundTag.getInt(DONE_TIMER_TAG);
  }

  @Override
  protected void saveAdditional(CompoundTag compoundTag) {
    super.saveAdditional(compoundTag);

    for (int i = 0; i < TOTAL_SLOTS; i++) {
      if (!items[i].isEmpty()) {
        compoundTag.put(ITEM_TAG_PREFIX + i, items[i].save(new CompoundTag()));
      }
    }

    compoundTag.putInt(PROGRESS_TAG, progress);
    compoundTag.putInt(MAX_PROGRESS_TAG, maxProgress);
    compoundTag.putInt(NO_RECIPE_TIMER_TAG, noRecipeTimer);
    compoundTag.putInt(DONE_TIMER_TAG, doneTimer);
  }

  @Override
  public int getContainerSize() {
    return TOTAL_SLOTS;
  }

  @Override
  public boolean isEmpty() {
    for (ItemStack item : items) {
      if (!item.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getItem(int slot) {
    if (slot < 0 || slot >= TOTAL_SLOTS) {
      return ItemStack.EMPTY;
    }
    return items[slot];
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    if (slot < 0 || slot >= TOTAL_SLOTS || items[slot].isEmpty()) {
      return ItemStack.EMPTY;
    }

    ItemStack result = items[slot].split(amount);
    if (items[slot].isEmpty()) {
      items[slot] = ItemStack.EMPTY;
    }
    setChanged();
    return result;
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    if (slot < 0 || slot >= TOTAL_SLOTS) {
      return ItemStack.EMPTY;
    }

    ItemStack result = items[slot];
    items[slot] = ItemStack.EMPTY;
    return result;
  }

  @Override
  public void setItem(int slot, ItemStack itemStack) {
    if (slot >= 0 && slot < TOTAL_SLOTS) {
      items[slot] = itemStack;
      if (!itemStack.isEmpty() && itemStack.getCount() > getMaxStackSize()) {
        itemStack.setCount(getMaxStackSize());
      }
      setChanged();
    }
  }

  @Override
  public boolean stillValid(Player player) {
    return Container.stillValidBlockEntity(this, player);
  }

  @Override
  public void clearContent() {
    for (int i = 0; i < TOTAL_SLOTS; i++) {
      items[i] = ItemStack.EMPTY;
    }
    setChanged();
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable(TRANSLATION_KEY);
  }

  @Override
  public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
    return new RecyclerMenu(windowId, playerInventory, this, containerData);
  }

  public int getRedstoneSignal() {
    if (maxProgress <= 0) {
      return 0;
    }
    return (progress * 15) / maxProgress;
  }

  public ContainerData getContainerData() {
    return containerData;
  }

  private void syncToClient() {
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
  }

  @Override
  public void setChanged() {
    super.setChanged();
    syncToClient();
  }

  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag compoundTag = new CompoundTag();
    saveAdditional(compoundTag);
    return compoundTag;
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public int[] getSlotsForFace(Direction direction) {
    if (direction == Direction.UP) {
      return new int[] {INPUT_SLOT};
    } else if (direction == Direction.DOWN || direction == getBackDirection()) {
      return new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
    }
    return new int[] {};
  }

  @Override
  public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
    return slot == INPUT_SLOT && direction == Direction.UP;
  }

  @Override
  public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
    return slot >= FIRST_OUTPUT_SLOT
        && slot <= LAST_OUTPUT_SLOT
        && (direction == Direction.DOWN || direction == getBackDirection());
  }

  private Direction getBackDirection() {
    if (getBlockState().getBlock() instanceof RecyclerBlock) {
      return getBlockState().getValue(RecyclerBlock.FACING).getOpposite();
    }
    return Direction.NORTH;
  }

  private static class TickResult {
    final RecyclerStatus newStatus;
    final boolean hasChanged;

    TickResult(RecyclerStatus newStatus, boolean hasChanged) {
      this.newStatus = newStatus;
      this.hasChanged = hasChanged;
    }
  }
}
