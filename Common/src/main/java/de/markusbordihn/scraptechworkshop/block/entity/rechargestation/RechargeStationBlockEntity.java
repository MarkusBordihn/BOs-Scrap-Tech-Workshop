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

package de.markusbordihn.scraptechworkshop.block.entity.rechargestation;

import de.markusbordihn.scraptechworkshop.block.entity.AbstractWorkshopBlockEntity;
import de.markusbordihn.scraptechworkshop.block.rechargestation.RechargeStationBlock;
import de.markusbordihn.scraptechworkshop.data.rechargestation.RechargeStationStatus;
import de.markusbordihn.scraptechworkshop.energy.EnergyCell;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerConsumer;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerData;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import de.markusbordihn.scraptechworkshop.item.component.EmptyEnergyCellBlockItem;
import de.markusbordihn.scraptechworkshop.item.component.EmptyEnergyCellItem;
import de.markusbordihn.scraptechworkshop.menu.RechargeStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RechargeStationBlockEntity extends AbstractWorkshopBlockEntity
    implements EnergyPowerConsumer {

  public static final String ID = "recharge_station";

  private static final int ENERGY_CAPACITY_MAH = 10000;
  private static final int ENERGY_CONSUMPTION_PER_TICK = 5;
  private static final int RECHARGE_RATE = 5;
  private static final int STATUS_CHANGE_DELAY = 20;

  private static final String PROGRESS_TAG = "Progress";
  private static final String MAX_PROGRESS_TAG = "MaxProgress";
  private static final String STATUS_DELAY_TIMER_TAG = "StatusDelayTimer";

  private static final int PROGRESS_DATA_INDEX = 0;
  private static final int MAX_PROGRESS_DATA_INDEX = 1;

  private static final String TRANSLATION_KEY = "container.scrap_tech_workshop.recharge_station";
  public static BlockEntityType<RechargeStationBlockEntity> TYPE;

  private final RechargeStationContainer container;
  private int progress = 0;
  private int maxProgress = 100;
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
  private int tickCounter = 0;
  private int statusDelayTimer = 0;
  private RechargeStationStatus pendingStatus = null;

  public RechargeStationBlockEntity(final BlockPos blockPos, final BlockState blockState) {
    super(TYPE, blockPos, blockState);
    this.container =
        new RechargeStationContainer(RechargeStationSlots.TOTAL_SLOTS, this::setChanged);
  }

  public static void tick(
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState,
      final RechargeStationBlockEntity blockEntity) {
    if (level.isClientSide) {
      return;
    }

    blockEntity.tickCounter++;
    blockEntity.updateEnergyFlow(level.getGameTime());

    boolean hasEnoughEnergy = blockEntity.hasStableEnergy(ENERGY_CONSUMPTION_PER_TICK);
    ItemStack inputItem = blockEntity.container.getItem(RechargeStationSlots.INPUT_SLOT);
    RechargeStationStatus newStatus = determineStatus(hasEnoughEnergy, inputItem);

    updateBlockStatus(level, blockPos, blockState, blockEntity, newStatus);
    processCharging(level, blockPos, blockState, blockEntity, newStatus, hasEnoughEnergy, inputItem);
    resetProgressIfNeeded(blockEntity, newStatus);
  }

  private static void updateBlockStatus(
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState,
      final RechargeStationBlockEntity blockEntity,
      final RechargeStationStatus newStatus) {
    RechargeStationStatus currentStatus = blockState.getValue(RechargeStationBlock.STATUS);

    if (newStatus == currentStatus) {
      blockEntity.statusDelayTimer = 0;
      blockEntity.pendingStatus = null;
      return;
    }

    if (blockEntity.pendingStatus != newStatus) {
      blockEntity.pendingStatus = newStatus;
      blockEntity.statusDelayTimer = 0;
      return;
    }

    blockEntity.statusDelayTimer++;
    if (blockEntity.statusDelayTimer >= STATUS_CHANGE_DELAY) {
      level.setBlock(blockPos, blockState.setValue(RechargeStationBlock.STATUS, newStatus), 3);
      blockEntity.progress = 0;
      blockEntity.statusDelayTimer = 0;
      blockEntity.pendingStatus = null;
    }
  }

  private static void processCharging(
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState,
      final RechargeStationBlockEntity blockEntity,
      final RechargeStationStatus status,
      final boolean hasEnoughEnergy,
      final ItemStack inputItem) {
    if (status != RechargeStationStatus.CHARGING || !hasEnoughEnergy) {
      return;
    }

    if (inputItem.getItem() instanceof EmptyEnergyCellBlockItem) {
      chargeEmptyCell(level, blockPos, blockState, blockEntity, ModItems.ENERGY_CELL_BLOCK.get());
    } else if (inputItem.getItem() instanceof EmptyEnergyCellItem) {
      chargeEmptyCell(level, blockPos, blockState, blockEntity, ModItems.ENERGY_CELL.get());
    } else if (inputItem.getItem() instanceof EnergyCell cell) {
      chargeEnergyCell(level, blockPos, blockState, blockEntity, inputItem, cell);
    }
  }

  private static void chargeEmptyCell(
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState,
      final RechargeStationBlockEntity blockEntity,
      final net.minecraft.world.item.Item cellItem) {
    ItemStack chargedCell = new ItemStack(cellItem);
    if (!(chargedCell.getItem() instanceof EnergyCell energyCellItem)) {
      return;
    }

    energyCellItem.setEnergy(chargedCell, RECHARGE_RATE);
    blockEntity.container.setItem(RechargeStationSlots.INPUT_SLOT, chargedCell);
    blockEntity.consumeEnergy(ENERGY_CONSUMPTION_PER_TICK);
    blockEntity.progress = 1;
    blockEntity.maxProgress = 100;
    blockEntity.setChanged();

    playChargingEffects(level, blockPos, blockState, blockEntity);
  }

  private static void chargeEnergyCell(
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState,
      final RechargeStationBlockEntity blockEntity,
      final ItemStack inputItem,
      final EnergyCell cell) {
    int currentBatteryEnergy = cell.getEnergy(inputItem);
    int maxBatteryEnergy = cell.getCapacity();

    if (currentBatteryEnergy >= maxBatteryEnergy) {
      level.setBlock(
          blockPos,
          blockState.setValue(RechargeStationBlock.STATUS, RechargeStationStatus.DONE),
          3);
      return;
    }

    int energyToTransfer = Math.min(RECHARGE_RATE, maxBatteryEnergy - currentBatteryEnergy);
    cell.setEnergy(inputItem, currentBatteryEnergy + energyToTransfer);
    blockEntity.consumeEnergy(ENERGY_CONSUMPTION_PER_TICK);
    blockEntity.progress = (currentBatteryEnergy * 100) / maxBatteryEnergy;
    blockEntity.maxProgress = 100;
    blockEntity.setChanged();

    playChargingEffects(level, blockPos, blockState, blockEntity);
  }

  private static void playChargingEffects(
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState,
      final RechargeStationBlockEntity blockEntity) {
    if (blockEntity.tickCounter % 10 == 0) {
      spawnChargingParticles(level, blockPos, blockState);
    }
    if (blockEntity.tickCounter % 20 == 0) {
      playChargingSound(level, blockPos);
    }
  }

  private static void resetProgressIfNeeded(
      final RechargeStationBlockEntity blockEntity, final RechargeStationStatus status) {
    if (status == RechargeStationStatus.DONE || status == RechargeStationStatus.IDLE) {
      blockEntity.progress = 0;
    }
  }

  private static RechargeStationStatus determineStatus(
      final boolean hasEnoughEnergy, final ItemStack inputItem) {
    if (!hasEnoughEnergy) {
      return RechargeStationStatus.NO_POWER;
    }

    if (inputItem.isEmpty()) {
      return RechargeStationStatus.IDLE;
    }

    if (inputItem.getItem() instanceof EmptyEnergyCellBlockItem) {
      return RechargeStationStatus.CHARGING;
    }

    if (inputItem.getItem() instanceof EmptyEnergyCellItem) {
      return RechargeStationStatus.CHARGING;
    }

    if (inputItem.getItem() instanceof EnergyCell cell) {
      int currentEnergy = cell.getEnergy(inputItem);
      int maxEnergy = cell.getCapacity();

      if (currentEnergy >= maxEnergy) {
        return RechargeStationStatus.DONE;
      } else {
        return RechargeStationStatus.CHARGING;
      }
    }

    return RechargeStationStatus.IDLE;
  }

  private static void spawnChargingParticles(
      final Level level, final BlockPos blockPos, final BlockState blockState) {
    if (level instanceof ServerLevel serverLevel) {
      // Calculate position based on facing direction
      double x = blockPos.getX() + 0.5;
      double z = blockPos.getZ() + 0.5;

      var facing = blockState.getValue(RechargeStationBlock.FACING);
      double offsetFromCenter = 0.15;

      switch (facing) {
        case NORTH -> z -= offsetFromCenter;
        case SOUTH -> z += offsetFromCenter;
        case WEST -> x -= offsetFromCenter;
        case EAST -> x += offsetFromCenter;
        default -> {}
      }

      for (int i = 0; i < 2; i++) {
        double offsetX = (level.random.nextDouble() - 0.5) * 0.2;
        double offsetZ = (level.random.nextDouble() - 0.5) * 0.2;

        serverLevel.sendParticles(
            ParticleTypes.ELECTRIC_SPARK,
            x + offsetX,
            blockPos.getY() + 0.75,
            z + offsetZ,
            1,
            0.0,
            0.05,
            0.0,
            0.01);

        serverLevel.sendParticles(
            ParticleTypes.ELECTRIC_SPARK,
            x + offsetX,
            blockPos.getY() + 0.20,
            z + offsetZ,
            1,
            0.0,
            0.05,
            0.0,
            0.01);
      }
    }
  }

  private static void playChargingSound(final Level level, final BlockPos blockPos) {
    level.playSound(null, blockPos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.15f, 1.8f);
  }

  public RechargeStationContainer getContainer() {
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
    statusDelayTimer = compoundTag.getInt(STATUS_DELAY_TIMER_TAG);
    loadEnergyPowerConsumer(compoundTag);
    energyData = getEnergyData();
  }

  @Override
  protected void saveAdditional(CompoundTag compoundTag) {
    super.saveAdditional(compoundTag);
    compoundTag.putInt(PROGRESS_TAG, progress);
    compoundTag.putInt(MAX_PROGRESS_TAG, maxProgress);
    compoundTag.putInt(STATUS_DELAY_TIMER_TAG, statusDelayTimer);
    saveEnergyPowerConsumer(compoundTag);
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable(TRANSLATION_KEY);
  }

  @Override
  public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
    return new RechargeStationMenu(windowId, playerInventory, this, containerData);
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
    return saveWithoutMetadata();
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public EnergyPowerData getEnergyData() {
    return energyData.withBattery(container.getItem(RechargeStationSlots.BATTERY_SLOT));
  }

  @Override
  public void setEnergyData(EnergyPowerData data) {
    this.energyData = data;
    container.setItem(RechargeStationSlots.BATTERY_SLOT, data.battery());
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

  public ItemStack getInputItem() {
    return container.getItem(RechargeStationSlots.INPUT_SLOT);
  }

  public int getProgress() {
    return progress;
  }
}
