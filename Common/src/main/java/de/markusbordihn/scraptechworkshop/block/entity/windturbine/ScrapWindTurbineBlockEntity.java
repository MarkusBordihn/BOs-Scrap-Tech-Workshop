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

package de.markusbordihn.scraptechworkshop.block.entity.windturbine;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.entity.AbstractWorkshopBlockEntity;
import de.markusbordihn.scraptechworkshop.block.windturbine.ScrapWindTurbineBlock;
import de.markusbordihn.scraptechworkshop.data.windturbine.ScrapWindTurbineStatus;
import de.markusbordihn.scraptechworkshop.energy.EnergyFlowStatus;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerBatteryHandler;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerData;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerGenerator;
import de.markusbordihn.scraptechworkshop.environment.WindCalculator;
import de.markusbordihn.scraptechworkshop.menu.ScrapWindTurbineMenu;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScrapWindTurbineBlockEntity extends AbstractWorkshopBlockEntity
    implements EnergyPowerGenerator, EnergyPowerBatteryHandler {

  public static final String ID = "scrap_wind_turbine";
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final int ENERGY_CAPACITY_MAH = 5000;
  private static final int ENERGY_GENERATION_INTERVAL = 20;
  private static final int ENERGY_DISTRIBUTION_INTERVAL = 1;
  private static final int WIND_CHECK_INTERVAL = 200;

  private static final String WIND_SPEED_TAG = "WindSpeed";
  private static final String POWER_GENERATION_TAG = "PowerGeneration";

  private static final int WIND_SPEED_DATA_INDEX = 0;
  private static final int POWER_GENERATION_DATA_INDEX = 1;

  private static final String TRANSLATION_KEY = "container.scrap_tech_workshop.scrap_wind_turbine";
  public static BlockEntityType<ScrapWindTurbineBlockEntity> TYPE;

  private final ScrapWindTurbineContainer container;
  private final int windCheckOffset;
  private int windSpeed = 0;
  private int powerGeneration = 0;
  private final ContainerData containerData =
      new ContainerData() {
        @Override
        public int get(int index) {
          return switch (index) {
            case WIND_SPEED_DATA_INDEX -> windSpeed;
            case POWER_GENERATION_DATA_INDEX -> powerGeneration;
            default -> 0;
          };
        }

        @Override
        public void set(int index, int value) {
          switch (index) {
            case WIND_SPEED_DATA_INDEX -> windSpeed = value;
            case POWER_GENERATION_DATA_INDEX -> powerGeneration = value;
          }
        }

        @Override
        public int getCount() {
          return 2;
        }
      };
  private EnergyPowerData energyData = EnergyPowerData.empty();
  private int tickCounter = 0;

  public ScrapWindTurbineBlockEntity(final BlockPos blockPos, final BlockState blockState) {
    super(TYPE, blockPos, blockState);
    this.container =
        new ScrapWindTurbineContainer(ScrapWindTurbineSlots.TOTAL_SLOTS, this::setChanged);
    this.windCheckOffset =
        Math.abs(
            (blockPos.getX() * 31 + blockPos.getY() * 17 + blockPos.getZ() * 13)
                % WIND_CHECK_INTERVAL);
    this.energyData = EnergyPowerData.empty();
  }

  public static void tick(
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState,
      final ScrapWindTurbineBlockEntity blockEntity) {
    if (level.isClientSide) {
      return;
    }

    blockEntity.tickCounter++;

    if ((blockEntity.tickCounter + blockEntity.windCheckOffset) % WIND_CHECK_INTERVAL == 0) {
      blockEntity.windSpeed = WindCalculator.calculateWindSpeed(level, blockPos.above());
      blockEntity.powerGeneration = WindCalculator.calculatePowerGeneration(blockEntity.windSpeed);
    }

    if (blockEntity.powerGeneration > 0
        && blockEntity.tickCounter % ENERGY_GENERATION_INTERVAL == 0) {
      blockEntity.generateEnergy(blockEntity.powerGeneration);
      blockEntity.updateEnergyFlow(level.getGameTime());
    }

    if (blockEntity.tickCounter % ENERGY_DISTRIBUTION_INTERVAL == 0) {
      int adaptiveRate = blockEntity.getAdaptiveTransferRate(blockEntity.getEnergyTransferRate());
      blockEntity.distributeEnergy(
          level, blockPos, ScrapWindTurbineSlots.CHARGING_BATTERY_SLOT, adaptiveRate);
    }

    ScrapWindTurbineStatus newStatus = blockEntity.determineStatus();

    if (newStatus != blockState.getValue(ScrapWindTurbineBlock.STATUS)) {
      ScrapWindTurbineBlock.updateStatus(level, blockPos, newStatus);
    }

    if (blockEntity.tickCounter % 20 == 0) {
      blockEntity.setChanged();
    }
  }

  private ScrapWindTurbineStatus determineStatus() {
    if (level != null && !level.canSeeSky(getBlockPos().above())) {
      return ScrapWindTurbineStatus.ERROR;
    }
    if (windSpeed == 0) {
      return ScrapWindTurbineStatus.NO_WIND;
    }
    if (windSpeed < 30) {
      return ScrapWindTurbineStatus.LOW_WIND;
    }
    if (powerGeneration > 0) {
      return ScrapWindTurbineStatus.GENERATING;
    }
    return ScrapWindTurbineStatus.IDLE;
  }

  public int getWindSpeed() {
    return windSpeed;
  }

  public ScrapWindTurbineContainer getContainer() {
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
    windSpeed = compoundTag.getInt(WIND_SPEED_TAG);
    powerGeneration = compoundTag.getInt(POWER_GENERATION_TAG);
    loadEnergyPowerGenerator(compoundTag);
  }

  @Override
  protected void saveAdditional(CompoundTag compoundTag) {
    super.saveAdditional(compoundTag);
    compoundTag.putInt(WIND_SPEED_TAG, windSpeed);
    compoundTag.putInt(POWER_GENERATION_TAG, powerGeneration);
    saveEnergyPowerGenerator(compoundTag);
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable(TRANSLATION_KEY);
  }

  @Override
  public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
    return new ScrapWindTurbineMenu(windowId, playerInventory, this, containerData);
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
    return energyData.withBattery(
        container.getItem(ScrapWindTurbineSlots.ENERGY_TAB_BATTERY_SLOT));
  }

  @Override
  public void setEnergyData(EnergyPowerData data) {
    energyData = data;
    container.setItem(ScrapWindTurbineSlots.ENERGY_TAB_BATTERY_SLOT, data.battery());
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

  @Override
  public int getChargeCycleCount() {
    return getEnergyData().chargeCycleCount();
  }

  @Override
  public void setChargeCycleCount(final int count) {
    setEnergyData(getEnergyData().withChargeCycleCount(count));
  }

  @Override
  public long getLastEnergyChangeTime() {
    return getEnergyData().lastEnergyChangeTime();
  }

  @Override
  public void setLastEnergyChangeTime(final long time) {
    setEnergyData(getEnergyData().withLastEnergyChangeTime(time));
  }

  @Override
  public int getLastEnergyLevel() {
    return getEnergyData().lastEnergyLevel();
  }

  @Override
  public void setLastEnergyLevel(final int level) {
    setEnergyData(getEnergyData().withLastEnergyLevel(level));
  }

  @Override
  public EnergyFlowStatus getEnergyFlowStatus() {
    return getEnergyData().energyFlowStatus();
  }

  @Override
  public void setEnergyFlowStatus(final EnergyFlowStatus status) {
    setEnergyData(getEnergyData().withEnergyFlowStatus(status));
  }
}
