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

import de.markusbordihn.scraptechworkshop.block.CollectorStationBlock;
import de.markusbordihn.scraptechworkshop.config.CollectorStationConfig;
import de.markusbordihn.scraptechworkshop.data.collectorstation.CollectorStationStatus;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import de.markusbordihn.scraptechworkshop.loot.ScrapLootGenerator;
import de.markusbordihn.scraptechworkshop.menu.CollectorStationMenu;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public class CollectorStationBlockEntity extends BlockEntity
    implements MenuProvider, WorldlyContainer {

  public static final int BATTERY_SLOT = 0;
  public static final int FIRST_STORAGE_SLOT = 1;
  public static final int LAST_STORAGE_SLOT = 24;
  public static final int STORAGE_SLOTS = 24;
  public static final int FIRST_UPGRADE_SLOT = 25;
  public static final int LAST_UPGRADE_SLOT = 28;
  public static final int UPGRADE_SLOTS = 4;
  public static final int TOTAL_SLOTS = 29;
  private static final Logger log = LogManager.getLogger();
  private static final int ENERGY_CONSUMPTION_INTERVAL = 20;
  private static final int ITEM_ADDITION_INTERVAL = 10;
  private static final int ENERGY_CONSUMPTION_AMOUNT = 10;
  private static final int MAX_ENERGY = 5000;

  private static final float SOUND_VOLUME = 0.5f;
  private static final float SOUND_PITCH = 1.0f;

  private static final String TRANSLATION_KEY = "container.scrap_tech_workshop.collector_station";
  private static final String ITEM_TAG_PREFIX = "Item";
  private static final String STATUS_TAG = "Status";
  private static final String STATE_TIMER_TAG = "StateTimer";
  private static final String ENERGY_TAG = "Energy";
  private static final String BIOME_TAG = "Biome";
  private static final String PENDING_ITEMS_TAG = "PendingItems";
  private static final String PROCESSING_INDEX_TAG = "ProcessingIndex";
  private static final int DATA_STATUS = 0;
  private static final int DATA_STATE_TIMER = 1;
  private static final int DATA_ENERGY = 2;
  private static final int DATA_POWERED = 3;
  private static final int DATA_COUNT = 4;
  public static BlockEntityType<CollectorStationBlockEntity> TYPE;
  private final CollectorStationContainer container;
  private CollectorStationStatus status = CollectorStationStatus.CHARGING;
  private int stateTimer = 0;
  private int currentEnergy = 0;
  private final ContainerData containerData =
      new ContainerData() {
        @Override
        public int get(int index) {
          return switch (index) {
            case DATA_STATUS -> status.ordinal();
            case DATA_STATE_TIMER -> stateTimer;
            case DATA_ENERGY -> currentEnergy;
            case DATA_POWERED -> status.isPowered() ? 1 : 0;
            default -> 0;
          };
        }

        @Override
        public void set(int index, int value) {
          switch (index) {
            case DATA_STATUS -> status = CollectorStationStatus.values()[value];
            case DATA_STATE_TIMER -> stateTimer = value;
            case DATA_ENERGY -> currentEnergy = value;
            case DATA_POWERED -> {} // Powered is derived from status
          }
        }

        @Override
        public int getCount() {
          return DATA_COUNT;
        }
      };
  private String cachedBiome = "";
  private List<ItemStack> pendingItems = new ArrayList<>();
  private int processingIndex = 0;

  public CollectorStationBlockEntity(final BlockPos blockPos, final BlockState blockState) {
    super(TYPE, blockPos, blockState);
    this.container = new CollectorStationContainer(this);
  }

  public static void tick(
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState,
      final CollectorStationBlockEntity blockEntity) {
    if (level.isClientSide) {
      return;
    }

    blockEntity.serverTick(level, blockPos, blockState);
  }

  private void serverTick(final Level level, final BlockPos blockPos, final BlockState blockState) {
    ItemStack battery = getBattery();
    if (battery.isEmpty() || !(battery.getItem() instanceof EnergyCellItem batteryItem)) {
      if (status != CollectorStationStatus.NO_POWER) {
        status = CollectorStationStatus.NO_POWER;
        stateTimer = 0;
        updateBlockState(blockState, false);
      }
      return;
    }

    currentEnergy = batteryItem.getEnergy(battery);
    if (currentEnergy < CollectorStationConfig.energyPerCycle) {
      if (status != CollectorStationStatus.NO_POWER) {
        status = CollectorStationStatus.NO_POWER;
        stateTimer = 0;
        updateBlockState(blockState, false);
      }
      return;
    }

    if (level.getGameTime() % CollectorStationConfig.checkInterval != 0) {
      return;
    }

    switch (status) {
      case NO_POWER:
        status = CollectorStationStatus.CHARGING;
        stateTimer = 0;
        log.debug("[CollectorStation@{}] Status changed: NO_POWER -> CHARGING", blockPos);
        playSound(level, blockPos, SoundEvents.BEACON_POWER_SELECT, 0.3f, 1.2f);
        updateBlockState(blockState, false);
        break;

      case CHARGING:
        stateTimer += CollectorStationConfig.checkInterval;
        if (stateTimer == CollectorStationConfig.checkInterval) {
          playSound(level, blockPos, SoundEvents.BEACON_AMBIENT, 0.5f, 1.5f);
        }

        if (stateTimer % ENERGY_CONSUMPTION_INTERVAL == 0 && currentEnergy > 0) {
          int energyToConsume = Math.min(ENERGY_CONSUMPTION_AMOUNT, currentEnergy);
          batteryItem.consumeEnergy(battery, energyToConsume);
          currentEnergy = batteryItem.getEnergy(battery);
        }

        if (stateTimer >= CollectorStationConfig.chargingTime) {
          status = CollectorStationStatus.READY;
          stateTimer = 0;
          log.debug(
              "[CollectorStation@{}] Status changed: CHARGING -> READY (robot fully charged)",
              blockPos);
          playSound(level, blockPos, SoundEvents.EXPERIENCE_ORB_PICKUP, SOUND_VOLUME, SOUND_PITCH);
          updateBlockState(blockState, false);
        }
        break;

      case READY:
        if (hasSpaceInStorage()) {
          status = CollectorStationStatus.COLLECTING;
          stateTimer = 0;
          cachedBiome = level.getBiome(blockPos).unwrapKey().get().location().toString();
          log.debug(
              "[CollectorStation@{}] Status changed: READY -> COLLECTING (Biome: {})",
              blockPos,
              cachedBiome);
          playSound(level, blockPos, SoundEvents.PISTON_EXTEND, SOUND_VOLUME, 0.8f);
          updateBlockState(blockState, true);
        }
        break;

      case COLLECTING:
        int speedMultiplier = getSpeedMultiplierBonus();
        stateTimer += CollectorStationConfig.checkInterval * speedMultiplier;
        if (stateTimer >= CollectorStationConfig.collectingTime) {
          status = CollectorStationStatus.RETURNING;
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
          status = CollectorStationStatus.PROCESSING;
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
          status = CollectorStationStatus.CHARGING;
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
    int totalMultiplier = 1; // Base speed
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

  private void addSingleScrapItem(final ItemStack scrap, final BlockPos blockPos) {
    if (scrap.isEmpty()) {
      return;
    }

    log.debug(
        "[CollectorStation@{}] Processing scrap: {} x{}",
        blockPos,
        scrap.getItem(),
        scrap.getCount());

    boolean added = false;
    for (int i = FIRST_STORAGE_SLOT; i <= LAST_STORAGE_SLOT; i++) {
      ItemStack slotStack = container.getItem(i);
      if (slotStack.isEmpty()) {
        container.setItem(i, scrap.copy());
        log.debug(
            "[CollectorStation@{}] Added {} x{} to empty slot {}",
            blockPos,
            scrap.getItem(),
            scrap.getCount(),
            i);
        added = true;
        break;
      } else if (ItemStack.isSameItemSameTags(slotStack, scrap)
          && slotStack.getCount() + scrap.getCount() <= slotStack.getMaxStackSize()) {
        slotStack.grow(scrap.getCount());
        log.debug(
            "[CollectorStation@{}] Stacked {} x{} into slot {} (now {})",
            blockPos,
            scrap.getItem(),
            scrap.getCount(),
            i,
            slotStack.getCount());
        added = true;
        break;
      }
    }

    if (!added) {
      log.warn(
          "[CollectorStation@{}] Could not add scrap {} x{} - no space available!",
          blockPos,
          scrap.getItem(),
          scrap.getCount());
    }
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

  private void updateBlockState(final BlockState blockState, final boolean active) {
    CollectorStationStatus newState = status;
    if (blockState.getValue(CollectorStationBlock.STATE) != newState) {
      level.setBlock(worldPosition, blockState.setValue(CollectorStationBlock.STATE, newState), 3);
    }
  }

  public ItemStack getBattery() {
    return container.getItem(BATTERY_SLOT);
  }

  public void setBattery(ItemStack battery) {
    container.setItem(BATTERY_SLOT, battery);
  }

  public CollectorStationContainer getContainer() {
    return container;
  }

  public ContainerData getContainerData() {
    return containerData;
  }

  public CollectorStationStatus getStatus() {
    return status;
  }

  @Override
  public void load(CompoundTag compoundTag) {
    super.load(compoundTag);

    for (int i = 0; i < TOTAL_SLOTS; i++) {
      if (compoundTag.contains(ITEM_TAG_PREFIX + i)) {
        container.getItems().set(i, ItemStack.of(compoundTag.getCompound(ITEM_TAG_PREFIX + i)));
      } else {
        container.getItems().set(i, ItemStack.EMPTY);
      }
    }

    status = CollectorStationStatus.values()[compoundTag.getInt(STATUS_TAG)];
    stateTimer = compoundTag.getInt(STATE_TIMER_TAG);
    currentEnergy = compoundTag.getInt(ENERGY_TAG);
    cachedBiome = compoundTag.getString(BIOME_TAG);
  }

  @Override
  protected void saveAdditional(CompoundTag compoundTag) {
    super.saveAdditional(compoundTag);

    for (int i = 0; i < TOTAL_SLOTS; i++) {
      if (!container.getItems().get(i).isEmpty()) {
        compoundTag.put(ITEM_TAG_PREFIX + i, container.getItems().get(i).save(new CompoundTag()));
      }
    }

    compoundTag.putInt(STATUS_TAG, status.ordinal());
    compoundTag.putInt(STATE_TIMER_TAG, stateTimer);
    compoundTag.putInt(ENERGY_TAG, currentEnergy);
    compoundTag.putString(BIOME_TAG, cachedBiome);
  }

  @Override
  public int getContainerSize() {
    return container.getContainerSize();
  }

  @Override
  public boolean isEmpty() {
    return container.isEmpty();
  }

  @Override
  public ItemStack getItem(int slot) {
    return container.getItem(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    return container.removeItem(slot, amount);
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return container.removeItemNoUpdate(slot);
  }

  @Override
  public void setItem(int slot, ItemStack itemStack) {
    container.setItem(slot, itemStack);
    setChanged();
  }

  @Override
  public boolean stillValid(Player player) {
    return container.stillValid(player);
  }

  @Override
  public void clearContent() {
    container.clearContent();
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack itemStack) {
    return container.canPlaceItem(slot, itemStack);
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
    return new CollectorStationMenu(windowId, playerInventory, this, containerData);
  }
}
