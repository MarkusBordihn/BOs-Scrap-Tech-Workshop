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

package de.markusbordihn.scraptechworkshop.item.multitool;

import de.markusbordihn.scraptechworkshop.config.MultitoolConfig;
import de.markusbordihn.scraptechworkshop.data.energy.EnergyData;
import de.markusbordihn.scraptechworkshop.data.multitool.DisplayMode;
import de.markusbordihn.scraptechworkshop.data.multitool.ScrapMultitoolData;
import de.markusbordihn.scraptechworkshop.data.multitool.ToolMode;
import de.markusbordihn.scraptechworkshop.energy.EnergyManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public record ToolModeDetector(ItemStack itemStack) {

  public ToolMode getToolModeForBlock(BlockState state) {
    if (state.is(BlockTags.MINEABLE_WITH_AXE)) return ToolMode.AXE;
    if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) return ToolMode.PICKAXE;
    if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) return ToolMode.SHOVEL;
    if (state.is(BlockTags.MINEABLE_WITH_HOE)) return ToolMode.HOE;
    return ToolMode.NONE;
  }

  public ToolMode getToolModeForEntity(LivingEntity entity) {
    return entity != null ? ToolMode.SWORD : ToolMode.NONE;
  }

  public void updateToolMode(Player player, BlockState targetBlock) {
    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    EnergyData energyData = EnergyManager.getEnergyData(itemStack, MultitoolConfig.energyMax);

    if (!data.hasBattery() || !energyData.hasEnergy(MultitoolConfig.energyPerBlock)) {
      if (!ToolMode.DEFAULT.getId().equals(data.activeMode())) {
        ScrapMultitoolData newData = data.withActiveMode(ToolMode.DEFAULT.getId());
        newData.saveToItemStack(itemStack);

        // Update display model when switching to default mode
        DisplayMode displayMode = new DisplayMode(itemStack);
        displayMode.updateModel(ToolMode.DEFAULT, newData.getBatteryLevel());
      }
      return;
    }

    ToolMode newMode = ToolMode.DEFAULT;

    if (targetBlock != null) {
      if (targetBlock.is(BlockTags.MINEABLE_WITH_AXE)) {
        newMode = ToolMode.AXE;
      } else if (targetBlock.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
        newMode = ToolMode.PICKAXE;
      } else if (targetBlock.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
        newMode = ToolMode.SHOVEL;
      } else if (targetBlock.is(BlockTags.MINEABLE_WITH_HOE)) {
        newMode = ToolMode.HOE;
      }
    }

    if (!newMode.getId().equals(data.activeMode())) {
      ScrapMultitoolData newData = data.withActiveMode(newMode.getId());
      newData.saveToItemStack(itemStack);

      // Update display model after mode change
      DisplayMode displayMode = new DisplayMode(itemStack);
      displayMode.updateModel(newMode, newData.getBatteryLevel());
    }
  }

  public BlockState getTargetBlock(Level level, Player player) {
    Vec3 start = player.getEyePosition(1.0F);
    Vec3 direction = player.getLookAngle();
    Vec3 end = start.add(direction.scale(MultitoolConfig.raycastDistance));

    BlockHitResult hitResult =
        level.clip(
            new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

    if (hitResult.getType() == HitResult.Type.BLOCK) {
      return level.getBlockState(hitResult.getBlockPos());
    }

    return null;
  }
}
