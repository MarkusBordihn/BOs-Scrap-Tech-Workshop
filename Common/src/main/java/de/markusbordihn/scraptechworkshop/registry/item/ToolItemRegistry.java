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

package de.markusbordihn.scraptechworkshop.registry.item;

import de.markusbordihn.scraptechworkshop.data.wire.WireType;
import de.markusbordihn.scraptechworkshop.item.component.CircuitBoardItem;
import de.markusbordihn.scraptechworkshop.item.component.EmptyEnergyCellItem;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellBlockItem;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import de.markusbordihn.scraptechworkshop.item.component.WireItem;
import de.markusbordihn.scraptechworkshop.item.tool.CreativeScrapMultitoolItem;
import de.markusbordihn.scraptechworkshop.item.tool.MagnetFishingRodItem;
import de.markusbordihn.scraptechworkshop.item.tool.ScrapFishingRodItem;
import de.markusbordihn.scraptechworkshop.item.tool.ScrapMultitoolItem;
import de.markusbordihn.scraptechworkshop.item.upgrade.CreativeFastChargeUpgradeItem;
import de.markusbordihn.scraptechworkshop.item.upgrade.CreativeSpeedUpgradeItem;
import de.markusbordihn.scraptechworkshop.item.upgrade.FastChargeUpgradeItem;
import de.markusbordihn.scraptechworkshop.item.upgrade.NormalSpeedUpgradeItem;
import net.minecraft.world.item.Item;

public class ToolItemRegistry {

  // Tool Items
  public static final ScrapMultitoolItem SCRAP_MULTITOOL_ITEM =
      new ScrapMultitoolItem(new Item.Properties());
  public static final CreativeScrapMultitoolItem CREATIVE_SCRAP_MULTITOOL_ITEM =
      new CreativeScrapMultitoolItem(new Item.Properties());

  // Fishing Rod Items
  public static final ScrapFishingRodItem SCRAP_FISHING_ROD_ITEM =
      new ScrapFishingRodItem(new Item.Properties().durability(128));
  public static final MagnetFishingRodItem MAGNET_FISHING_ROD_ITEM =
      new MagnetFishingRodItem(new Item.Properties().durability(256));

  // Component Items
  public static final EnergyCellItem ENERGY_CELL_ITEM = new EnergyCellItem(new Item.Properties());
  public static final EnergyCellItem SLIGHTLY_DAMAGED_ENERGY_CELL_ITEM =
      new EnergyCellItem(new Item.Properties(), 2500);
  public static final EnergyCellItem DAMAGED_ENERGY_CELL_ITEM =
      new EnergyCellItem(new Item.Properties(), 1250);
  public static final EmptyEnergyCellItem EMPTY_ENERGY_CELL_ITEM =
      new EmptyEnergyCellItem(new Item.Properties());
  public static final EnergyCellBlockItem ENERGY_CELL_BLOCK_ITEM =
      new EnergyCellBlockItem(new Item.Properties());
  public static final CircuitBoardItem CIRCUIT_BOARD_ITEM =
      new CircuitBoardItem(new Item.Properties());
  public static final WireItem RECLAIMED_COPPER_WIRE_ITEM =
      new WireItem(new Item.Properties(), WireType.RECLAIMED_COPPER);
  public static final WireItem REFINED_COPPER_WIRE_ITEM =
      new WireItem(new Item.Properties(), WireType.REFINED_COPPER);

  // Upgrade Items
  public static final NormalSpeedUpgradeItem SPEED_UPGRADE_ITEM =
      new NormalSpeedUpgradeItem(new Item.Properties());
  public static final CreativeSpeedUpgradeItem CREATIVE_SPEED_UPGRADE_ITEM =
      new CreativeSpeedUpgradeItem(new Item.Properties());
  public static final FastChargeUpgradeItem FAST_CHARGE_UPGRADE_ITEM =
      new FastChargeUpgradeItem(new Item.Properties());
  public static final CreativeFastChargeUpgradeItem CREATIVE_FAST_CHARGE_UPGRADE_ITEM =
      new CreativeFastChargeUpgradeItem(new Item.Properties());

  private ToolItemRegistry() {}
}
