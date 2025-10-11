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

package de.markusbordihn.scraptechworkshop.block;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.collectorstation.BasicScrapCollectorStationBlock;
import de.markusbordihn.scraptechworkshop.block.hololog.HoloCubeBlock;
import de.markusbordihn.scraptechworkshop.block.recycler.RecyclerBlock;
import de.markusbordihn.scraptechworkshop.block.scrap.ScrapPileBlock;
import de.markusbordihn.scraptechworkshop.registry.block.CollectorStationBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.RecyclerBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.ScrapPileBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.hololog.HoloLogBlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgeModBlocks {

  public static final DeferredRegister<Block> BLOCKS =
      DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

  public static final RegistryObject<Block> RECYCLER_BLOCK =
      BLOCKS.register(RecyclerBlock.ID, () -> RecyclerBlockRegistry.RECYCLER_BLOCK);

  public static final RegistryObject<Block> SCRAP_PILE_BLOCK =
      BLOCKS.register(ScrapPileBlock.ID, () -> ScrapPileBlockRegistry.SCRAP_PILE_BLOCK);

  public static final RegistryObject<Block> HOLO_CUBE_BLOCK =
      BLOCKS.register(HoloCubeBlock.ID, () -> HoloLogBlockRegistry.HOLO_CUBE_BLOCK);

  public static final RegistryObject<Block> BASIC_SCRAP_COLLECTOR_STATION_BLOCK =
      BLOCKS.register(
          BasicScrapCollectorStationBlock.ID,
          () -> CollectorStationBlockRegistry.BASIC_SCRAP_COLLECTOR_STATION_BLOCK);

  private ForgeModBlocks() {}

  public static void register(IEventBus eventBus) {
    BLOCKS.register(eventBus);
  }
}
