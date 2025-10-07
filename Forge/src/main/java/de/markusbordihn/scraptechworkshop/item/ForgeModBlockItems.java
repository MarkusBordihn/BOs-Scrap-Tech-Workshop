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
import de.markusbordihn.scraptechworkshop.block.ForgeModBlocks;
import de.markusbordihn.scraptechworkshop.block.RecyclerBlock;
import de.markusbordihn.scraptechworkshop.data.ScrapPileVariant;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloCubeItem;
import de.markusbordihn.scraptechworkshop.registry.item.hololog.HoloLogItemRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgeModBlockItems {

  public static final DeferredRegister<Item> BLOCK_ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

  public static final RegistryObject<BlockItem> RECYCLER_BLOCK_ITEM =
      BLOCK_ITEMS.register(
          RecyclerBlock.ID,
          () -> new RecyclerBlockItem(ForgeModBlocks.RECYCLER_BLOCK.get(), new Item.Properties()));

  public static final RegistryObject<BlockItem> MIXED_SCRAP_PILE_BLOCK_ITEM =
      BLOCK_ITEMS.register(
          ScrapPileBlockItem.MIXED_ID,
          () ->
              new ScrapPileBlockItem(
                  ForgeModBlocks.SCRAP_PILE_BLOCK.get(),
                  new Item.Properties(),
                  ScrapPileVariant.MIXED));

  public static final RegistryObject<BlockItem> METAL_SCRAP_PILE_BLOCK_ITEM =
      BLOCK_ITEMS.register(
          ScrapPileBlockItem.METAL_ID,
          () ->
              new ScrapPileBlockItem(
                  ForgeModBlocks.SCRAP_PILE_BLOCK.get(),
                  new Item.Properties(),
                  ScrapPileVariant.METAL));

  public static final RegistryObject<BlockItem> TECH_SCRAP_PILE_BLOCK_ITEM =
      BLOCK_ITEMS.register(
          ScrapPileBlockItem.TECH_ID,
          () ->
              new ScrapPileBlockItem(
                  ForgeModBlocks.SCRAP_PILE_BLOCK.get(),
                  new Item.Properties(),
                  ScrapPileVariant.TECH));

  // Hololog Items
  public static final RegistryObject<BlockItem> HOLO_CUBE_BLOCK_ITEM =
      BLOCK_ITEMS.register(HoloCubeItem.ID, () -> HoloLogItemRegistry.HOLO_CUBE_ITEM);

  private ForgeModBlockItems() {}

  public static void register(IEventBus eventBus) {
    BLOCK_ITEMS.register(eventBus);

    ModBlockItems.RECYCLER = RECYCLER_BLOCK_ITEM;
    ModBlockItems.MIXED_SCRAP_PILE = MIXED_SCRAP_PILE_BLOCK_ITEM;
    ModBlockItems.METAL_SCRAP_PILE = METAL_SCRAP_PILE_BLOCK_ITEM;
    ModBlockItems.TECH_SCRAP_PILE = TECH_SCRAP_PILE_BLOCK_ITEM;
    ModBlockItems.HOLO_CUBE = HOLO_CUBE_BLOCK_ITEM;
  }
}
