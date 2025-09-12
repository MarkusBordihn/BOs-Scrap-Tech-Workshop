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
import de.markusbordihn.scraptechworkshop.item.scrap.MetalScrapItem;
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

  public static final RegistryObject<Item> FORGE_SCRAP_METAL =
      ITEMS.register(
          MetalScrapItem.METAL_SCRAP_ID, () -> new MetalScrapItem(new Item.Properties()));

  public static final RegistryObject<Item> FORGE_SCRAP_GOLD =
      ITEMS.register(
          MetalScrapItem.GOLD_SCRAP_ID,
          () -> new MetalScrapItem(new Item.Properties(), MetalScrapItem.GOLD_SCRAP_ID));

  public static final RegistryObject<Item> FORGE_SCRAP_IRON =
      ITEMS.register(
          MetalScrapItem.IRON_SCRAP_ID,
          () -> new MetalScrapItem(new Item.Properties(), MetalScrapItem.IRON_SCRAP_ID));

  public static final RegistryObject<Item> FORGE_SCRAP_COPPER =
      ITEMS.register(
          MetalScrapItem.COPPER_SCRAP_ID,
          () -> new MetalScrapItem(new Item.Properties(), MetalScrapItem.COPPER_SCRAP_ID));

  public static final RegistryObject<CreativeModeTab> SCRAP_TECH_WORKSHOP_TAB =
      CREATIVE_MODE_TABS.register("scrap_tech_workshop", ModCreativeTabs.createMainTab()::build);

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
    CREATIVE_MODE_TABS.register(eventBus);
    ModItems.SCRAP_METAL = FORGE_SCRAP_METAL;
    ModItems.SCRAP_GOLD = FORGE_SCRAP_GOLD;
    ModItems.SCRAP_IRON = FORGE_SCRAP_IRON;
    ModItems.SCRAP_COPPER = FORGE_SCRAP_COPPER;
  }
}
