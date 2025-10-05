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
import de.markusbordihn.scraptechworkshop.item.hololog.HoloCubeItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {

  public static final ResourceKey<CreativeModeTab> SCRAP_TECH_WORKSHOP_TAB =
      ResourceKey.create(
          Registries.CREATIVE_MODE_TAB,
          new ResourceLocation(Constants.MOD_ID, "scrap_tech_workshop"));

  public static CreativeModeTab.Builder createMainTab() {
    return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
        .title(Component.translatable("itemGroup.scrap_tech_workshop.main"))
        .icon(() -> new ItemStack(ModItems.METAL_SCRAP.get()))
        .displayItems(
            (parameters, output) -> {
              output.accept(ModItems.ALLOY_SCRAP.get());
              output.accept(ModItems.BIO_SCRAP.get());
              output.accept(ModItems.CERAMIC_SCRAP.get());
              output.accept(ModItems.COPPER_SCRAP.get());
              output.accept(ModItems.CRYSTAL_SCRAP.get());
              output.accept(ModItems.FASTENER_SCRAP.get());
              output.accept(ModItems.FIBER_SCRAP.get());
              output.accept(ModItems.GLASS_SCRAP.get());
              output.accept(ModItems.GOLD_SCRAP.get());
              output.accept(ModItems.INSULATION_SCRAP.get());
              output.accept(ModItems.IRON_SCRAP.get());
              output.accept(ModItems.LUMINOUS_SCRAP.get());
              output.accept(ModItems.METAL_SCRAP.get());
              output.accept(ModItems.MINERAL_SCRAP.get());
              output.accept(ModItems.PLASTIC_SCRAP.get());
              output.accept(ModItems.RUBBER_SCRAP.get());
              output.accept(ModItems.TECH_SCRAP.get());
              output.accept(ModItems.WOOD_SCRAP.get());

              // Additional Tech Scrap Items
              output.accept(ModItems.CIRCUIT_SCRAP.get());
              output.accept(ModItems.COIL_SCRAP.get());
              output.accept(ModItems.CAPACITOR_SCRAP.get());
              output.accept(ModItems.ENERGY_CELL_SCRAP.get());

              // Component Items
              output.accept(ModItems.ENERGY_CELL.get().getDefaultInstance());
              output.accept(ModItems.SLIGHTLY_DAMAGED_ENERGY_CELL.get().getDefaultInstance());
              output.accept(ModItems.DAMAGED_ENERGY_CELL.get().getDefaultInstance());
              output.accept(ModItems.EMPTY_ENERGY_CELL.get());
              output.accept(ModItems.ENERGY_CELL_BLOCK.get());
              output.accept(ModItems.CIRCUIT_BOARD.get());

              // Tool Items
              output.accept(ModItems.SCRAP_MULTITOOL.get());

              // HoloCubes with different holologs
              ItemStack holoCubeStack = new ItemStack(ModBlockItems.HOLOCUBE_BLOCK_ITEM.get());
              HoloCubeItem.setHolologId(
                  holoCubeStack,
                  new ResourceLocation(Constants.MOD_ID, "holologs/intro/introduction"));
              output.accept(holoCubeStack);

              output.accept(ModBlockItems.RECYCLER_BLOCK_ITEM.get());
              output.accept(ModBlockItems.MIXED_SCRAP_PILE_BLOCK_ITEM.get());
              output.accept(ModBlockItems.METAL_SCRAP_PILE_BLOCK_ITEM.get());
              output.accept(ModBlockItems.TECH_SCRAP_PILE_BLOCK_ITEM.get());
            });
  }
}
