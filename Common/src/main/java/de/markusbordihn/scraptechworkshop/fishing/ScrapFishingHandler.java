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

package de.markusbordihn.scraptechworkshop.fishing;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.config.ScrapFishingConfig;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import de.markusbordihn.scraptechworkshop.loot.ScrapFishingLootTables;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScrapFishingHandler {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static void handleFishing(final ServerPlayer serverPlayer, final ServerLevel serverLevel) {
    ItemStack fishingRodStack = getFishingRod(serverPlayer);
    if (!fishingRodStack.isEmpty()) {
      handleFishing(serverPlayer, serverLevel, fishingRodStack);
    }
  }

  public static void handleFishing(
      final ServerPlayer serverPlayer,
      final ServerLevel serverLevel,
      final ItemStack fishingRodItemStack) {

    // Determine the actual fishing rod to use
    ItemStack actualRodStack =
        fishingRodItemStack.isEmpty() ? getFishingRod(serverPlayer) : fishingRodItemStack;
    if (actualRodStack.isEmpty()) {
      return;
    }
    Item fishingRod = actualRodStack.getItem();
    log.debug("Fishing event triggered for player {} with rod {}", serverPlayer, fishingRod);

    // Check if we have a valid fishing rod with a scrap chance
    int scrapChance = getScrapChanceForRod(fishingRod);
    if (scrapChance > 0 && serverLevel.random.nextInt(100) < scrapChance) {
      ItemStack scrapStack = generateScrapItem(fishingRod, serverLevel);
      ItemEntity itemEntity =
          new ItemEntity(
              serverLevel,
              serverPlayer.getX(),
              serverPlayer.getY() + 0.5,
              serverPlayer.getZ(),
              scrapStack);
      itemEntity.setNoPickUpDelay();
      serverLevel.addFreshEntity(itemEntity);
      playScrapSound(serverLevel, serverPlayer, fishingRod);
      logScrapFished(serverPlayer, scrapStack.getItem(), fishingRod, scrapChance);
    }
  }

  private static ItemStack getFishingRod(final ServerPlayer serverPlayer) {
    ItemStack mainHand = serverPlayer.getMainHandItem();
    if (!mainHand.isEmpty() && mainHand.getItem() instanceof FishingRodItem) {
      return mainHand;
    }
    ItemStack offHand = serverPlayer.getOffhandItem();
    if (!offHand.isEmpty() && offHand.getItem() instanceof FishingRodItem) {
      return offHand;
    }
    return ItemStack.EMPTY;
  }

  private static ItemStack generateScrapItem(final Item rod, final ServerLevel serverLevel) {
    Item scrapItem = getScrapItemForRod(rod, serverLevel);
    return new ItemStack(scrapItem, 1);
  }

  private static void playScrapSound(
      final ServerLevel serverLevel, final ServerPlayer player, final Item rod) {
    if (!ScrapFishingConfig.enableSounds) {
      return;
    }
    float volume = ScrapFishingConfig.soundVolume;
    boolean isUpgradedRod = isScrapRodOrBetter(rod);
    serverLevel.playSound(
        null,
        player.blockPosition(),
        isUpgradedRod ? SoundEvents.ANVIL_LAND : SoundEvents.IRON_GOLEM_HURT,
        SoundSource.PLAYERS,
        volume * (isUpgradedRod ? 0.5f : 0.3f),
        (isUpgradedRod ? 1.2f : 1.5f)
            + serverLevel.random.nextFloat() * (isUpgradedRod ? 0.2f : 0.3f));
  }

  private static void logScrapFished(
      final ServerPlayer serverPlayer,
      final Item scrapItem,
      final Item rod,
      final int scrapChance) {
    log.debug(
        "Player {} fished {} with {} ({}% chance)",
        serverPlayer.getName().getString(),
        new ItemStack(scrapItem).getDisplayName().getString(),
        rod,
        scrapChance);
  }

  private static int getScrapChanceForRod(final Item item) {
    if (item == ModItems.MAGNET_FISHING_ROD.get()) {
      return ScrapFishingConfig.magnetRodScrapChance;
    }
    if (item == ModItems.SCRAP_FISHING_ROD.get()) {
      return ScrapFishingConfig.scrapRodScrapChance;
    }
    if (item == Items.FISHING_ROD) {
      return ScrapFishingConfig.vanillaRodScrapChance;
    }
    return 0;
  }

  private static boolean isScrapRodOrBetter(final Item item) {
    return item == ModItems.MAGNET_FISHING_ROD.get() || item == ModItems.SCRAP_FISHING_ROD.get();
  }

  private static Item getScrapItemForRod(final Item item, final ServerLevel serverLevel) {
    if (item == ModItems.MAGNET_FISHING_ROD.get()) {
      return ScrapFishingLootTables.getMagnetRodLoot(serverLevel.random);
    }
    if (item == ModItems.SCRAP_FISHING_ROD.get()) {
      return ScrapFishingLootTables.getScrapRodLoot(serverLevel.random);
    }
    return ScrapFishingLootTables.getVanillaRodLoot(serverLevel.random);
  }
}
