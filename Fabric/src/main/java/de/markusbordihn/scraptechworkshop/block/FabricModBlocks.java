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
import de.markusbordihn.scraptechworkshop.block.deco.NeonTubeBlock;
import de.markusbordihn.scraptechworkshop.block.deco.ReplicantTestLampBlock;
import de.markusbordihn.scraptechworkshop.block.floatingscrapcollector.FloatingScrapCollectorBlock;
import de.markusbordihn.scraptechworkshop.block.hololog.HoloCubeBlock;
import de.markusbordihn.scraptechworkshop.block.rechargestation.RechargeStationBlock;
import de.markusbordihn.scraptechworkshop.block.recycler.RecyclerBlock;
import de.markusbordihn.scraptechworkshop.block.scrap.ScrapPileBlock;
import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import de.markusbordihn.scraptechworkshop.registry.block.CollectorStationBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.DecoBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.FloatingScrapCollectorBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.RechargeStationBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.RecyclerBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.ScrapBoxBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.ScrapPileBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.hololog.HoloLogBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class FabricModBlocks {

  private FabricModBlocks() {}

  public static void registerBlocks() {
    // Register recycler block
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, RecyclerBlock.ID),
        RecyclerBlockRegistry.RECYCLER_BLOCK);

    // Register recharge station block
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, RechargeStationBlock.ID),
        RechargeStationBlockRegistry.RECHARGE_STATION_BLOCK);

    // Register scrap pile block
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapPileBlock.ID),
        ScrapPileBlockRegistry.SCRAP_PILE_BLOCK);

    // Register holocube block
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, HoloCubeBlock.ID),
        HoloLogBlockRegistry.HOLO_CUBE_BLOCK);

    // Register collector station block
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, BasicScrapCollectorStationBlock.ID),
        CollectorStationBlockRegistry.BASIC_SCRAP_COLLECTOR_STATION_BLOCK);

    // Register floating scrap collector block
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, FloatingScrapCollectorBlock.ID),
        FloatingScrapCollectorBlockRegistry.FLOATING_SCRAP_COLLECTOR_BLOCK);

    // Register neon tube block
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, NeonTubeBlock.ID),
        DecoBlockRegistry.NEON_TUBE_BLOCK);

    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ReplicantTestLampBlock.ID),
        DecoBlockRegistry.REPLICANT_TEST_LAMP_BLOCK);

    // Register scrap box blocks
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.ALLOY.getBlockId()),
        ScrapBoxBlockRegistry.ALLOY_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.BIO.getBlockId()),
        ScrapBoxBlockRegistry.BIO_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.CAPACITOR.getBlockId()),
        ScrapBoxBlockRegistry.CAPACITOR_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.CERAMIC.getBlockId()),
        ScrapBoxBlockRegistry.CERAMIC_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.CIRCUIT.getBlockId()),
        ScrapBoxBlockRegistry.CIRCUIT_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.COIL.getBlockId()),
        ScrapBoxBlockRegistry.COIL_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.COPPER.getBlockId()),
        ScrapBoxBlockRegistry.COPPER_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.CRYSTAL.getBlockId()),
        ScrapBoxBlockRegistry.CRYSTAL_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.ENERGY_CELL.getBlockId()),
        ScrapBoxBlockRegistry.ENERGY_CELL_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.FASTENER.getBlockId()),
        ScrapBoxBlockRegistry.FASTENER_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.FIBER.getBlockId()),
        ScrapBoxBlockRegistry.FIBER_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.GLASS.getBlockId()),
        ScrapBoxBlockRegistry.GLASS_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.GOLD.getBlockId()),
        ScrapBoxBlockRegistry.GOLD_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.INSULATION.getBlockId()),
        ScrapBoxBlockRegistry.INSULATION_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.IRON.getBlockId()),
        ScrapBoxBlockRegistry.IRON_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.LUMINOUS.getBlockId()),
        ScrapBoxBlockRegistry.LUMINOUS_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.METAL.getBlockId()),
        ScrapBoxBlockRegistry.METAL_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.MINERAL.getBlockId()),
        ScrapBoxBlockRegistry.MINERAL_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.PLASTIC.getBlockId()),
        ScrapBoxBlockRegistry.PLASTIC_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.RUBBER.getBlockId()),
        ScrapBoxBlockRegistry.RUBBER_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.TECH.getBlockId()),
        ScrapBoxBlockRegistry.TECH_SCRAP_BOX_BLOCK);
    Registry.register(
        BuiltInRegistries.BLOCK,
        new ResourceLocation(Constants.MOD_ID, ScrapType.WOOD.getBlockId()),
        ScrapBoxBlockRegistry.WOOD_SCRAP_BOX_BLOCK);
  }
}
