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

package de.markusbordihn.scraptechworkshop.block.entity.floatingscrapcollector;

import static de.markusbordihn.scraptechworkshop.block.entity.floatingscrapcollector.FloatingScrapCollectorSlots.*;

import de.markusbordihn.scraptechworkshop.block.entity.AbstractWorkshopBlockEntity;
import de.markusbordihn.scraptechworkshop.block.floatingscrapcollector.FloatingScrapCollectorBlock;
import de.markusbordihn.scraptechworkshop.data.floatingscrapcollector.FloatingScrapCollectorStatus;
import de.markusbordihn.scraptechworkshop.data.floatingscrapcollector.ScrapFilterType;
import de.markusbordihn.scraptechworkshop.item.ScrapFilterItem;
import de.markusbordihn.scraptechworkshop.menu.FloatingScrapCollectorMenu;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class FloatingScrapCollectorBlockEntity extends AbstractWorkshopBlockEntity {

  private static final String TRANSLATION_KEY =
      "container.scrap_tech_workshop.floating_scrap_collector";
  private static final String PROGRESS_TAG = "Progress";
  private static final String MAX_PROGRESS_TAG = "MaxProgress";
  private static final int DATA_PROGRESS = 0;
  private static final int DATA_MAX_PROGRESS = 1;
  private static final int DATA_COUNT = 2;
  private static final int MIN_COLLECTION_TIME = 120 * 20;
  private static final int MAX_COLLECTION_TIME = 240 * 20;

  public static BlockEntityType<FloatingScrapCollectorBlockEntity> TYPE;

  private final FloatingScrapCollectorContainer container;
  private int progress = 0;
  private int maxProgress = MIN_COLLECTION_TIME;
  private final ContainerData containerData =
      new ContainerData() {
        @Override
        public int get(int index) {
          return switch (index) {
            case DATA_PROGRESS -> progress;
            case DATA_MAX_PROGRESS -> maxProgress;
            default -> 0;
          };
        }

        @Override
        public void set(int index, int value) {
          switch (index) {
            case DATA_PROGRESS -> progress = value;
            case DATA_MAX_PROGRESS -> maxProgress = value;
          }
        }

        @Override
        public int getCount() {
          return DATA_COUNT;
        }
      };

  public FloatingScrapCollectorBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(TYPE, blockPos, blockState);
    this.container = new FloatingScrapCollectorContainer(this);
  }

  public static void tick(
      Level level,
      BlockPos blockPos,
      BlockState blockState,
      FloatingScrapCollectorBlockEntity blockEntity) {
    if (!level.isClientSide) {
      blockEntity.serverTick(level, blockPos, blockState);
    }
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return container.getItems();
  }

  protected int getTotalSlots() {
    return TOTAL_SLOTS;
  }

  @Override
  protected WorldlyContainer getContainerDelegate() {
    return container;
  }

  private void serverTick(Level level, BlockPos blockPos, BlockState blockState) {
    ItemStack netStack = container.getItem(NET_SLOT);
    FloatingScrapCollectorStatus newStatus = determineStatus(netStack);

    if (newStatus == FloatingScrapCollectorStatus.WORKING) {
      progress++;

      if (progress >= maxProgress) {
        collectScrap(level, blockPos, netStack);
        progress = 0;
        maxProgress =
            MIN_COLLECTION_TIME
                + level.getRandom().nextInt(MAX_COLLECTION_TIME - MIN_COLLECTION_TIME);
      }
    } else {
      progress = 0;
    }

    setStatus(blockState, newStatus);
  }

  private FloatingScrapCollectorStatus determineStatus(ItemStack netStack) {
    if (netStack.isEmpty() || !(netStack.getItem() instanceof ScrapFilterItem)) {
      return FloatingScrapCollectorStatus.EMPTY;
    }

    if (ScrapFilterItem.getDurability(netStack) <= 0) {
      return FloatingScrapCollectorStatus.EMPTY;
    }

    if (isOutputFull()) {
      return FloatingScrapCollectorStatus.FULL;
    }

    return FloatingScrapCollectorStatus.WORKING;
  }

  private boolean isOutputFull() {
    for (int i = FIRST_OUTPUT_SLOT; i < TOTAL_SLOTS; i++) {
      ItemStack stack = container.getItem(i);
      if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
        return false;
      }
    }
    return true;
  }

  private void collectScrap(Level level, BlockPos blockPos, ItemStack netStack) {
    if (!(netStack.getItem() instanceof ScrapFilterItem filterItem)) {
      return;
    }
    ScrapFilterType filterType = filterItem.getFilterType();

    // Calculate water quality bonus based on surrounding water blocks
    int waterBlocks = countSurroundingWater(level, blockPos);
    float waterQualityBonus = getWaterQualityBonus(waterBlocks);

    // Generate loot with potential bonus items based on water quality
    List<ItemStack> loot = generateLoot(level, filterType, waterQualityBonus);
    for (ItemStack item : loot) {
      if (!addToOutput(item)) {
        break;
      }
    }

    ScrapFilterItem.consumeDurability(netStack, 1);
    if (ScrapFilterItem.getDurability(netStack) <= 0) {
      container.setItem(NET_SLOT, ItemStack.EMPTY);
    }

    setChanged();
  }

  private int countSurroundingWater(Level level, BlockPos pos) {
    int waterCount = 0;

    // Check all 8 horizontal neighbors and below (9 positions total)
    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        BlockPos checkPos = pos.offset(x, 0, z);
        FluidState fluid = level.getFluidState(checkPos);

        if (fluid.getType() == Fluids.WATER) {
          waterCount++;
        } else if (fluid.getType() == Fluids.FLOWING_WATER) {
          waterCount++;
        }
      }
    }

    // Also check below
    BlockPos belowPos = pos.below();
    FluidState fluidBelow = level.getFluidState(belowPos);
    if (fluidBelow.getType() == Fluids.WATER) {
      waterCount++;
    } else if (fluidBelow.getType() == Fluids.FLOWING_WATER) {
      waterCount++;
    }

    return waterCount;
  }

  private float getWaterQualityBonus(int waterBlocks) {
    if (waterBlocks >= 9) {
      return 0.75f;
    } else if (waterBlocks >= 7) {
      return 0.50f;
    } else if (waterBlocks >= 4) {
      return 0.25f;
    }
    return 0.0f;
  }

  private List<ItemStack> generateLoot(
      Level level, ScrapFilterType filterType, float waterQualityBonus) {
    if (!(level instanceof ServerLevel serverLevel)) {
      return new ArrayList<>();
    }

    String lootTablePath =
        "scrap_tech_workshop:gameplay/floating_scrap_collector/" + filterType.getSerializedName();
    ResourceLocation lootTableId = new ResourceLocation(lootTablePath);
    LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(lootTableId);
    if (lootTable == LootTable.EMPTY) {
      return new ArrayList<>();
    }

    LootParams lootParams =
        new LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
            .create(LootContextParamSets.EMPTY);

    List<ItemStack> loot = lootTable.getRandomItems(lootParams);

    // Apply water quality bonus - chance for extra items
    if (waterQualityBonus > 0 && level.getRandom().nextFloat() < waterQualityBonus) {
      List<ItemStack> bonusLoot = lootTable.getRandomItems(lootParams);
      loot.addAll(bonusLoot);
    }

    return loot;
  }

  private boolean addToOutput(ItemStack newItem) {
    if (newItem.isEmpty()) {
      return false;
    }

    for (int i = FIRST_OUTPUT_SLOT; i < TOTAL_SLOTS; i++) {
      ItemStack existing = container.getItem(i);

      if (existing.isEmpty()) {
        container.setItem(i, newItem.copy());
        return true;
      }

      if (ItemStack.isSameItemSameTags(existing, newItem)) {
        int space = existing.getMaxStackSize() - existing.getCount();
        if (space > 0) {
          int amountToAdd = Math.min(space, newItem.getCount());
          existing.grow(amountToAdd);
          newItem.shrink(amountToAdd);
          if (newItem.isEmpty()) {
            return true;
          }
        }
      }
    }

    return newItem.isEmpty();
  }

  private void setStatus(BlockState blockState, FloatingScrapCollectorStatus newStatus) {
    Level currentLevel = getLevel();
    if (currentLevel != null
        && blockState.hasProperty(FloatingScrapCollectorBlock.STATUS)
        && blockState.getValue(FloatingScrapCollectorBlock.STATUS) != newStatus) {
      currentLevel.setBlock(
          worldPosition, blockState.setValue(FloatingScrapCollectorBlock.STATUS, newStatus), 3);
    }
  }

  public FloatingScrapCollectorContainer getContainer() {
    return container;
  }

  public ContainerData getContainerData() {
    return containerData;
  }

  @Override
  public void load(CompoundTag compoundTag) {
    super.load(compoundTag);
    progress = compoundTag.getInt(PROGRESS_TAG);
    maxProgress = compoundTag.getInt(MAX_PROGRESS_TAG);
  }

  @Override
  protected void saveAdditional(CompoundTag compoundTag) {
    super.saveAdditional(compoundTag);
    compoundTag.putInt(PROGRESS_TAG, progress);
    compoundTag.putInt(MAX_PROGRESS_TAG, maxProgress);
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable(TRANSLATION_KEY);
  }

  @Override
  public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
    return new FloatingScrapCollectorMenu(windowId, playerInventory, this, containerData);
  }
}
