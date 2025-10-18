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
import de.markusbordihn.scraptechworkshop.block.rechargestation.RechargeStationBlock;
import de.markusbordihn.scraptechworkshop.block.recycler.RecyclerBlock;
import de.markusbordihn.scraptechworkshop.block.scrap.ScrapPileBlock;
import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import de.markusbordihn.scraptechworkshop.registry.block.CollectorStationBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.RechargeStationBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.RecyclerBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.ScrapBoxBlockRegistry;
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

  public static final RegistryObject<Block> RECHARGE_STATION_BLOCK =
      BLOCKS.register(
          RechargeStationBlock.ID, () -> RechargeStationBlockRegistry.RECHARGE_STATION_BLOCK);

  public static final RegistryObject<Block> SCRAP_PILE_BLOCK =
      BLOCKS.register(ScrapPileBlock.ID, () -> ScrapPileBlockRegistry.SCRAP_PILE_BLOCK);

  public static final RegistryObject<Block> HOLO_CUBE_BLOCK =
      BLOCKS.register(HoloCubeBlock.ID, () -> HoloLogBlockRegistry.HOLO_CUBE_BLOCK);

  public static final RegistryObject<Block> BASIC_SCRAP_COLLECTOR_STATION_BLOCK =
      BLOCKS.register(
          BasicScrapCollectorStationBlock.ID,
          () -> CollectorStationBlockRegistry.BASIC_SCRAP_COLLECTOR_STATION_BLOCK);

  public static final RegistryObject<Block> ALLOY_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.ALLOY.getBlockId(), () -> ScrapBoxBlockRegistry.ALLOY_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> BIO_SCRAP_BOX_BLOCK =
      BLOCKS.register(ScrapType.BIO.getBlockId(), () -> ScrapBoxBlockRegistry.BIO_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> CAPACITOR_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.CAPACITOR.getBlockId(), () -> ScrapBoxBlockRegistry.CAPACITOR_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> CERAMIC_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.CERAMIC.getBlockId(), () -> ScrapBoxBlockRegistry.CERAMIC_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> CIRCUIT_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.CIRCUIT.getBlockId(), () -> ScrapBoxBlockRegistry.CIRCUIT_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> COIL_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.COIL.getBlockId(), () -> ScrapBoxBlockRegistry.COIL_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> COPPER_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.COPPER.getBlockId(), () -> ScrapBoxBlockRegistry.COPPER_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> CRYSTAL_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.CRYSTAL.getBlockId(), () -> ScrapBoxBlockRegistry.CRYSTAL_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> ENERGY_CELL_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.ENERGY_CELL.getBlockId(),
          () -> ScrapBoxBlockRegistry.ENERGY_CELL_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> FASTENER_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.FASTENER.getBlockId(), () -> ScrapBoxBlockRegistry.FASTENER_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> FIBER_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.FIBER.getBlockId(), () -> ScrapBoxBlockRegistry.FIBER_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> GLASS_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.GLASS.getBlockId(), () -> ScrapBoxBlockRegistry.GLASS_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> GOLD_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.GOLD.getBlockId(), () -> ScrapBoxBlockRegistry.GOLD_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> INSULATION_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.INSULATION.getBlockId(),
          () -> ScrapBoxBlockRegistry.INSULATION_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> IRON_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.IRON.getBlockId(), () -> ScrapBoxBlockRegistry.IRON_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> LUMINOUS_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.LUMINOUS.getBlockId(), () -> ScrapBoxBlockRegistry.LUMINOUS_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> METAL_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.METAL.getBlockId(), () -> ScrapBoxBlockRegistry.METAL_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> MINERAL_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.MINERAL.getBlockId(), () -> ScrapBoxBlockRegistry.MINERAL_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> PLASTIC_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.PLASTIC.getBlockId(), () -> ScrapBoxBlockRegistry.PLASTIC_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> RUBBER_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.RUBBER.getBlockId(), () -> ScrapBoxBlockRegistry.RUBBER_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> TECH_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.TECH.getBlockId(), () -> ScrapBoxBlockRegistry.TECH_SCRAP_BOX_BLOCK);
  public static final RegistryObject<Block> WOOD_SCRAP_BOX_BLOCK =
      BLOCKS.register(
          ScrapType.WOOD.getBlockId(), () -> ScrapBoxBlockRegistry.WOOD_SCRAP_BOX_BLOCK);

  private ForgeModBlocks() {}

  public static void register(IEventBus eventBus) {
    BLOCKS.register(eventBus);
  }
}
