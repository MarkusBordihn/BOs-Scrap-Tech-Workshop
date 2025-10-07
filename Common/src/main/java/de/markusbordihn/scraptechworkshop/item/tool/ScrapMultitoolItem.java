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
import de.markusbordihn.scraptechworkshop.data.energy.EnergyData;
import de.markusbordihn.scraptechworkshop.data.multitool.*;
import de.markusbordihn.scraptechworkshop.energy.EnergyManager;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import de.markusbordihn.scraptechworkshop.menu.ScrapMultitoolMenuProvider;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScrapMultitoolItem extends DiggerItem {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public ScrapMultitoolItem(final Properties properties) {
    super(
        4.0f,
        -2.4f,
        Tiers.IRON,
        BlockTags.MINEABLE_WITH_PICKAXE,
        properties.durability(MultitoolConfig.energyMax).rarity(Rarity.RARE));
  }

  @Override
  public void onCraftedBy(ItemStack itemStack, Level level, Player player) {
    super.onCraftedBy(itemStack, level, player);

    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    if (!data.hasBattery()) {
      ItemStack battery = new ItemStack(ModItems.SLIGHTLY_DAMAGED_ENERGY_CELL.get());
      if (battery.getItem() instanceof EnergyCellItem energyCell) {
        energyCell.setEnergy(battery, EnergyCellItem.ENERGY_MAX / 2);
      }
      data = data.withBattery(battery);
      data.saveToItemStack(itemStack);
      syncEnergyWithBattery(itemStack);

      DisplayMode displayMode = new DisplayMode(itemStack);
      displayMode.updateModel(ToolMode.fromId(data.activeMode()), data.getBatteryLevel());
    }
  }

  @Override
  public float getDestroySpeed(ItemStack itemStack, BlockState state) {
    ToolModeDetector detector = new ToolModeDetector(itemStack);
    detector.updateToolMode(null, state);

    if (!hasEnergyFromBattery(itemStack, MultitoolConfig.energyPerBlock)) {
      return 1.0f;
    }

    ToolMode mode = detector.getToolModeForBlock(state);
    if (mode != ToolMode.NONE) {
      return getPoweredSpeed(mode);
    }

    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    ToolMode activeToolMode = ToolMode.fromId(data.activeMode());

    boolean isEffective =
        switch (activeToolMode) {
          case PICKAXE -> state.is(BlockTags.MINEABLE_WITH_PICKAXE);
          case AXE -> state.is(BlockTags.MINEABLE_WITH_AXE);
          case SHOVEL -> state.is(BlockTags.MINEABLE_WITH_SHOVEL);
          case HOE -> state.is(BlockTags.MINEABLE_WITH_HOE);
          default -> state.is(BlockTags.MINEABLE_WITH_PICKAXE);
        };

    if (isEffective) {
      return getPoweredSpeed(activeToolMode);
    }

    return super.getDestroySpeed(itemStack, state);
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

    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    return consumeEnergyFromBattery(itemStack, data, MultitoolConfig.energyPerAttack);
  }

  @Override
  public boolean mineBlock(
      ItemStack itemStack,
      Level level,
      BlockState state,
      net.minecraft.core.BlockPos pos,
      LivingEntity entity) {
    if (entity instanceof Player player) {
      ToolModeDetector detector = new ToolModeDetector(itemStack);
      detector.updateToolMode(player, state);
    }

    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    if (consumeEnergyFromBattery(itemStack, data, MultitoolConfig.energyPerBlock)) {
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
      BlockState targetBlock = detector.getTargetBlock(level, player);
      detector.updateToolMode(player, targetBlock);
    }

    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    if (!level.isClientSide && hasEnergyFromBattery(itemStack, MultitoolConfig.energyPerUse)) {
      Vec3 playerPos = player.position().add(0, 1, 0);
      Vec3 lookDirection = player.getLookAngle();
      Vec3 particlePos = playerPos.add(lookDirection.scale(1.5));

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

      consumeEnergyFromBattery(itemStack, data, MultitoolConfig.energyPerUse);
    }

    return InteractionResultHolder.pass(itemStack);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
      return InteractionResult.PASS;
    }
    return super.useOn(context);
  }

  private float getPoweredSpeed(ToolMode mode) {
    return switch (mode) {
      case PICKAXE -> 6.0f;
      case AXE -> 6.0f;
      case SHOVEL -> 6.0f;
      case HOE -> 4.0f;
      case SWORD -> 6.0f;
      default -> 6.0f;
    };
  }

  private void openMultitoolScreen(Player player, ItemStack stack, InteractionHand hand) {
    if (Constants.IS_FABRIC) {
      try {
        Class<?> fabricHandlerClass =
            Class.forName("de.markusbordihn.scraptechworkshop.menu.ScrapMultitoolScreenHandler");
        Object fabricHandler =
            fabricHandlerClass
                .getConstructor(ItemStack.class, InteractionHand.class)
                .newInstance(stack, hand);
        player.openMenu((MenuProvider) fabricHandler);
      } catch (Exception e) {
        log.error("Failed to open multitool screen on Fabric: {}", e.getMessage());
      }
    } else {
      player.openMenu(new ScrapMultitoolMenuProvider(stack, hand));
    }
  }

  @Override
  public boolean isBarVisible(ItemStack itemStack) {
    return true;
  }

  @Override
  public int getBarColor(ItemStack itemStack) {
    EnergyData energyData = EnergyManager.getEnergyData(itemStack, MultitoolConfig.energyMax);
    float energyRatio = energyData.getPercentage();
    return energyRatio > 0.6f ? 0x00FF00 : energyRatio > 0.3f ? 0xFFFF00 : 0xFF0000;
  }

  @Override
  public void appendHoverText(
      ItemStack itemStack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
    tooltipComponents.add(
        Component.translatable(Constants.ITEM_PREFIX + "scrap_multitool.description"));

    EnergyData energyData = EnergyManager.getEnergyData(itemStack, MultitoolConfig.energyMax);
    int displayEnergy = energyData.getDisplayEnergy();
    tooltipComponents.add(
        Component.literal("Energy: " + displayEnergy + "%")
            .withStyle(style -> style.withColor(getBarColor(itemStack))));

    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    if (data.hasBattery()) {
      tooltipComponents.add(
          Component.literal("Battery: Installed").withStyle(style -> style.withColor(0x00FF00)));
    }

    String mode = data.activeMode();
    ToolMode activeMode = ToolMode.fromId(mode);
    Component modeComponent =
        switch (activeMode) {
          case AXE ->
              Component.literal("Mode: Axe (Auto-detected)")
                  .withStyle(style -> style.withColor(0xFF8800));
          case PICKAXE ->
              Component.literal("Mode: Pickaxe (Auto-detected)")
                  .withStyle(style -> style.withColor(0x888888));
          case SHOVEL ->
              Component.literal("Mode: Shovel (Auto-detected)")
                  .withStyle(style -> style.withColor(0xBB8844));
          case HOE ->
              Component.literal("Mode: Hoe (Auto-detected)")
                  .withStyle(style -> style.withColor(0x00AA00));
          case SWORD ->
              Component.literal("Mode: Sword (Combat)")
                  .withStyle(style -> style.withColor(0xFF0000));
          default ->
              Component.literal("Mode: Normal").withStyle(style -> style.withColor(0xAAAAAA));
        };
    tooltipComponents.add(modeComponent);

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

  public void syncEnergyWithBattery(ItemStack itemStack) {
    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);

    if (data.hasBattery()) {
      ItemStack battery = data.battery();
      if (battery.getItem() instanceof EnergyCellItem batteryItem) {
        int batteryEnergy = batteryItem.getEnergy(battery);
        if (batteryEnergy <= 1) {
          ItemStack emptyBattery = batteryItem.createEmptyBattery();
          ScrapMultitoolData updatedData = data.withBattery(emptyBattery);
          updatedData.saveToItemStack(itemStack);
          EnergyManager.setEnergy(itemStack, MultitoolConfig.energyMax, 0);
          return;
        }

        // Only sync display energy without consuming battery
        float energyRatio = (float) batteryEnergy / EnergyCellItem.ENERGY_MAX;
        int multitoolEnergy = Math.round(MultitoolConfig.energyMax * energyRatio);
        EnergyManager.setEnergy(itemStack, MultitoolConfig.energyMax, multitoolEnergy);
      } else {
        EnergyManager.setEnergy(itemStack, MultitoolConfig.energyMax, 0);
      }
    } else {
      EnergyManager.setEnergy(itemStack, MultitoolConfig.energyMax, 0);
    }
  }

  private boolean consumeEnergyFromBattery(
      ItemStack itemStack, ScrapMultitoolData data, int amount) {
    if (!data.hasBattery()) {
      return false;
    }

    ItemStack battery = data.battery();
    if (!(battery.getItem() instanceof EnergyCellItem)) {
      return false;
    }

    int batteryEnergy = ((EnergyCellItem) battery.getItem()).getEnergy(battery);
    if (batteryEnergy < amount) {
      return false;
    }

    EnergyManager.consumeWithBatteryBackup(itemStack, MultitoolConfig.energyMax, amount, battery);

    ScrapMultitoolData updatedData = data.withBattery(battery);
    updatedData.saveToItemStack(itemStack);

    syncEnergyWithBattery(itemStack);
    return true;
  }

  private boolean hasEnergyFromBattery(ItemStack itemStack, int amount) {
    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    if (!data.hasBattery()) {
      return false;
    }

    ItemStack battery = data.battery();
    if (!(battery.getItem() instanceof EnergyCellItem batteryItem)) {
      return false;
    }

    return batteryItem.getEnergy(battery) >= amount;
  }
}
