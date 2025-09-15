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
import de.markusbordihn.scraptechworkshop.item.scrap.BioScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.CeramicScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.CrystalScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.FastenerScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.FiberScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.GlassScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.LuminousScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.MetalScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.MineralScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.PlasticScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.RubberScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.TechScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.WoodScrapItem;
import de.markusbordihn.scraptechworkshop.registry.item.scrap.ScrapItemRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgeModItems {

  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
      DeferredRegister.create(
          net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

  // Metal Scrap Items
  public static final RegistryObject<Item> SCRAP_METAL =
      ITEMS.register(MetalScrapItem.METAL_SCRAP_ID, () -> ScrapItemRegistry.METAL_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_GOLD =
      ITEMS.register(MetalScrapItem.GOLD_SCRAP_ID, () -> ScrapItemRegistry.GOLD_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_IRON =
      ITEMS.register(MetalScrapItem.IRON_SCRAP_ID, () -> ScrapItemRegistry.IRON_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_COPPER =
      ITEMS.register(MetalScrapItem.COPPER_SCRAP_ID, () -> ScrapItemRegistry.COPPER_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_CERAMIC =
      ITEMS.register(CeramicScrapItem.CERAMIC_SCRAP_ID, () -> ScrapItemRegistry.CERAMIC_SCRAP_ITEM);

  // Other Scrap Items
  public static final RegistryObject<Item> SCRAP_CRYSTAL =
      ITEMS.register(CrystalScrapItem.CRYSTAL_SCRAP_ID, () -> ScrapItemRegistry.CRYSTAL_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_FASTENER =
      ITEMS.register(
          FastenerScrapItem.FASTENER_SCRAP_ID, () -> ScrapItemRegistry.FASTENER_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_LUMINOUS =
      ITEMS.register(
          LuminousScrapItem.LUMINOUS_SCRAP_ID, () -> ScrapItemRegistry.LUMINOUS_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_PLASTIC =
      ITEMS.register(PlasticScrapItem.PLASTIC_SCRAP_ID, () -> ScrapItemRegistry.PLASTIC_SCRAP_ITEM);

  // Additional Material Scrap Items
  public static final RegistryObject<Item> SCRAP_MINERAL =
      ITEMS.register(MineralScrapItem.MINERAL_SCRAP_ID, () -> ScrapItemRegistry.MINERAL_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_WOOD =
      ITEMS.register(WoodScrapItem.WOOD_SCRAP_ID, () -> ScrapItemRegistry.WOOD_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_RUBBER =
      ITEMS.register(RubberScrapItem.RUBBER_SCRAP_ID, () -> ScrapItemRegistry.RUBBER_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_GLASS =
      ITEMS.register(GlassScrapItem.GLASS_SCRAP_ID, () -> ScrapItemRegistry.GLASS_SCRAP_ITEM);

  // Organic and Textile Scrap Items
  public static final RegistryObject<Item> SCRAP_FIBER =
      ITEMS.register(FiberScrapItem.FIBER_SCRAP_ID, () -> ScrapItemRegistry.FIBER_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_BIO =
      ITEMS.register(BioScrapItem.BIO_SCRAP_ID, () -> ScrapItemRegistry.BIO_SCRAP_ITEM);

  // Tech Scrap Items
  public static final RegistryObject<Item> TECH_SCRAP =
      ITEMS.register(TechScrapItem.TECH_SCRAP_ID, () -> ScrapItemRegistry.TECH_SCRAP_ITEM);
  public static final RegistryObject<Item> CIRCUIT_SCRAP =
      ITEMS.register(TechScrapItem.CIRCUIT_SCRAP_ID, () -> ScrapItemRegistry.CIRCUIT_SCRAP_ITEM);
  public static final RegistryObject<Item> COIL_SCRAP =
      ITEMS.register(TechScrapItem.COIL_SCRAP_ID, () -> ScrapItemRegistry.COIL_SCRAP_ITEM);
  public static final RegistryObject<Item> CAPACITOR_SCRAP =
      ITEMS.register(
          TechScrapItem.CAPACITOR_SCRAP_ID, () -> ScrapItemRegistry.CAPACITOR_SCRAP_ITEM);
  public static final RegistryObject<Item> ENERGY_CELL_SCRAP =
      ITEMS.register(
          TechScrapItem.ENERGY_CELL_SCRAP_ID, () -> ScrapItemRegistry.ENERGY_CELL_SCRAP_ITEM);

  public static final RegistryObject<CreativeModeTab> SCRAP_TECH_WORKSHOP_TAB =
      CREATIVE_MODE_TABS.register("scrap_tech_workshop", ModCreativeTabs.createMainTab()::build);

  public static void register(IEventBus eventBus) {
    CREATIVE_MODE_TABS.register(eventBus);

    ITEMS.register(eventBus);

    // Metal Scrap Items
    ModItems.METAL_SCRAP = SCRAP_METAL;
    ModItems.GOLD_SCRAP = SCRAP_GOLD;
    ModItems.IRON_SCRAP = SCRAP_IRON;
    ModItems.COPPER_SCRAP = SCRAP_COPPER;

    // Other Scrap Items
    ModItems.CERAMIC_SCRAP = SCRAP_CERAMIC;
    ModItems.CRYSTAL_SCRAP = SCRAP_CRYSTAL;
    ModItems.FASTENER_SCRAP = SCRAP_FASTENER;
    ModItems.LUMINOUS_SCRAP = SCRAP_LUMINOUS;
    ModItems.PLASTIC_SCRAP = SCRAP_PLASTIC;

    // Additional Material Scrap Items
    ModItems.MINERAL_SCRAP = SCRAP_MINERAL;
    ModItems.WOOD_SCRAP = SCRAP_WOOD;
    ModItems.RUBBER_SCRAP = SCRAP_RUBBER;
    ModItems.GLASS_SCRAP = SCRAP_GLASS;

    // Organic and Textile Scrap Items
    ModItems.FIBER_SCRAP = SCRAP_FIBER;
    ModItems.BIO_SCRAP = SCRAP_BIO;

    // Tech Scrap Items
    ModItems.TECH_SCRAP = TECH_SCRAP;
    ModItems.CIRCUIT_SCRAP = CIRCUIT_SCRAP;
    ModItems.COIL_SCRAP = COIL_SCRAP;
    ModItems.CAPACITOR_SCRAP = CAPACITOR_SCRAP;
    ModItems.ENERGY_CELL_SCRAP = ENERGY_CELL_SCRAP;
  }
}
