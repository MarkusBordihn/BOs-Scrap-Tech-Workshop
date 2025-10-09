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

import de.markusbordihn.scraptechworkshop.block.RecyclerBlock;
import de.markusbordihn.scraptechworkshop.config.RecyclerConfig;
import de.markusbordihn.scraptechworkshop.data.recycler.RecyclerStatus;
import de.markusbordihn.scraptechworkshop.item.upgrade.SpeedUpgradeItem;
import de.markusbordihn.scraptechworkshop.menu.RecyclerMenu;
import de.markusbordihn.scraptechworkshop.recipe.recycler.RecyclerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
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

public class RecyclerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {

  public static final String ID = "recycler";

  // Slot configuration
  private static final int INPUT_SLOTS = 1;
  private static final int OUTPUT_SLOTS = 9;
  private static final int UPGRADE_SLOTS = 2;
  private static final int TOTAL_SLOTS = INPUT_SLOTS + OUTPUT_SLOTS + UPGRADE_SLOTS;
  private static final int INPUT_SLOT = 0;
  private static final int FIRST_OUTPUT_SLOT = 1;
  private static final int LAST_OUTPUT_SLOT = 9;
  private static final int FIRST_UPGRADE_SLOT = 10;
  private static final int LAST_UPGRADE_SLOT = 11;

  // NBT tags
  private static final String PROGRESS_TAG = "Progress";
  private static final String MAX_PROGRESS_TAG = "MaxProgress";
  private static final String NO_RECIPE_TIMER_TAG = "NoRecipeTimer";
  private static final String DONE_TIMER_TAG = "DoneTimer";
  private static final String ITEM_TAG_PREFIX = "Item";

  // Container data indices
  private static final int PROGRESS_DATA_INDEX = 0;
  private static final int MAX_PROGRESS_DATA_INDEX = 1;

  private static final String TRANSLATION_KEY = "container.scrap_tech_workshop.recycler";
  public static BlockEntityType<RecyclerBlockEntity> TYPE;

  private final RecyclerContainer container;
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

  public RecyclerBlockEntity(final BlockPos blockPos, final BlockState blockState) {
    super(TYPE, blockPos, blockState);
    this.container = new RecyclerContainer(TOTAL_SLOTS, this::setChanged);
  }

  public static void tick(
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState,
      final RecyclerBlockEntity blockEntity) {
    if (level.isClientSide) {
      return;
    }

    blockEntity.tickCounter++;
    RecyclerStatus currentStatus = blockState.getValue(RecyclerBlock.STATUS);

    // Calculate speed multiplier from upgrade slots
    int speedMultiplier = blockEntity.getSpeedMultiplierBonus();

    RecyclerTickProcessor.RecyclerState recyclerState =
        new RecyclerTickProcessor.RecyclerState(
            level,
            blockPos,
            blockEntity.container.getItems(),
            blockEntity.progress,
            blockEntity.maxProgress,
            blockEntity.noRecipeTimer,
            blockEntity.doneTimer,
            blockEntity.currentRecipe,
            speedMultiplier);

    RecyclerTickProcessor.TickResult result =
        switch (currentStatus) {
          case NO_RECIPE -> RecyclerTickProcessor.processNoRecipeStatus(recyclerState);
          case DONE -> RecyclerTickProcessor.processDoneStatus(recyclerState);
          case IDLE, WORKING, ERROR ->
              RecyclerTickProcessor.processActiveStatus(recyclerState, blockState);
        };

    blockEntity.progress = recyclerState.progress;
    blockEntity.noRecipeTimer = recyclerState.noRecipeTimer;
    blockEntity.doneTimer = recyclerState.doneTimer;
    blockEntity.currentRecipe = recyclerState.currentRecipe;

    if (result.newStatus != currentStatus) {
      RecyclerBlock.updateStatus(level, blockPos, result.newStatus);
    }

    if (result.hasChanged && blockEntity.tickCounter % RecyclerConfig.progressUpdateInterval == 0) {
      blockEntity.setChanged();
    }
  }

  public RecyclerContainer getContainer() {
    return container;
  }

  @Override
  public void load(CompoundTag compoundTag) {
    super.load(compoundTag);

    for (int i = 0; i < TOTAL_SLOTS; i++) {
      if (compoundTag.contains(ITEM_TAG_PREFIX + i)) {
        container.getItems()[i] = ItemStack.of(compoundTag.getCompound(ITEM_TAG_PREFIX + i));
      } else {
        container.getItems()[i] = ItemStack.EMPTY;
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
      if (!container.getItems()[i].isEmpty()) {
        compoundTag.put(ITEM_TAG_PREFIX + i, container.getItems()[i].save(new CompoundTag()));
      }
    }

    compoundTag.putInt(PROGRESS_TAG, progress);
    compoundTag.putInt(MAX_PROGRESS_TAG, maxProgress);
    compoundTag.putInt(NO_RECIPE_TIMER_TAG, noRecipeTimer);
    compoundTag.putInt(DONE_TIMER_TAG, doneTimer);
  }

  public int getContainerSize() {
    return container.getContainerSize();
  }

  public boolean isEmpty() {
    return container.isEmpty();
  }

  public ItemStack getItem(int slot) {
    return container.getItem(slot);
  }

  public ItemStack removeItem(int slot, int amount) {
    return container.removeItem(slot, amount);
  }

  public ItemStack removeItemNoUpdate(int slot) {
    return container.removeItemNoUpdate(slot);
  }

  public void setItem(final int slot, final ItemStack itemStack) {
    container.setItem(slot, itemStack);
  }

  public boolean stillValid(final Player player) {
    return Container.stillValidBlockEntity(this, player);
  }

  public boolean canPlaceItem(final int slot, final ItemStack itemStack) {
    return container.canPlaceItem(slot, itemStack);
  }

  public void clearContent() {
    container.clearContent();
  }

  @Override
  public int[] getSlotsForFace(Direction direction) {
    return container.getSlotsForFace(direction);
  }

  @Override
  public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
    return container.canPlaceItemThroughFace(slot, itemStack, direction);
  }

  @Override
  public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
    return container.canTakeItemThroughFace(slot, itemStack, direction);
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

  private int getSpeedMultiplierBonus() {
    int totalMultiplier = 1;
    for (int i = FIRST_UPGRADE_SLOT; i <= LAST_UPGRADE_SLOT; i++) {
      ItemStack stack = container.getItem(i);
      if (!stack.isEmpty() && stack.getItem() instanceof SpeedUpgradeItem speedUpgrade) {
        totalMultiplier += speedUpgrade.getSpeedMultiplier() - 1;
      }
    }
    return totalMultiplier;
  }
}
