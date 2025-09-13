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

package de.markusbordihn.scraptechworkshop.block.entity.recycler;

import de.markusbordihn.scraptechworkshop.config.RecyclerConfig;
import de.markusbordihn.scraptechworkshop.menu.RecyclerMenu;
import de.markusbordihn.scraptechworkshop.recipe.recycler.RecyclerRecipe;
import de.markusbordihn.scraptechworkshop.recipe.recycler.RecyclerRecipeType;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RecyclerBlockEntity extends BlockEntity implements MenuProvider, Container {

  // Slot configuration
  public static final int INPUT_SLOTS = 1;
  public static final int OUTPUT_SLOTS = 9;
  public static final int UPGRADE_SLOTS = 2;
  public static final int TOTAL_SLOTS = INPUT_SLOTS + OUTPUT_SLOTS + UPGRADE_SLOTS;
  public static BlockEntityType<RecyclerBlockEntity> TYPE;
  // Simple inventory implementation
  private final ItemStack[] items = new ItemStack[TOTAL_SLOTS];

  // Processing state
  private int progress = 0;
  private int maxProgress = RecyclerConfig.processTime;
  // Container data for client sync
  private final ContainerData containerData =
      new ContainerData() {
        @Override
        public int get(int index) {
          return switch (index) {
            case 0 -> progress;
            case 1 -> maxProgress;
            default -> 0;
          };
        }

        @Override
        public void set(int index, int value) {
          switch (index) {
            case 0 -> progress = value;
            case 1 -> maxProgress = value;
          }
        }

        @Override
        public int getCount() {
          return 2;
        }
      };
  private RecyclerRecipe currentRecipe = null;
  private int tickCounter = 0;

  public RecyclerBlockEntity(BlockPos pos, BlockState blockState) {
    super(TYPE, pos, blockState);
    // Initialize all slots as empty
    for (int i = 0; i < TOTAL_SLOTS; i++) {
      items[i] = ItemStack.EMPTY;
    }
  }

  public static void tick(
      Level level, BlockPos pos, BlockState state, RecyclerBlockEntity blockEntity) {
    if (level.isClientSide) {
      return;
    }

    blockEntity.tickCounter++;
    boolean hasChanged = false;

    // Check for recipe and process
    if (blockEntity.currentRecipe == null || !blockEntity.canProcessCurrentRecipe()) {
      blockEntity.currentRecipe = blockEntity.findRecipe();
      blockEntity.progress = 0;
      hasChanged = true;
    }

    if (blockEntity.currentRecipe != null && blockEntity.canProcessCurrentRecipe()) {
      blockEntity.progress++;
      hasChanged = true;

      if (blockEntity.progress >= blockEntity.maxProgress) {
        blockEntity.processRecipe();
        blockEntity.progress = 0;
        blockEntity.currentRecipe = null;
        hasChanged = true;
      }
    } else if (blockEntity.progress > 0) {
      blockEntity.progress = Math.max(0, blockEntity.progress - 2);
      hasChanged = true;
    }

    // Sync to client periodically
    if (hasChanged && blockEntity.tickCounter % RecyclerConfig.progressUpdateInterval == 0) {
      blockEntity.setChanged();
    }
  }

  private RecyclerRecipe findRecipe() {
    if (level == null || getInputStack().isEmpty()) {
      return null;
    }

    SimpleContainer container = new SimpleContainer(1);
    container.setItem(0, getInputStack());

    return level
        .getRecipeManager()
        .getRecipeFor(RecyclerRecipeType.INSTANCE, container, level)
        .orElse(null);
  }

  private boolean canProcessCurrentRecipe() {
    if (currentRecipe == null) {
      return false;
    }

    ItemStack inputStack = getInputStack();
    return !inputStack.isEmpty()
        && currentRecipe.matchesInput(inputStack)
        && canInsertOutputs(currentRecipe.getOutputsForInput(inputStack));
  }

  private void processRecipe() {
    if (currentRecipe == null || level == null || getInputStack().isEmpty()) {
      return;
    }

    List<ItemStack> outputs = currentRecipe.getOutputsForInput(getInputStack());
    outputs.forEach(this::insertOutput);
    getInputStack().shrink(1);
    setChanged();
  }

  private ItemStack getInputStack() {
    return getItem(0);
  }

  private boolean canInsertOutputs(List<ItemStack> outputs) {
    // Simulate insertion
    ItemStack[] simulatedItems = new ItemStack[TOTAL_SLOTS];
    for (int i = 0; i < TOTAL_SLOTS; i++) {
      simulatedItems[i] = items[i].copy();
    }

    for (ItemStack output : outputs) {
      if (!simulateInsertOutput(simulatedItems, output)) {
        return false;
      }
    }
    return true;
  }

  private boolean simulateInsertOutput(ItemStack[] simulatedItems, ItemStack output) {
    ItemStack remaining = output.copy();

    // Try to insert into output slots (slots 1-9)
    for (int i = 1; i <= OUTPUT_SLOTS && !remaining.isEmpty(); i++) {
      ItemStack slotStack = simulatedItems[i];
      if (slotStack.isEmpty()) {
        simulatedItems[i] = remaining.copy();
        remaining = ItemStack.EMPTY;
      } else if (ItemStack.isSameItemSameTags(slotStack, remaining)) {
        int maxStackSize = slotStack.getMaxStackSize();
        int canAdd = maxStackSize - slotStack.getCount();
        int toAdd = Math.min(canAdd, remaining.getCount());
        slotStack.grow(toAdd);
        remaining.shrink(toAdd);
      }
    }

    return remaining.isEmpty();
  }

  private void insertOutput(ItemStack output) {
    ItemStack remaining = output.copy();

    // Try to insert into output slots (slots 1-9)
    for (int i = 1; i <= OUTPUT_SLOTS && !remaining.isEmpty(); i++) {
      ItemStack slotStack = items[i];
      if (slotStack.isEmpty()) {
        items[i] = remaining.copy();
        remaining = ItemStack.EMPTY;
      } else if (ItemStack.isSameItemSameTags(slotStack, remaining)) {
        int maxStackSize = slotStack.getMaxStackSize();
        int canAdd = maxStackSize - slotStack.getCount();
        int toAdd = Math.min(canAdd, remaining.getCount());
        slotStack.grow(toAdd);
        remaining.shrink(toAdd);
      }
    }
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);

    // Load items
    for (int i = 0; i < TOTAL_SLOTS; i++) {
      if (tag.contains("Item" + i)) {
        items[i] = ItemStack.of(tag.getCompound("Item" + i));
      } else {
        items[i] = ItemStack.EMPTY;
      }
    }

    progress = tag.getInt("Progress");
    maxProgress = tag.getInt("MaxProgress");
  }

  @Override
  protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);

    // Save items
    for (int i = 0; i < TOTAL_SLOTS; i++) {
      if (!items[i].isEmpty()) {
        tag.put("Item" + i, items[i].save(new CompoundTag()));
      }
    }

    tag.putInt("Progress", progress);
    tag.putInt("MaxProgress", maxProgress);
  }

  // Container implementation
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
  public void setItem(int slot, ItemStack stack) {
    if (slot >= 0 && slot < TOTAL_SLOTS) {
      items[slot] = stack;
      if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) {
        stack.setCount(getMaxStackSize());
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

  // MenuProvider implementation
  @Override
  public Component getDisplayName() {
    return Component.translatable("container.scrap_tech_workshop.recycler");
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
}
