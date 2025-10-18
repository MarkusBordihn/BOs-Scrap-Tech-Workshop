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
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerConsumer;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerData;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import de.markusbordihn.scraptechworkshop.item.component.EmptyEnergyCellItem;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
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
  private static final int ENERGY_CHARGE_INTERVAL = 5;
  private static final int ENERGY_CONSUMPTION_PER_TICK = 5;
  private static final int RECHARGE_RATE = 5;

  private static final String PROGRESS_TAG = "Progress";
  private static final String MAX_PROGRESS_TAG = "MaxProgress";

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

    if (blockEntity.tickCounter % ENERGY_CHARGE_INTERVAL == 0) {
      blockEntity.chargeFromBattery(blockEntity.getEnergyTransferRate());
    }

    RechargeStationStatus currentStatus = blockState.getValue(RechargeStationBlock.STATUS);

    boolean hasEnoughEnergy = blockEntity.getCurrentEnergy() >= ENERGY_CONSUMPTION_PER_TICK;
    ItemStack inputItem = blockEntity.container.getItem(RechargeStationSlots.INPUT_SLOT);

    RechargeStationStatus newStatus = determineStatus(hasEnoughEnergy, inputItem);

    if (newStatus != currentStatus) {
      level.setBlock(blockPos, blockState.setValue(RechargeStationBlock.STATUS, newStatus), 3);
      blockEntity.progress = 0;
    }

    if (newStatus == RechargeStationStatus.CHARGING && hasEnoughEnergy) {
      if (inputItem.getItem() instanceof EmptyEnergyCellItem) {
        ItemStack chargedCell = new ItemStack(ModItems.ENERGY_CELL.get());
        if (chargedCell.getItem() instanceof EnergyCellItem energyCellItem) {
          energyCellItem.setEnergy(chargedCell, RECHARGE_RATE);
          blockEntity.container.setItem(RechargeStationSlots.INPUT_SLOT, chargedCell);
          blockEntity.consumeEnergy(ENERGY_CONSUMPTION_PER_TICK);
          blockEntity.progress = 1;
          blockEntity.maxProgress = 100;
          blockEntity.setChanged();

          if (blockEntity.tickCounter % 10 == 0) {
            spawnChargingParticles(level, blockPos);
          }
          if (blockEntity.tickCounter % 20 == 0) {
            playChargingSound(level, blockPos);
          }
        }
      } else if (inputItem.getItem() instanceof EnergyCellItem batteryItem) {
        int currentBatteryEnergy = batteryItem.getEnergy(inputItem);
        int maxBatteryEnergy = EnergyCellItem.CAPACITY_MAH;

        if (currentBatteryEnergy < maxBatteryEnergy) {
          int energyToTransfer = Math.min(RECHARGE_RATE, maxBatteryEnergy - currentBatteryEnergy);
          batteryItem.setEnergy(inputItem, currentBatteryEnergy + energyToTransfer);

          blockEntity.consumeEnergy(ENERGY_CONSUMPTION_PER_TICK);

          blockEntity.progress = (currentBatteryEnergy * 100) / maxBatteryEnergy;
          blockEntity.maxProgress = 100;
          blockEntity.setChanged();

          if (blockEntity.tickCounter % 10 == 0) {
            spawnChargingParticles(level, blockPos);
          }
          if (blockEntity.tickCounter % 20 == 0) {
            playChargingSound(level, blockPos);
          }
        } else {
          level.setBlock(
              blockPos,
              blockState.setValue(RechargeStationBlock.STATUS, RechargeStationStatus.DONE),
              3);
        }
      }
    }

    if (newStatus == RechargeStationStatus.DONE || newStatus == RechargeStationStatus.IDLE) {
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

    if (inputItem.getItem() instanceof EmptyEnergyCellItem) {
      return RechargeStationStatus.CHARGING;
    }

    if (inputItem.getItem() instanceof EnergyCellItem batteryItem) {
      int currentEnergy = batteryItem.getEnergy(inputItem);
      int maxEnergy = EnergyCellItem.CAPACITY_MAH;

      if (currentEnergy >= maxEnergy) {
        return RechargeStationStatus.DONE;
      } else {
        return RechargeStationStatus.CHARGING;
      }
    }

    return RechargeStationStatus.IDLE;
  }

  private static void spawnChargingParticles(final Level level, final BlockPos blockPos) {
    if (level instanceof ServerLevel serverLevel) {
      double x = blockPos.getX() + 0.5;
      double z = blockPos.getZ() + 0.65;

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

  protected int getTotalSlots() {
    return RechargeStationSlots.TOTAL_SLOTS;
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
    loadEnergyPowerConsumer(compoundTag);
    energyData =
        new EnergyPowerData(
            energyData.currentEnergy(), container.getItem(RechargeStationSlots.BATTERY_SLOT));
  }

  @Override
  protected void saveAdditional(CompoundTag compoundTag) {
    super.saveAdditional(compoundTag);
    compoundTag.putInt(PROGRESS_TAG, progress);
    compoundTag.putInt(MAX_PROGRESS_TAG, maxProgress);
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
    return new EnergyPowerData(
        energyData.currentEnergy(), container.getItem(RechargeStationSlots.BATTERY_SLOT));
  }

  @Override
  public void setEnergyData(EnergyPowerData data) {
    this.energyData = new EnergyPowerData(data.currentEnergy(), data.battery());
    container.setItem(RechargeStationSlots.BATTERY_SLOT, data.battery());
    setChanged();
  }

  @Override
  public int getEnergyCapacity() {
    return ENERGY_CAPACITY_MAH;
  }

  @Override
  public int getBatterySlot() {
    return RechargeStationSlots.BATTERY_SLOT;
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

  public int getMaxProgress() {
    return maxProgress;
  }
}
