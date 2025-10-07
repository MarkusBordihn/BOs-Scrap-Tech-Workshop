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

import de.markusbordihn.scraptechworkshop.block.RecyclerBlock;
import de.markusbordihn.scraptechworkshop.data.ScrapPileVariant;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloCubeItem;
import de.markusbordihn.scraptechworkshop.registry.block.RecyclerBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.ScrapPileBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.hololog.HoloLogItemRegistry;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class FabricModBlockItems {

  private FabricModBlockItems() {}

  public static void registerBlockItems() {
    // Register recycler block item
    ModBlockItems.RECYCLER =
        registerBlockItem(
            RecyclerBlock.ID,
            () ->
                new RecyclerBlockItem(RecyclerBlockRegistry.RECYCLER_BLOCK, new Item.Properties()));

    // Register scrap pile block items
    ModBlockItems.MIXED_SCRAP_PILE =
        registerBlockItem(
            ScrapPileBlockItem.MIXED_ID,
            () ->
                new ScrapPileBlockItem(
                    ScrapPileBlockRegistry.SCRAP_PILE_BLOCK,
                    new Item.Properties(),
                    ScrapPileVariant.MIXED));

    ModBlockItems.METAL_SCRAP_PILE =
        registerBlockItem(
            ScrapPileBlockItem.METAL_ID,
            () ->
                new ScrapPileBlockItem(
                    ScrapPileBlockRegistry.SCRAP_PILE_BLOCK,
                    new Item.Properties(),
                    ScrapPileVariant.METAL));

    ModBlockItems.TECH_SCRAP_PILE =
        registerBlockItem(
            ScrapPileBlockItem.TECH_ID,
            () ->
                new ScrapPileBlockItem(
                    ScrapPileBlockRegistry.SCRAP_PILE_BLOCK,
                    new Item.Properties(),
                    ScrapPileVariant.TECH));

    // Hololog Items
    ModBlockItems.HOLO_CUBE =
        registerBlockItem(HoloCubeItem.ID, () -> HoloLogItemRegistry.HOLO_CUBE_ITEM);
  }

  private static Supplier<BlockItem> registerBlockItem(
      String name, Supplier<BlockItem> blockItemSupplier) {
    BlockItem blockItem = blockItemSupplier.get();
    Registry.register(BuiltInRegistries.ITEM, ModBlockItems.getBlockItemId(name), blockItem);
    return () -> blockItem;
  }
}
