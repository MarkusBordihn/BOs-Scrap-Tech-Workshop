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
import de.markusbordihn.scraptechworkshop.item.component.EmptyEnergyCellBlockItem;
import de.markusbordihn.scraptechworkshop.item.component.EmptyEnergyCellItem;
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
          case 2 -> {
            // If this implements EnergyPowerBatteryHandler, return flow status
            if (EnergyPowerGenerator.this instanceof EnergyPowerBatteryHandler handler) {
              yield handler.getEnergyFlowStatus().ordinal();
            }
            yield 0;
          }
          default -> 0;
        };
      }

      @Override
      public void set(int index, int value) {
        switch (index) {
          case 0 -> setCurrentEnergy(value);
          case 2 -> {
            if (EnergyPowerGenerator.this instanceof EnergyPowerBatteryHandler handler) {
              EnergyFlowStatus[] statuses = EnergyFlowStatus.values();
              if (value >= 0 && value < statuses.length) {
                handler.setEnergyFlowStatus(statuses[value]);
              }
            }
          }
        }
      }

      @Override
      public int getCount() {
        return 3;
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
    int energyToAdd = Math.min(amount, capacity - currentEnergy);
    
    if (energyToAdd > 0) {
      setCurrentEnergy(currentEnergy + energyToAdd);
      return true;
    }
    
    return false;
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
    
    EnergyPowerData data = EnergyPowerData.withEnergy(energy).withBattery(battery);
    
    if (this instanceof EnergyPowerBatteryHandler) {
      if (compoundTag.contains("ChargeCycleCount")) {
        data = data.withChargeCycleCount(compoundTag.getInt("ChargeCycleCount"));
      }
      if (compoundTag.contains("LastEnergyChangeTime")) {
        data = data.withLastEnergyChangeTime(compoundTag.getLong("LastEnergyChangeTime"));
      }
      if (compoundTag.contains("LastEnergyLevel")) {
        data = data.withLastEnergyLevel(compoundTag.getInt("LastEnergyLevel"));
      }
      if (compoundTag.contains("EnergyFlowStatus")) {
        int statusOrdinal = compoundTag.getInt("EnergyFlowStatus");
        EnergyFlowStatus[] statuses = EnergyFlowStatus.values();
        if (statusOrdinal >= 0 && statusOrdinal < statuses.length) {
          data = data.withEnergyFlowStatus(statuses[statusOrdinal]);
        }
      }
    }
    
    setEnergyData(data.withDebounceData(EnergyDebounceData.empty()));
  }

  default void saveEnergyPowerGenerator(final CompoundTag compoundTag) {
    EnergyPowerData data = getEnergyData();
    compoundTag.putInt(ENERGY_TAG, data.currentEnergy());
    if (!data.battery().isEmpty()) {
      CompoundTag batteryTag = new CompoundTag();
      data.battery().save(batteryTag);
      compoundTag.put(BATTERY_TAG, batteryTag);
    }
    
    if (this instanceof EnergyPowerBatteryHandler) {
      compoundTag.putInt("ChargeCycleCount", data.chargeCycleCount());
      compoundTag.putLong("LastEnergyChangeTime", data.lastEnergyChangeTime());
      compoundTag.putInt("LastEnergyLevel", data.lastEnergyLevel());
      compoundTag.putInt("EnergyFlowStatus", data.energyFlowStatus().ordinal());
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

    int platformDistributed =
        distributeToPlatformEnergySystem(level, blockPos, availableEnergy, maxTransferRate);
    if (platformDistributed > 0) {
      extractEnergy(platformDistributed, false);
      totalDistributed += platformDistributed;
      availableEnergy -= platformDistributed;
      markDirty();
    }

    List<EnergyConsumerInfo> consumers = new ArrayList<>();

    for (Direction direction : Direction.values()) {
      BlockPos adjacentPos = blockPos.relative(direction);
      BlockEntity blockEntity = level.getBlockEntity(adjacentPos);

      if (blockEntity instanceof EnergyPowerConsumer consumer
          && consumer.canAcceptExternalEnergy()) {
        int spaceAvailable = consumer.getTotalSpaceAvailable();
        if (spaceAvailable > 0) {
          consumers.add(new EnergyConsumerInfo(consumer, spaceAvailable));
        }
      }
    }

    if (chargingBatterySlot >= 0 && this instanceof Container container) {
      ItemStack battery = container.getItem(chargingBatterySlot);
      int batterySpace = getBatterySpaceAvailable(battery);
      if (batterySpace > 0) {
        consumers.add(new EnergyConsumerInfo(container, chargingBatterySlot, battery, batterySpace));
      }
    }

    if (!consumers.isEmpty()) {
      int totalSpace = consumers.stream().mapToInt(c -> c.spaceAvailable).sum();
      int energyToDistribute = Math.min(Math.min(availableEnergy, maxTransferRate), totalSpace);

      if (energyToDistribute > 0) {
        int remainingEnergy = energyToDistribute;

        for (int i = 0; i < consumers.size(); i++) {
          EnergyConsumerInfo consumer = consumers.get(i);
          int energyForThis;

          if (i == consumers.size() - 1) {
            energyForThis = Math.min(remainingEnergy, consumer.spaceAvailable);
          } else {
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

        if (totalDistributed > platformDistributed) {
          extractEnergy(totalDistributed - platformDistributed, false);
          markDirty();
        }
      }
    }

    if (this instanceof EnergyPowerBatteryHandler batteryHandler) {
      chargeEnergyTabBattery(batteryHandler);
    }

    return totalDistributed;
  }

  default void chargeEnergyTabBattery(EnergyPowerBatteryHandler handler) {
    ItemStack battery = handler.getBattery();
    if (battery.isEmpty()) {
      return;
    }

    int currentEnergy = getCurrentEnergy();
    int capacity = getEnergyCapacity();

    // Handle empty energy cell block - convert to charged block
    if (battery.getItem() instanceof EmptyEnergyCellBlockItem) {
      // Only charge if we're at or above capacity (generator is full)
      if (currentEnergy >= capacity) {
        ItemStack chargedBattery = new ItemStack(ModItems.ENERGY_CELL_BLOCK.get());
        if (chargedBattery.getItem() instanceof EnergyCell energyCell) {
          int energyToAdd = Math.min(currentEnergy - capacity + 1, energyCell.getCapacity());
          energyCell.setEnergy(chargedBattery, energyToAdd);
          handler.setBattery(chargedBattery);
          setCurrentEnergy(currentEnergy - energyToAdd);
          markDirty();
        }
      }
      return;
    }

    // Handle empty energy cell - convert to charged cell
    if (battery.getItem() instanceof EmptyEnergyCellItem) {
      // Only charge if we're at or above capacity (generator is full)
      if (currentEnergy >= capacity) {
        ItemStack chargedBattery = new ItemStack(ModItems.ENERGY_CELL.get());
        if (chargedBattery.getItem() instanceof EnergyCell energyCell) {
          int energyToAdd = Math.min(currentEnergy - capacity + 1, energyCell.getCapacity());
          energyCell.setEnergy(chargedBattery, energyToAdd);
          handler.setBattery(chargedBattery);
          setCurrentEnergy(currentEnergy - energyToAdd);
          markDirty();
        }
      }
      return;
    }
    
    // Handle charging existing battery - only if above target (95%)
    if (battery.getItem() instanceof EnergyCell cell) {
      int batteryEnergy = cell.getEnergy(battery);
      if (batteryEnergy < cell.getCapacity()) {
        ChargingMode mode = handler.getChargingMode();
        int targetEnergy = (capacity * mode.getTargetPercentage()) / 100;
        
        // Only charge battery if we're above target level (95%)
        if (currentEnergy > targetEnergy) {
          int availableForCharging = currentEnergy - targetEnergy;
          int transferred = cell.addEnergy(battery, availableForCharging);
          if (transferred > 0) {
            setCurrentEnergy(currentEnergy - transferred);
            handler.setBattery(battery);
            markDirty();
          }
        }
      }
    }
  }

  default int getBatterySpaceAvailable(final ItemStack battery) {
    if (battery.isEmpty()) {
      return 0;
    }

    if (battery.getItem() instanceof EmptyEnergyCellBlockItem) {
      // For empty cell blocks, return capacity of standard energy cell block
      ItemStack tempCell = new ItemStack(ModItems.ENERGY_CELL_BLOCK.get());
      if (tempCell.getItem() instanceof EnergyCell cell) {
        return cell.getCapacity();
      }
      return 30000; // Fallback to block capacity
    }

    if (battery.getItem() instanceof EmptyEnergyCellItem) {
      // For empty cells, return capacity of standard energy cell
      ItemStack tempCell = new ItemStack(ModItems.ENERGY_CELL.get());
      if (tempCell.getItem() instanceof EnergyCell cell) {
        return cell.getCapacity();
      }
      return 5000; // Fallback to standard capacity
    }

    if (battery.getItem() instanceof EnergyCell cell) {
      int currentEnergy = cell.getEnergy(battery);
      return cell.getCapacity() - currentEnergy;
    }

    return 0;
  }

  class EnergyConsumerInfo {
    private final EnergyPowerConsumer blockConsumer;
    private final Container batteryContainer;
    private final int batterySlot;
    private final ItemStack battery;
    private final int spaceAvailable;

    public EnergyConsumerInfo(EnergyPowerConsumer consumer, int spaceAvailable) {
      this.blockConsumer = consumer;
      this.batteryContainer = null;
      this.batterySlot = -1;
      this.battery = null;
      this.spaceAvailable = spaceAvailable;
    }

    public EnergyConsumerInfo(Container container, int slot, ItemStack battery, int spaceAvailable) {
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
      // Handle empty energy cell block
      if (battery.getItem() instanceof EmptyEnergyCellBlockItem) {
        ItemStack chargedBattery = new ItemStack(ModItems.ENERGY_CELL_BLOCK.get());
        if (chargedBattery.getItem() instanceof EnergyCell energyCell) {
          int energyToAdd = Math.min(amount, energyCell.getCapacity());
          energyCell.setEnergy(chargedBattery, energyToAdd);
          container.setItem(slot, chargedBattery);
          return energyToAdd;
        }
      }
      // Handle empty energy cell
      else if (battery.getItem() instanceof EmptyEnergyCellItem) {
        ItemStack chargedBattery = new ItemStack(ModItems.ENERGY_CELL.get());
        if (chargedBattery.getItem() instanceof EnergyCell energyCell) {
          int energyToAdd = Math.min(amount, energyCell.getCapacity());
          energyCell.setEnergy(chargedBattery, energyToAdd);
          container.setItem(slot, chargedBattery);
          return energyToAdd;
        }
      } 
      // Handle charging existing battery
      else if (battery.getItem() instanceof EnergyCell cell) {
        int currentEnergy = cell.getEnergy(battery);
        int energyToAdd = Math.min(amount, cell.getCapacity() - currentEnergy);
        cell.setEnergy(battery, currentEnergy + energyToAdd);
        return energyToAdd;
      }
      return 0;
    }
  }
}
