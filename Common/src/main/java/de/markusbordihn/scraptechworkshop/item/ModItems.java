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

package de.markusbordihn.scraptechworkshop.item;

import de.markusbordihn.scraptechworkshop.Constants;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItems {

  // Basic Scrap Items
  public static Supplier<Item> BIO_SCRAP;
  public static Supplier<Item> CERAMIC_SCRAP;
  public static Supplier<Item> COPPER_SCRAP;
  public static Supplier<Item> CRYSTAL_SCRAP;
  public static Supplier<Item> FASTENER_SCRAP;
  public static Supplier<Item> FIBER_SCRAP;
  public static Supplier<Item> GLASS_SCRAP;
  public static Supplier<Item> GOLD_SCRAP;
  public static Supplier<Item> IRON_SCRAP;
  public static Supplier<Item> LUMINOUS_SCRAP;
  public static Supplier<Item> METAL_SCRAP;
  public static Supplier<Item> MINERAL_SCRAP;
  public static Supplier<Item> PLASTIC_SCRAP;
  public static Supplier<Item> RUBBER_SCRAP;
  public static Supplier<Item> WOOD_SCRAP;

  // Additional Tech Scrap Items
  public static Supplier<Item> CAPACITOR_SCRAP;
  public static Supplier<Item> CIRCUIT_SCRAP;
  public static Supplier<Item> COIL_SCRAP;
  public static Supplier<Item> ENERGY_CELL_SCRAP;
  public static Supplier<Item> TECH_SCRAP;

  // Additional Material and Synthetic Scrap Items
  public static Supplier<Item> ALLOY_SCRAP;
  public static Supplier<Item> INSULATION_SCRAP;

  // Tool Items
  public static Supplier<Item> SCRAP_MULTITOOL;

  // Fishing Rod Items
  public static Supplier<Item> SCRAP_FISHING_ROD;
  public static Supplier<Item> MAGNET_FISHING_ROD;

  // Component Items
  public static Supplier<Item> ENERGY_CELL;
  public static Supplier<Item> SLIGHTLY_DAMAGED_ENERGY_CELL;
  public static Supplier<Item> DAMAGED_ENERGY_CELL;
  public static Supplier<Item> EMPTY_ENERGY_CELL;
  public static Supplier<Item> ENERGY_CELL_BLOCK;
  public static Supplier<Item> CIRCUIT_BOARD;

  // Hololog Items
  public static Supplier<Item> HOLO_PAD;

  // Upgrade Items
  public static Supplier<Item> CREATIVE_SPEED_UPGRADE;

  public static ResourceLocation getItemId(String name) {
    return new ResourceLocation(Constants.MOD_ID, name);
  }
}
