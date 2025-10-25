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

import de.markusbordihn.scraptechworkshop.block.entity.AbstractWorkshopBlockEntity;
import de.markusbordihn.scraptechworkshop.block.recycler.RecyclerBlock;
import de.markusbordihn.scraptechworkshop.config.RecyclerConfig;
import de.markusbordihn.scraptechworkshop.data.recycler.RecyclerStatus;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerConsumer;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerData;
import de.markusbordihn.scraptechworkshop.item.upgrade.SpeedUpgradeItem;
import de.markusbordihn.scraptechworkshop.menu.RecyclerMenu;
import de.markusbordihn.scraptechworkshop.recipe.recycler.RecyclerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RecyclerBlockEntity extends AbstractWorkshopBlockEntity
    implements EnergyPowerConsumer {

  public static final String ID = "recycler";

  private static final int ENERGY_CAPACITY_MAH = 10000;
  private static final int ENERGY_CHARGE_INTERVAL = 5;
  private static final int ENERGY_CONSUMPTION_PER_TICK = 5;
  private static final int ENERGY_CONSUMPTION_INTERVAL = 10;

  private static final String PROGRESS_TAG = "Progress";
  private static final String MAX_PROGRESS_TAG = "MaxProgress";
  private static final String NO_RECIPE_TIMER_TAG = "NoRecipeTimer";
  private static final String DONE_TIMER_TAG = "DoneTimer";

  private static final int PROGRESS_DATA_INDEX = 0;
  private static final int MAX_PROGRESS_DATA_INDEX = 1;

  private static final String TRANSLATION_KEY = "container.scrap_tech_workshop.recycler";
  public static BlockEntityType<RecyclerBlockEntity> TYPE;

  private final RecyclerContainer container;
  private final int tickOffset;
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
  private EnergyPowerData energyData = EnergyPowerData.empty();
  private int noRecipeTimer = 0;
  private int doneTimer = 0;
  private RecyclerRecipe currentRecipe = null;
  private int tickCounter = 0;

  public RecyclerBlockEntity(final BlockPos blockPos, final BlockState blockState) {
    super(TYPE, blockPos, blockState);
    this.container = new RecyclerContainer(RecyclerSlots.TOTAL_SLOTS, this::setChanged);
    this.tickOffset =
        Math.abs(
            (blockPos.getX() * 31 + blockPos.getY() * 17 + blockPos.getZ() * 13)
                % ENERGY_CHARGE_INTERVAL);
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

    if ((blockEntity.tickCounter + blockEntity.tickOffset) % ENERGY_CHARGE_INTERVAL == 0) {
      blockEntity.chargeFromBattery(blockEntity.getEnergyTransferRate());
    }

    RecyclerStatus currentStatus = blockState.getValue(RecyclerBlock.STATUS);

    // Check if we have enough energy to work
    boolean hasEnoughEnergy = blockEntity.getCurrentEnergy() >= ENERGY_CONSUMPTION_PER_TICK;
    int speedMultiplier = blockEntity.getSpeedMultiplierBonus();

    RecyclerState recyclerState =
        new RecyclerState(
            level,
            blockPos,
            blockEntity.container.getItems(),
            blockEntity.progress,
            blockEntity.maxProgress,
            blockEntity.noRecipeTimer,
            blockEntity.doneTimer,
            blockEntity.currentRecipe,
            speedMultiplier,
            blockEntity.getCurrentEnergy(),
            blockEntity.tickCounter);

    RecyclerTickResult result;

    // Check if we're currently working without energy
    if (!hasEnoughEnergy && currentStatus == RecyclerStatus.WORKING) {
      recyclerState.progress = 0;
      result = new RecyclerTickResult(RecyclerStatus.IDLE, true);
    } else {
      result =
          switch (currentStatus) {
            case NO_RECIPE -> RecyclerTickProcessor.processNoRecipeStatus(recyclerState);
            case DONE -> RecyclerTickProcessor.processDoneStatus(recyclerState);
            case IDLE, WORKING, ERROR ->
                RecyclerTickProcessor.processActiveStatus(recyclerState, blockState);
          };

      if (result.newStatus() == RecyclerStatus.WORKING && !hasEnoughEnergy) {
        recyclerState.progress = 0;
        result = new RecyclerTickResult(RecyclerStatus.IDLE, false);
      }

      if (result.newStatus() == RecyclerStatus.WORKING && hasEnoughEnergy) {
        if (blockEntity.tickCounter % ENERGY_CONSUMPTION_INTERVAL == 0) {
          blockEntity.consumeEnergy(ENERGY_CONSUMPTION_PER_TICK);
        }
      }
    }

    blockEntity.progress = recyclerState.progress;
    blockEntity.noRecipeTimer = recyclerState.noRecipeTimer;
    blockEntity.doneTimer = recyclerState.doneTimer;
    blockEntity.currentRecipe = recyclerState.currentRecipe;

    if (result.newStatus() != currentStatus) {
      RecyclerBlock.updateStatus(level, blockPos, result.newStatus());
    }

    if (result.hasChanged()
        && blockEntity.tickCounter % RecyclerConfig.progressUpdateInterval == 0) {
      blockEntity.setChanged();
    }
  }

  public RecyclerContainer getContainer() {
    return container;
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return container.getItems();
  }

  @Override
  protected WorldlyContainer getContainerDelegate() {
    return container;
  }

  @Override
  public void load(CompoundTag compoundTag) {
    super.load(compoundTag);
    progress = compoundTag.getInt(PROGRESS_TAG);
    maxProgress = compoundTag.getInt(MAX_PROGRESS_TAG);
    noRecipeTimer = compoundTag.getInt(NO_RECIPE_TIMER_TAG);
    doneTimer = compoundTag.getInt(DONE_TIMER_TAG);
    loadEnergyPowerConsumer(compoundTag);
    energyData =
        new EnergyPowerData(
            energyData.currentEnergy(),
            container.getItem(RecyclerSlots.BATTERY_SLOT),
            energyData.debounceData());
  }

  @Override
  protected void saveAdditional(CompoundTag compoundTag) {
    super.saveAdditional(compoundTag);
    compoundTag.putInt(PROGRESS_TAG, progress);
    compoundTag.putInt(MAX_PROGRESS_TAG, maxProgress);
    compoundTag.putInt(NO_RECIPE_TIMER_TAG, noRecipeTimer);
    compoundTag.putInt(DONE_TIMER_TAG, doneTimer);
    saveEnergyPowerConsumer(compoundTag);
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
    Level currentLevel = getLevel();
    if (currentLevel != null && !currentLevel.isClientSide) {
      currentLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
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
    for (int i = RecyclerSlots.FIRST_UPGRADE_SLOT; i <= RecyclerSlots.LAST_UPGRADE_SLOT; i++) {
      ItemStack stack = container.getItem(i);
      if (!stack.isEmpty() && stack.getItem() instanceof SpeedUpgradeItem speedUpgrade) {
        totalMultiplier += speedUpgrade.getSpeedMultiplier() - 1;
      }
    }
    return totalMultiplier;
  }

  @Override
  public EnergyPowerData getEnergyData() {
    return new EnergyPowerData(
        energyData.currentEnergy(),
        container.getItem(RecyclerSlots.BATTERY_SLOT),
        energyData.debounceData());
  }

  @Override
  public void setEnergyData(EnergyPowerData data) {
    this.energyData =
        new EnergyPowerData(data.currentEnergy(), data.battery(), data.debounceData());
    container.setItem(RecyclerSlots.BATTERY_SLOT, data.battery());
    setChanged();
  }

  @Override
  public int getEnergyCapacity() {
    return ENERGY_CAPACITY_MAH;
  }

  @Override
  public void markDirty() {
    setChanged();
  }
}
