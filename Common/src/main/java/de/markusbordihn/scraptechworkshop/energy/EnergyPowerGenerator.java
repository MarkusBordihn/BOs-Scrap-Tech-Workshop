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

package de.markusbordihn.scraptechworkshop.energy;

import de.markusbordihn.scraptechworkshop.item.ModItems;
import de.markusbordihn.scraptechworkshop.item.component.EmptyEnergyCellItem;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface EnergyPowerGenerator extends EnergyBatteryHandler {

  String ENERGY_TAG = "Energy";
  String BATTERY_TAG = "Battery";

  int TICKS_PER_SECOND = 20;

  int getEnergyCapacity();

  void markDirty();

  EnergyPowerData getEnergyData();

  void setEnergyData(final EnergyPowerData data);

  default int getCurrentEnergy() {
    return getEnergyData().currentEnergy();
  }

  default void setCurrentEnergy(final int energy) {
    setEnergyData(getEnergyData().withCurrentEnergy(energy));
  }

  default ItemStack getBattery() {
    return getEnergyData().battery();
  }

  default void setBattery(final ItemStack battery) {
    setEnergyData(getEnergyData().withBattery(battery));
  }

  default int getEnergyTransferRate() {
    return 50;
  }

  default ContainerData getEnergyPowerData() {
    return new ContainerData() {
      @Override
      public int get(int index) {
        return switch (index) {
          case 0 -> getCurrentEnergy();
          case 1 -> getEnergyCapacity();
          default -> 0;
        };
      }

      @Override
      public void set(int index, int value) {
        if (index == 0) {
          setCurrentEnergy(value);
        }
      }

      @Override
      public int getCount() {
        return 2;
      }
    };
  }

  default int getAdaptiveTransferRate(final int baseTransferRate) {
    if (getCurrentEnergy() > baseTransferRate) {
      return baseTransferRate;
    }

    return Math.max(1, baseTransferRate / TICKS_PER_SECOND);
  }

  default boolean generateEnergy(final int amount) {
    int currentEnergy = getCurrentEnergy();
    int capacity = getEnergyCapacity();

    if (currentEnergy >= capacity) {
      return false;
    }

    int energyToAdd = Math.min(amount, capacity - currentEnergy);
    setCurrentEnergy(currentEnergy + energyToAdd);
    return true;
  }

  default int extractEnergy(final int amount, final boolean simulate) {
    int currentEnergy = getCurrentEnergy();
    int energyExtracted = Math.min(amount, currentEnergy);

    if (!simulate && energyExtracted > 0) {
      setCurrentEnergy(currentEnergy - energyExtracted);
    }

    return energyExtracted;
  }

  default void loadEnergyPowerGenerator(final CompoundTag compoundTag) {
    int energy = compoundTag.getInt(ENERGY_TAG);
    ItemStack battery = ItemStack.EMPTY;
    if (compoundTag.contains(BATTERY_TAG)) {
      battery = ItemStack.of(compoundTag.getCompound(BATTERY_TAG));
    }
    setEnergyData(new EnergyPowerData(energy, battery, EnergyDebounceData.empty()));
  }

  default void saveEnergyPowerGenerator(final CompoundTag compoundTag) {
    EnergyPowerData data = getEnergyData();
    compoundTag.putInt(ENERGY_TAG, data.currentEnergy());
    if (!data.battery().isEmpty()) {
      CompoundTag batteryTag = new CompoundTag();
      data.battery().save(batteryTag);
      compoundTag.put(BATTERY_TAG, batteryTag);
    }
  }

  default int distributeToPlatformEnergySystem(
      final Level level,
      final BlockPos blockPos,
      final int availableEnergy,
      final int maxTransferRate) {
    try {
      Class<?> distributorClass =
          Class.forName("de.markusbordihn.scraptechworkshop.energy.compat.EnergyDistributor");
      java.lang.reflect.Method method =
          distributorClass.getMethod(
              "distributeToAdjacentBlocks", Level.class, BlockPos.class, int.class, int.class);
      Object result = method.invoke(null, level, blockPos, availableEnergy, maxTransferRate);
      return (int) result;
    } catch (Exception e) {
      return 0;
    } 
  }

  default int distributeEnergy(
      final Level level,
      final BlockPos blockPos,
      final int chargingBatterySlot,
      final int maxTransferRate) {

    if (level == null || level.isClientSide) {
      return 0;
    }

    int availableEnergy = getCurrentEnergy();
    if (availableEnergy <= 0) {
      return 0;
    }

    int totalDistributed = 0;

    // First, try platform-specific energy distribution (Forge Energy, etc.)
    int platformDistributed =
        distributeToPlatformEnergySystem(level, blockPos, availableEnergy, maxTransferRate);
    if (platformDistributed > 0) {
      extractEnergy(platformDistributed, false);
      totalDistributed += platformDistributed;
      markDirty();
    }

    // Collect consumers that need energy
    List<EnergyConsumerInfo> consumers = new ArrayList<>();

    // Check adjacent blocks for EnergyPowerConsumer
    for (Direction direction : Direction.values()) {
      BlockPos adjacentPos = blockPos.relative(direction);
      BlockEntity blockEntity = level.getBlockEntity(adjacentPos);

      if (blockEntity instanceof EnergyPowerConsumer consumer
          && consumer.canAcceptExternalEnergy()) {
        int spaceAvailable = consumer.getEnergyCapacity() - consumer.getCurrentEnergy();
        if (spaceAvailable > 0) {
          consumers.add(new EnergyConsumerInfo(consumer, spaceAvailable));
        }
      }
    }

    // Check charging battery slot
    if (chargingBatterySlot >= 0 && this instanceof Container container) {
      ItemStack battery = container.getItem(chargingBatterySlot);
      int batterySpace = getBatterySpaceAvailable(battery);
      if (batterySpace > 0) {
        consumers.add(
            new EnergyConsumerInfo(container, chargingBatterySlot, battery, batterySpace));
      }
    }

    if (consumers.isEmpty()) {
      return totalDistributed;
    }

    // Calculate total space needed
    int totalSpace = consumers.stream().mapToInt(c -> c.spaceAvailable).sum();
    int energyToDistribute =
        Math.min(Math.min(availableEnergy - totalDistributed, maxTransferRate), totalSpace);

    if (energyToDistribute <= 0) {
      return totalDistributed;
    }

    // Distribute energy proportionally
    int remainingEnergy = energyToDistribute;

    for (int i = 0; i < consumers.size(); i++) {
      EnergyConsumerInfo consumer = consumers.get(i);
      int energyForThis;

      if (i == consumers.size() - 1) {
        // Last consumer gets all remaining energy
        energyForThis = Math.min(remainingEnergy, consumer.spaceAvailable);
      } else {
        // Proportional distribution
        energyForThis =
            Math.min(
                (energyToDistribute * consumer.spaceAvailable) / totalSpace,
                consumer.spaceAvailable);
        energyForThis = Math.min(energyForThis, remainingEnergy);
      }

      if (energyForThis > 0) {
        int actualTransferred = consumer.receiveEnergy(energyForThis);
        totalDistributed += actualTransferred;
        remainingEnergy -= actualTransferred;
      }
    }

    // Extract energy from generator
    if (totalDistributed > 0) {
      extractEnergy(totalDistributed, false);
      markDirty();
    }

    return totalDistributed;
  }

  default int getBatterySpaceAvailable(final ItemStack battery) {
    if (battery.isEmpty()) {
      return 0;
    }

    if (battery.getItem() instanceof EmptyEnergyCellItem) {
      return EnergyCellItem.CAPACITY_MAH;
    }

    if (battery.getItem() instanceof EnergyCellItem batteryItem) {
      int currentEnergy = batteryItem.getEnergy(battery);
      return EnergyCellItem.CAPACITY_MAH - currentEnergy;
    }

    return 0;
  }

  class EnergyConsumerInfo {
    private final EnergyPowerConsumer blockConsumer;
    private final Container batteryContainer;
    private final int batterySlot;
    private final ItemStack battery;
    private final int spaceAvailable;

    // Constructor for block consumers
    public EnergyConsumerInfo(EnergyPowerConsumer consumer, int spaceAvailable) {
      this.blockConsumer = consumer;
      this.batteryContainer = null;
      this.batterySlot = -1;
      this.battery = null;
      this.spaceAvailable = spaceAvailable;
    }

    // Constructor for battery slot
    public EnergyConsumerInfo(
        Container container, int slot, ItemStack battery, int spaceAvailable) {
      this.blockConsumer = null;
      this.batteryContainer = container;
      this.batterySlot = slot;
      this.battery = battery;
      this.spaceAvailable = spaceAvailable;
    }

    public int receiveEnergy(int amount) {
      if (blockConsumer != null) {
        return blockConsumer.receiveEnergy(amount, false);
      } else if (battery != null && batteryContainer != null) {
        return chargeBatteryItem(batteryContainer, batterySlot, battery, amount);
      }
      return 0;
    }

    private int chargeBatteryItem(Container container, int slot, ItemStack battery, int amount) {
      if (battery.getItem() instanceof EmptyEnergyCellItem) {
        // Convert empty battery to charged battery
        ItemStack chargedBattery = new ItemStack(ModItems.ENERGY_CELL.get());
        if (chargedBattery.getItem() instanceof EnergyCellItem energyCell) {
          int energyToAdd = Math.min(amount, EnergyCellItem.CAPACITY_MAH);
          energyCell.setEnergy(chargedBattery, energyToAdd);
          container.setItem(slot, chargedBattery);
          return energyToAdd;
        }
      } else if (battery.getItem() instanceof EnergyCellItem batteryItem) {
        int currentEnergy = batteryItem.getEnergy(battery);
        int energyToAdd = Math.min(amount, EnergyCellItem.CAPACITY_MAH - currentEnergy);
        batteryItem.setEnergy(battery, currentEnergy + energyToAdd);
        return energyToAdd;
      }
      return 0;
    }
  }
}
