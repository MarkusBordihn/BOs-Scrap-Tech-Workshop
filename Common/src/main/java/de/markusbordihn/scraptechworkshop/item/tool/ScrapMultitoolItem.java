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
import de.markusbordihn.scraptechworkshop.data.ScrapMultitoolData;
import de.markusbordihn.scraptechworkshop.menu.ScrapMultitoolMenu;
import de.markusbordihn.scraptechworkshop.menu.ScrapMultitoolMenuProvider;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScrapMultitoolItem extends DiggerItem {

  public static final String ID = "scrap_multitool";
  public static final int ENERGY_MAX = 10000;
  public static final int ENERGY_PER_USE = 1;
  public static final int ENERGY_PER_BLOCK = 2;
  public static final int ENERGY_PER_ATTACK = 5;

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public ScrapMultitoolItem(Properties properties) {
    super(
        4.0f,
        -2.0f,
        Tiers.IRON,
        BlockTags.MINEABLE_WITH_PICKAXE,
        properties.durability(ENERGY_MAX).rarity(Rarity.RARE));
  }

  @Override
  public float getDestroySpeed(ItemStack itemStack, BlockState state) {
    if (!hasEnergy(itemStack, ENERGY_PER_BLOCK)) {
      return 1.0f;
    }

    ToolMode mode = getToolModeForBlock(state);
    if (mode != ToolMode.NONE) {
      return getPoweredSpeed(mode);
    }
    return super.getDestroySpeed(itemStack, state);
  }

  @Override
  public boolean hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
    if (hasEnergy(itemStack, ENERGY_PER_ATTACK)) {
      consumeEnergy(itemStack, ENERGY_PER_ATTACK);
      return true;
    }
    return false;
  }

  @Override
  public boolean mineBlock(
      ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
    if (hasEnergy(itemStack, ENERGY_PER_BLOCK)) {
      consumeEnergy(itemStack, ENERGY_PER_BLOCK);
      return true;
    }
    return false;
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack itemStack = player.getItemInHand(hand);

    // Shift + Right-click opens UI
    if (player.isShiftKeyDown() && !level.isClientSide) {
      openMultitoolScreen(player, itemStack, hand);
      return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    if (!level.isClientSide && hasEnergy(itemStack, ENERGY_PER_USE)) {
      Vec3 playerPos = player.position().add(0, 1, 0);
      Vec3 lookDirection = player.getLookAngle();
      Vec3 particlePos = playerPos.add(lookDirection.scale(1.5));

      if (level instanceof ServerLevel serverLevel) {
        serverLevel.sendParticles(
            ParticleTypes.END_ROD,
            particlePos.x,
            particlePos.y,
            particlePos.z,
            5,
            0.2,
            0.2,
            0.2,
            0.05);
      }

      level.playSound(
          null,
          player.blockPosition(),
          SoundEvents.BEACON_POWER_SELECT,
          SoundSource.PLAYERS,
          0.5f,
          1.2f);

      consumeEnergy(itemStack, ENERGY_PER_USE);
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

  // Energy management
  public boolean hasEnergy(ItemStack itemStack, int amount) {
    return getEnergy(itemStack) >= amount;
  }

  public int getEnergy(ItemStack itemStack) {
    return ENERGY_MAX - itemStack.getDamageValue();
  }

  public void setEnergy(ItemStack itemStack, int energy) {
    itemStack.setDamageValue(ENERGY_MAX - Math.max(1, Math.min(energy, ENERGY_MAX)));
  }

  public void consumeEnergy(ItemStack itemStack, int amount) {
    int currentEnergy = getEnergy(itemStack);
    setEnergy(itemStack, Math.max(1, currentEnergy - amount));
  }

  public void addEnergy(ItemStack itemStack, int amount) {
    int currentEnergy = getEnergy(itemStack);
    setEnergy(itemStack, Math.min(ENERGY_MAX, currentEnergy + amount));
  }

  // Tool mode detection
  public ToolMode getToolModeForBlock(BlockState state) {
    if (state.is(BlockTags.MINEABLE_WITH_AXE)) return ToolMode.AXE;
    if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) return ToolMode.SHOVEL;
    if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) return ToolMode.PICKAXE;
    return ToolMode.NONE;
  }

  public ToolMode getToolModeForEntity(LivingEntity entity) {
    return entity != null ? ToolMode.SWORD : ToolMode.NONE;
  }

  private float getPoweredSpeed(ToolMode mode) {
    return switch (mode) {
      case PICKAXE -> 8.0f;
      case AXE -> 9.0f;
      case SHOVEL -> 7.0f;
      default -> 6.0f;
    };
  }

  public ScrapMultitoolData getData(ItemStack itemStack) {
    CompoundTag tag = itemStack.getOrCreateTag();
    if (tag.contains("MultitoolData")) {
      return ScrapMultitoolData.fromNBT(tag.getCompound("MultitoolData"));
    }
    return ScrapMultitoolData.createDefault();
  }

  public void setData(ItemStack itemStack, ScrapMultitoolData data) {
    CompoundTag tag = itemStack.getOrCreateTag();
    tag.put("MultitoolData", data.toNBT());
  }

  private void openMultitoolScreen(Player player, ItemStack stack, InteractionHand hand) {
    if (Constants.IS_FABRIC) {
      try {
        // Get the MenuFactory interface
        Class<?> menuFactoryInterface =
            Class.forName(
                "de.markusbordihn.scraptechworkshop.menu.ItemBaseScreenHandler$MenuFactory");

        // Create a lambda/proxy for the MenuFactory
        Object menuFactory =
            java.lang.reflect.Proxy.newProxyInstance(
                menuFactoryInterface.getClassLoader(),
                new Class<?>[] {menuFactoryInterface},
                (proxy, method, args) -> {
                  if ("create".equals(method.getName())) {
                    int windowId = (int) args[0];
                    Inventory playerInventory = (Inventory) args[1];
                    ItemStack itemStack = (ItemStack) args[2];
                    InteractionHand h = (InteractionHand) args[3];
                    int slotIndex = (int) args[4];
                    return new ScrapMultitoolMenu(
                        windowId, playerInventory, itemStack, h, slotIndex);
                  }
                  return null;
                });

        // Create ItemBaseScreenHandler with the factory
        Class<?> fabricFactoryClass =
            Class.forName("de.markusbordihn.scraptechworkshop.menu.ItemBaseScreenHandler");
        Object fabricFactory =
            fabricFactoryClass
                .getConstructor(ItemStack.class, InteractionHand.class, menuFactoryInterface)
                .newInstance(stack, hand, menuFactory);
        player.openMenu((net.minecraft.world.MenuProvider) fabricFactory);
      } catch (Exception e) {
        log.error("Failed to open Multitool menu on Fabric: {}", e.getMessage(), e);
        player.openMenu(new ScrapMultitoolMenuProvider(stack, hand));
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
    float energyRatio = (float) getEnergy(itemStack) / ENERGY_MAX;
    return energyRatio > 0.6f ? 0x00FF00 : energyRatio > 0.3f ? 0xFFFF00 : 0xFF0000;
  }

  @Override
  public void appendHoverText(
      ItemStack itemStack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
    tooltipComponents.add(
        Component.translatable(Constants.ITEM_PREFIX + "scrap_multitool.description"));

    int energy = getEnergy(itemStack);
    tooltipComponents.add(
        Component.literal("Energy: " + energy + "/" + ENERGY_MAX)
            .withStyle(style -> style.withColor(getBarColor(itemStack))));

    ScrapMultitoolData data = getData(itemStack);
    if (data.hasBattery()) {
      tooltipComponents.add(
          Component.literal("Battery: Installed").withStyle(style -> style.withColor(0x00FF00)));
    }

    tooltipComponents.add(
        Component.literal("Shift + Right-click to configure")
            .withStyle(style -> style.withItalic(true)));

    super.appendHoverText(itemStack, level, tooltipComponents, isAdvanced);
  }

  @Override
  public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
    return false; // No vanilla repair, only through battery charging
  }

  @Override
  public boolean isEnchantable(ItemStack itemStack) {
    return false; // Upgrades through modules instead
  }

  public enum ToolMode {
    NONE,
    PICKAXE,
    AXE,
    SHOVEL,
    SWORD
  }
}
