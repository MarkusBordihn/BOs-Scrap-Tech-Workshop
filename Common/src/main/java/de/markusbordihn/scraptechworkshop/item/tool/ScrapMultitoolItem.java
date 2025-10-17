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

package de.markusbordihn.scraptechworkshop.item.tool;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.config.MultitoolConfig;
import de.markusbordihn.scraptechworkshop.data.block.StrippableBlocks;
import de.markusbordihn.scraptechworkshop.data.multitool.DisplayMode;
import de.markusbordihn.scraptechworkshop.data.multitool.ScrapMultitoolData;
import de.markusbordihn.scraptechworkshop.data.multitool.ToolMode;
import de.markusbordihn.scraptechworkshop.energy.EnergyCellConsumer;
import de.markusbordihn.scraptechworkshop.energy.EnergyManager;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import de.markusbordihn.scraptechworkshop.menu.MenuManager;
import de.markusbordihn.scraptechworkshop.processing.*;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ScrapMultitoolItem extends DiggerItem implements EnergyCellConsumer {

  public ScrapMultitoolItem(final Properties properties) {
    super(
        4.0f,
        -2.4f,
        Tiers.IRON,
        BlockTags.MINEABLE_WITH_PICKAXE,
        properties.durability(MultitoolConfig.energyMax).rarity(Rarity.RARE));
  }

  @Override
  public int getMaxEnergy() {
    return MultitoolConfig.energyMax;
  }

  @Override
  public void onCraftedBy(ItemStack itemStack, Level level, Player player) {
    super.onCraftedBy(itemStack, level, player);

    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    if (!data.hasBattery()) {
      ItemStack battery = new ItemStack(ModItems.SLIGHTLY_DAMAGED_ENERGY_CELL.get());
      if (battery.getItem() instanceof EnergyCellItem energyCell) {
        energyCell.setEnergy(battery, EnergyCellItem.CAPACITY_MAH / 2);
      }
      data = data.withBattery(battery);
      data.saveToItemStack(itemStack);
      syncEnergyDisplay(itemStack);

      DisplayMode displayMode = new DisplayMode(itemStack);
      displayMode.updateModel(ToolMode.fromId(data.activeMode()), data.getBatteryLevel());
    }
  }

  @Override
  public float getDestroySpeed(ItemStack itemStack, BlockState state) {
    ToolModeDetector detector = new ToolModeDetector(itemStack);
    detector.updateToolMode(null, state);

    if (!hasEnergy(itemStack, MultitoolConfig.energyPerBlock)) {
      return 1.0f;
    }

    ToolMode mode = detector.getToolModeForBlock(state);
    if (mode != ToolMode.NONE) {
      return getPoweredSpeed(mode);
    }

    ToolMode activeToolMode =
        ToolMode.fromId(ScrapMultitoolData.fromItemStack(itemStack).activeMode());

    boolean isEffective =
        switch (activeToolMode) {
          case PICKAXE -> state.is(BlockTags.MINEABLE_WITH_PICKAXE);
          case AXE -> state.is(BlockTags.MINEABLE_WITH_AXE);
          case SHOVEL -> state.is(BlockTags.MINEABLE_WITH_SHOVEL);
          case HOE -> state.is(BlockTags.MINEABLE_WITH_HOE);
          default -> state.is(BlockTags.MINEABLE_WITH_PICKAXE);
        };

    return isEffective ? getPoweredSpeed(activeToolMode) : super.getDestroySpeed(itemStack, state);
  }

  @Override
  public boolean hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
    if (attacker instanceof Player) {
      ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
      if (!ToolMode.SWORD.getId().equals(data.activeMode())) {
        ScrapMultitoolData newData = data.withActiveMode(ToolMode.SWORD.getId());
        newData.saveToItemStack(itemStack);

        DisplayMode displayMode = new DisplayMode(itemStack);
        displayMode.updateModel(ToolMode.SWORD, newData.getBatteryLevel());
      }
    }

    return consumeEnergy(itemStack, MultitoolConfig.energyPerAttack);
  }

  @Override
  public boolean mineBlock(
      ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
    if (entity instanceof Player player) {
      ToolModeDetector detector = new ToolModeDetector(itemStack);
      detector.updateToolMode(player, state);
    }

    if (consumeEnergy(itemStack, MultitoolConfig.energyPerBlock)) {
      if (entity instanceof Player player) {
        ToolModeDetector detector = new ToolModeDetector(itemStack);
        detector.updateToolMode(player, state);
      }
      return true;
    }
    return false;
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack itemStack = player.getItemInHand(hand);

    if (player.isShiftKeyDown() && !level.isClientSide) {
      openMultitoolScreen(player, itemStack, hand);
      return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    if (!level.isClientSide) {
      ToolModeDetector detector = new ToolModeDetector(itemStack);
      detector.updateToolMode(player, detector.getTargetBlock(level, player));
    }

    if (!level.isClientSide && hasEnergy(itemStack, MultitoolConfig.energyPerUse)) {
      Vec3 playerPos = player.position().add(0, 1, 0);
      Vec3 particlePos = playerPos.add(player.getLookAngle().scale(1.5));
      if (level instanceof ServerLevel serverLevel) {
        serverLevel.sendParticles(
            ParticleTypes.ELECTRIC_SPARK,
            particlePos.x,
            particlePos.y,
            particlePos.z,
            5,
            0.1,
            0.1,
            0.1,
            0.02);
      }
      level.playSound(
          null,
          player.blockPosition(),
          SoundEvents.BEACON_POWER_SELECT,
          SoundSource.PLAYERS,
          0.5f,
          1.2f);

      consumeEnergy(itemStack, MultitoolConfig.energyPerUse);
    }

    return InteractionResultHolder.pass(itemStack);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    if (player != null && player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }

    ItemStack itemStack = context.getItemInHand();
    if (!hasEnergy(itemStack, MultitoolConfig.energyPerUse)) {
      return InteractionResult.PASS;
    }

    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState state = level.getBlockState(pos);
    Block block = state.getBlock();

    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    ToolMode activeMode = ToolMode.fromId(data.activeMode());
    InteractionResult result;
    ToolMode usedMode = null;

    if (BlockInteractionProcessor.isCycleableBlock(block)
        && context.getClickedFace() != Direction.DOWN) {
      result = BlockInteractionProcessor.processCycleableBlock(context, state);
      if (result.consumesAction()) {
        // Determine which tool mode to use based on the block type
        if (StrippableBlocks.isCycleableWood(block)) {
          usedMode = ToolMode.AXE;
        } else if (block == Blocks.DIRT_PATH || state.getBlock() == Blocks.FARMLAND) {
          usedMode = ToolMode.HOE;
        } else {
          usedMode = ToolMode.SHOVEL;
        }
      }
    } else {
      result =
          switch (activeMode) {
            case AXE -> {
              usedMode = ToolMode.AXE;
              yield AxeInteractionHandler.processAxeInteraction(context);
            }
            case SHOVEL -> {
              usedMode = ToolMode.SHOVEL;
              yield ShovelInteractionHandler.processInteraction(context);
            }
            case SWORD -> {
              usedMode = ToolMode.SWORD;
              yield SwordInteractionHandler.processInteraction(context);
            }
            default -> InteractionResult.PASS;
          };

      if (result == InteractionResult.PASS && activeMode != ToolMode.AXE) {
        result = AxeInteractionHandler.processAxeInteraction(context);
        if (result != InteractionResult.PASS) {
          usedMode = ToolMode.AXE;
        }
      }
      if (result == InteractionResult.PASS && activeMode != ToolMode.SHOVEL) {
        result = ShovelInteractionHandler.processInteraction(context);
        if (result != InteractionResult.PASS) {
          usedMode = ToolMode.SHOVEL;
        }
      }
      if (result == InteractionResult.PASS && activeMode != ToolMode.SWORD) {
        result = SwordInteractionHandler.processInteraction(context);
        if (result != InteractionResult.PASS) {
          usedMode = ToolMode.SWORD;
        }
      }
    }

    if (result.consumesAction()) {
      if (usedMode != null && usedMode != activeMode) {
        ScrapMultitoolData newData = data.withActiveMode(usedMode.getId());
        newData.saveToItemStack(itemStack);

        DisplayMode displayMode = new DisplayMode(itemStack);
        displayMode.updateModel(usedMode, newData.getBatteryLevel());
      }
      consumeEnergy(itemStack, MultitoolConfig.energyPerUse);
    }

    return result != InteractionResult.PASS ? result : super.useOn(context);
  }

  private float getPoweredSpeed(final ToolMode toolMode) {
    return switch (toolMode) {
      case PICKAXE -> 6.0f;
      case AXE -> 6.0f;
      case SHOVEL -> 6.0f;
      case HOE -> 4.0f;
      case SWORD -> 6.0f;
      default -> 6.0f;
    };
  }

  private void openMultitoolScreen(
      final Player player, final ItemStack itemStack, final InteractionHand interactionHand) {
    MenuManager.openMenu(player, itemStack, interactionHand);
  }

  @Override
  public boolean isBarVisible(ItemStack itemStack) {
    return true;
  }

  @Override
  public int getBarColor(ItemStack itemStack) {
    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    if (!data.hasBattery()) {
      return 0xFF0000;
    }
    float energyRatio =
        EnergyManager.getEnergyData(itemStack, MultitoolConfig.energyMax).getPercentage();
    return energyRatio > 0.6f ? 0x00FF00 : energyRatio > 0.3f ? 0xFFFF00 : 0xFF0000;
  }

  @Override
  public void appendHoverText(
      ItemStack itemStack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
    tooltipComponents.add(
        Component.translatable(Constants.ITEM_PREFIX + "scrap_multitool.description"));

    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    if (!data.hasBattery()) {
      tooltipComponents.add(
          Component.literal("Energy: 0%").withStyle(style -> style.withColor(0xFF0000)));
      tooltipComponents.add(
          Component.literal("Battery: Not installed")
              .withStyle(style -> style.withColor(0xFF0000)));
    } else {
      int displayEnergy =
          EnergyManager.getEnergyData(itemStack, MultitoolConfig.energyMax).getDisplayEnergy();
      tooltipComponents.add(
          Component.literal("Energy: " + displayEnergy + "%")
              .withStyle(style -> style.withColor(getBarColor(itemStack))));
      tooltipComponents.add(
          Component.literal("Battery: Installed").withStyle(style -> style.withColor(0x00FF00)));
    }

    ToolMode activeMode = ToolMode.fromId(data.activeMode());
    tooltipComponents.add(
        Component.translatable(activeMode.getTranslationKey())
            .withStyle(style -> style.withColor(activeMode.getColor())));

    tooltipComponents.add(
        Component.literal("Shift + Right-click to configure")
            .withStyle(style -> style.withItalic(true)));

    super.appendHoverText(itemStack, level, tooltipComponents, isAdvanced);
  }

  @Override
  public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
    return false;
  }

  @Override
  public boolean isEnchantable(ItemStack itemStack) {
    return false;
  }
}
