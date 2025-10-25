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

package de.markusbordihn.scraptechworkshop.block.entity.collectorstation;

import de.markusbordihn.scraptechworkshop.block.collectorstation.CollectorStationBlock;
import de.markusbordihn.scraptechworkshop.block.entity.AbstractWorkshopBlockEntity;
import de.markusbordihn.scraptechworkshop.config.CollectorStationConfig;
import de.markusbordihn.scraptechworkshop.data.collectorstation.CollectorStationStatus;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerConsumer;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerData;
import de.markusbordihn.scraptechworkshop.loot.ScrapLootGenerator;
import de.markusbordihn.scraptechworkshop.menu.CollectorStationMenu;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CollectorStationBlockEntity extends AbstractWorkshopBlockEntity
    implements EnergyPowerConsumer {

  public static final int BATTERY_SLOT = 0;
  public static final int FIRST_STORAGE_SLOT = 1;
  public static final int LAST_STORAGE_SLOT = 24;
  public static final int STORAGE_SLOTS = 24;
  public static final int FIRST_UPGRADE_SLOT = 25;
  public static final int LAST_UPGRADE_SLOT = 28;
  public static final int TOTAL_SLOTS = 29;
  private static final Logger log = LogManager.getLogger();
  private static final int ENERGY_CONSUMPTION_INTERVAL = 20;
  private static final int ITEM_ADDITION_INTERVAL = 10;
  private static final int ENERGY_CONSUMPTION_AMOUNT = 5;
  private static final float SOUND_VOLUME = 0.5f;
  private static final float SOUND_PITCH = 1.0f;
  private static final String TRANSLATION_KEY = "container.scrap_tech_workshop.collector_station";
  private static final String STATE_TIMER_TAG = "StateTimer";
  private static final String BIOME_TAG = "Biome";
  private static final int DATA_STATUS = 0;
  private static final int DATA_STATE_TIMER = 1;
  private static final int DATA_COUNT = 2;
  private static final int ENERGY_CAPACITY_MAH = 5000;
  private static final int ENERGY_CHARGE_INTERVAL = 5;

  public static BlockEntityType<CollectorStationBlockEntity> TYPE;

  private final CollectorStationContainer container;
  private final int tickOffset;
  private int stateTimer = 0;
  private final ContainerData containerData =
      new ContainerData() {
        @Override
        public int get(int index) {
          return switch (index) {
            case DATA_STATUS -> getStatus().ordinal();
            case DATA_STATE_TIMER -> stateTimer;
            default -> 0;
          };
        }

        @Override
        public void set(int index, int value) {
          switch (index) {
            case DATA_STATUS -> setStatus(CollectorStationStatus.values()[value]);
            case DATA_STATE_TIMER -> stateTimer = value;
          }
        }

        @Override
        public int getCount() {
          return DATA_COUNT;
        }
      };
  private EnergyPowerData energyData = EnergyPowerData.empty();
  private String cachedBiome = "";
  private List<ItemStack> pendingItems = new ArrayList<>();
  private int processingIndex = 0;

  public CollectorStationBlockEntity(final BlockPos blockPos, final BlockState blockState) {
    super(TYPE, blockPos, blockState);
    this.tickOffset =
        Math.abs(
            (blockPos.getX() * 31 + blockPos.getY() * 17 + blockPos.getZ() * 13)
                % CollectorStationConfig.checkInterval);
    this.container = new CollectorStationContainer(this);
  }

  public static void tick(
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState,
      final CollectorStationBlockEntity blockEntity) {
    if (level.isClientSide) {
      blockEntity.clientTick(level, blockPos, blockState);
    } else {
      blockEntity.serverTick(level, blockPos, blockState);
    }
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return container.getItems();
  }

  @Override
  protected WorldlyContainer getContainerDelegate() {
    return container;
  }

  private void clientTick(final Level level, final BlockPos blockPos, final BlockState blockState) {
    // Client-side robot management is handled by the entity itself via its tick() method
  }

  private void serverTick(final Level level, final BlockPos blockPos, final BlockState blockState) {
    CollectorStationStatus currentStatus = getStatus();

    if ((level.getGameTime() + tickOffset) % ENERGY_CHARGE_INTERVAL == 0) {
      chargeFromBattery(getEnergyTransferRate());
    }

    if (!hasStableEnergy(CollectorStationConfig.energyPerCycle)) {
      if (currentStatus != CollectorStationStatus.NO_POWER) {
        setStatus(CollectorStationStatus.NO_POWER);
        stateTimer = 0;
      }
      return;
    }

    if ((level.getGameTime() + tickOffset) % CollectorStationConfig.checkInterval != 0) {
      return;
    }

    switch (getStatus()) {
      case NO_POWER:
        setStatus(CollectorStationStatus.CHARGING);
        stateTimer = 0;
        log.debug("[CollectorStation@{}] Status changed: NO_POWER -> CHARGING", blockPos);
        playSound(level, blockPos, SoundEvents.BEACON_POWER_SELECT, 0.3f, 1.2f);
        break;

      case CHARGING:
        int chargeMultiplier = getChargeMultiplierBonus();
        stateTimer += CollectorStationConfig.checkInterval * chargeMultiplier;
        if (stateTimer == CollectorStationConfig.checkInterval * chargeMultiplier) {
          playSound(level, blockPos, SoundEvents.BEACON_AMBIENT, 0.5f, 1.5f);
        }

        if (stateTimer % ENERGY_CONSUMPTION_INTERVAL == 0) {
          consumeEnergy(ENERGY_CONSUMPTION_AMOUNT);
        }

        if (stateTimer >= CollectorStationConfig.chargingTime) {
          if (hasSpaceInStorage()) {
            setStatus(CollectorStationStatus.COLLECTING);
            stateTimer = 0;
            cachedBiome = level.getBiome(blockPos).unwrapKey().get().location().toString();
            log.debug(
                "[CollectorStation@{}] Status changed: CHARGING -> COLLECTING (Biome: {})",
                blockPos,
                cachedBiome);
            playSound(level, blockPos, SoundEvents.PISTON_EXTEND, SOUND_VOLUME, 0.8f);
          } else {
            setStatus(CollectorStationStatus.NO_STORAGE);
            stateTimer = 0;
            log.debug(
                "[CollectorStation@{}] Status changed: CHARGING -> NO_STORAGE (storage full)",
                blockPos);
            playSound(level, blockPos, SoundEvents.IRON_DOOR_CLOSE, SOUND_VOLUME, 0.7f);
          }
        }
        break;

      case NO_STORAGE:
        if (hasSpaceInStorage()) {
          setStatus(CollectorStationStatus.COLLECTING);
          stateTimer = 0;
          cachedBiome = level.getBiome(blockPos).unwrapKey().get().location().toString();
          log.debug(
              "[CollectorStation@{}] Status changed: NO_STORAGE -> COLLECTING (Biome: {})",
              blockPos,
              cachedBiome);
          playSound(level, blockPos, SoundEvents.PISTON_EXTEND, SOUND_VOLUME, 0.8f);
        }
        break;

      case COLLECTING:
        int speedMultiplier = getSpeedMultiplierBonus();
        stateTimer += CollectorStationConfig.checkInterval * speedMultiplier;
        if (stateTimer >= CollectorStationConfig.collectingTime) {
          setStatus(CollectorStationStatus.RETURNING);
          stateTimer = 0;
          log.debug(
              "[CollectorStation@{}] Status changed: COLLECTING -> RETURNING (collected for {} ticks)",
              blockPos,
              CollectorStationConfig.collectingTime);
          playSound(level, blockPos, SoundEvents.PISTON_CONTRACT, SOUND_VOLUME, 0.9f);
        }
        break;

      case RETURNING:
        stateTimer += CollectorStationConfig.checkInterval;
        if (stateTimer >= CollectorStationConfig.returningTime) {
          setStatus(CollectorStationStatus.PROCESSING);
          stateTimer = 0;
          processingIndex = 0;
          log.debug("[CollectorStation@{}] Status changed: RETURNING -> PROCESSING", blockPos);
          playSound(level, blockPos, SoundEvents.ANVIL_LAND, 0.3f, 1.5f);
          pendingItems = ScrapLootGenerator.generateScrapForBiome(level, blockPos, cachedBiome);
          log.debug(
              "[CollectorStation@{}] Generated {} scrap items to process",
              blockPos,
              pendingItems.size());
        }
        break;

      case PROCESSING:
        stateTimer += CollectorStationConfig.checkInterval;
        if (stateTimer % ITEM_ADDITION_INTERVAL == 0 && processingIndex < pendingItems.size()) {
          ItemStack scrap = pendingItems.get(processingIndex);
          if (!scrap.isEmpty()) {
            addSingleScrapItem(scrap, blockPos);
            playSound(level, blockPos, SoundEvents.ITEM_PICKUP, 0.3f, 1.2f);
          }
          processingIndex++;
        }

        if (stateTimer >= CollectorStationConfig.processingTime
            && processingIndex >= pendingItems.size()) {
          setStatus(CollectorStationStatus.CHARGING);
          stateTimer = 0;
          pendingItems.clear();
          processingIndex = 0;
          log.debug(
              "[CollectorStation@{}] Status changed: PROCESSING -> CHARGING (cycle complete)",
              blockPos);
          playSound(level, blockPos, SoundEvents.BEACON_DEACTIVATE, 0.4f, SOUND_PITCH);
        }
        break;
    }

    setChanged();
  }

  private boolean hasSpaceInStorage() {
    for (int i = FIRST_STORAGE_SLOT; i <= LAST_STORAGE_SLOT; i++) {
      ItemStack stack = container.getItem(i);
      if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
        return true;
      }
    }
    return false;
  }

  private int getSpeedMultiplierBonus() {
    int totalMultiplier = 1;
    for (int i = FIRST_UPGRADE_SLOT; i <= LAST_UPGRADE_SLOT; i++) {
      ItemStack stack = container.getItem(i);
      if (!stack.isEmpty()
          && stack.getItem()
              instanceof
              de.markusbordihn.scraptechworkshop.item.upgrade.SpeedUpgradeItem speedUpgrade) {
        totalMultiplier += speedUpgrade.getSpeedMultiplier() - 1;
      }
    }
    return totalMultiplier;
  }

  private int getChargeMultiplierBonus() {
    int totalMultiplier = 1;
    for (int i = FIRST_UPGRADE_SLOT; i <= LAST_UPGRADE_SLOT; i++) {
      ItemStack stack = container.getItem(i);
      if (!stack.isEmpty()
          && stack.getItem()
              instanceof
              de.markusbordihn.scraptechworkshop.item.upgrade.ChargeUpgradeItem chargeUpgrade) {
        totalMultiplier += chargeUpgrade.getChargeMultiplier() - 1;
      }
    }
    return totalMultiplier;
  }

  private void addSingleScrapItem(final ItemStack scrap, final BlockPos blockPos) {
    if (scrap.isEmpty()) {
      return;
    }

    for (int i = FIRST_STORAGE_SLOT; i <= LAST_STORAGE_SLOT; i++) {
      ItemStack slotStack = container.getItem(i);
      if (slotStack.isEmpty()) {
        container.setItem(i, scrap.copy());
        log.debug(
            "[CollectorStation@{}] Added {} x{} to slot {}",
            blockPos,
            scrap.getItem(),
            scrap.getCount(),
            i);
        return;
      } else if (ItemStack.isSameItemSameTags(slotStack, scrap)
          && slotStack.getCount() + scrap.getCount() <= slotStack.getMaxStackSize()) {
        slotStack.grow(scrap.getCount());
        log.debug(
            "[CollectorStation@{}] Stacked {} x{} into slot {} (total: {})",
            blockPos,
            scrap.getItem(),
            scrap.getCount(),
            i,
            slotStack.getCount());
        return;
      }
    }

    log.warn(
        "[CollectorStation@{}] No space for {} x{}", blockPos, scrap.getItem(), scrap.getCount());
  }

  private void playSound(
      final Level level,
      final BlockPos blockPos,
      final SoundEvent sound,
      final float volume,
      final float pitch) {
    if (level != null && !level.isClientSide) {
      level.playSound(null, blockPos, sound, SoundSource.BLOCKS, volume, pitch);
    }
  }

  public ItemStack getBattery() {
    return container.getItem(BATTERY_SLOT);
  }

  @Override
  public EnergyPowerData getEnergyData() {
    return new EnergyPowerData(
        energyData.currentEnergy(), container.getItem(BATTERY_SLOT), energyData.debounceData());
  }

  @Override
  public void setEnergyData(EnergyPowerData data) {
    this.energyData =
        new EnergyPowerData(data.currentEnergy(), data.battery(), data.debounceData());
    container.setItem(BATTERY_SLOT, data.battery());
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

  public CollectorStationContainer getContainer() {
    return container;
  }

  public ContainerData getContainerData() {
    return containerData;
  }

  public CollectorStationStatus getStatus() {
    Level currentLevel = getLevel();
    if (currentLevel != null && getBlockState().hasProperty(CollectorStationBlock.STATE)) {
      return getBlockState().getValue(CollectorStationBlock.STATE);
    }
    return CollectorStationStatus.NO_POWER;
  }

  private void setStatus(CollectorStationStatus newStatus) {
    Level currentLevel = getLevel();
    if (currentLevel != null && getBlockState().hasProperty(CollectorStationBlock.STATE)) {
      BlockState currentState = getBlockState();
      if (currentState.getValue(CollectorStationBlock.STATE) != newStatus) {
        currentLevel.setBlock(
            worldPosition, currentState.setValue(CollectorStationBlock.STATE, newStatus), 3);
      }
    }
  }

  public int getStateTimer() {
    return stateTimer;
  }

  @Override
  public void load(CompoundTag compoundTag) {
    super.load(compoundTag);
    stateTimer = compoundTag.getInt(STATE_TIMER_TAG);
    loadEnergyPowerConsumer(compoundTag);
    cachedBiome = compoundTag.getString(BIOME_TAG);
    // Sync battery from container after loading
    energyData =
        new EnergyPowerData(
            energyData.currentEnergy(), container.getItem(BATTERY_SLOT), energyData.debounceData());
  }

  @Override
  protected void saveAdditional(CompoundTag compoundTag) {
    super.saveAdditional(compoundTag);
    compoundTag.putInt(STATE_TIMER_TAG, stateTimer);
    saveEnergyPowerConsumer(compoundTag);
    compoundTag.putString(BIOME_TAG, cachedBiome);
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable(TRANSLATION_KEY);
  }

  @Override
  public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
    return new CollectorStationMenu(windowId, playerInventory, this, containerData);
  }
}
