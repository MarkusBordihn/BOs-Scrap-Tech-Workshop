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
import de.markusbordihn.scraptechworkshop.block.collectorstation.BasicScrapCollectorStationBlock;
import de.markusbordihn.scraptechworkshop.block.deco.NeonTubeBlock;
import de.markusbordihn.scraptechworkshop.block.deco.ReplicantTestLampBlock;
import de.markusbordihn.scraptechworkshop.block.floatingscrapcollector.FloatingScrapCollectorBlock;
import de.markusbordihn.scraptechworkshop.block.rechargestation.RechargeStationBlock;
import de.markusbordihn.scraptechworkshop.block.recycler.RecyclerBlock;
import de.markusbordihn.scraptechworkshop.block.windturbine.ScrapWindTurbineBlock;
import de.markusbordihn.scraptechworkshop.data.ScrapPileVariant;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloCubeItem;
import de.markusbordihn.scraptechworkshop.registry.item.HoloLogItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.HoloLogRegistry;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
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
          () ->
              new DescriptiveBlockItem(ForgeModBlocks.RECYCLER_BLOCK.get(), new Item.Properties()));

  public static final RegistryObject<BlockItem> RECHARGE_STATION_BLOCK_ITEM =
      BLOCK_ITEMS.register(
          RechargeStationBlock.ID,
          () ->
              new DescriptiveBlockItem(
                  ForgeModBlocks.RECHARGE_STATION_BLOCK.get(), new Item.Properties()));

  public static final RegistryObject<BlockItem> SCRAP_WIND_TURBINE_BLOCK_ITEM =
      BLOCK_ITEMS.register(
          ScrapWindTurbineBlock.ID,
          () ->
              new DescriptiveBlockItem(
                  ForgeModBlocks.SCRAP_WIND_TURBINE_BLOCK.get(), new Item.Properties()));

  public static final RegistryObject<BlockItem> BASIC_SCRAP_COLLECTOR_STATION_BLOCK_ITEM =
      BLOCK_ITEMS.register(
          BasicScrapCollectorStationBlock.ID,
          () ->
              new DescriptiveBlockItem(
                  ForgeModBlocks.BASIC_SCRAP_COLLECTOR_STATION_BLOCK.get(), new Item.Properties()));

  public static final RegistryObject<BlockItem> FLOATING_SCRAP_COLLECTOR_BLOCK_ITEM =
      BLOCK_ITEMS.register(
          FloatingScrapCollectorBlock.ID,
          () ->
              new DescriptiveBlockItem(
                  ForgeModBlocks.FLOATING_SCRAP_COLLECTOR_BLOCK.get(), new Item.Properties()));

  public static final RegistryObject<BlockItem> NEON_TUBE_BLOCK_ITEM =
      BLOCK_ITEMS.register(
          NeonTubeBlock.ID,
          () -> new BlockItem(ForgeModBlocks.NEON_TUBE_BLOCK.get(), new Item.Properties()));

  public static final RegistryObject<BlockItem> REPLICANT_TEST_LAMP_BLOCK_ITEM =
      BLOCK_ITEMS.register(
          ReplicantTestLampBlock.ID,
          () ->
              new DescriptiveBlockItem(
                  ForgeModBlocks.REPLICANT_TEST_LAMP_BLOCK.get(), new Item.Properties()));

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

  private static final Map<String, RegistryObject<BlockItem>> HOLO_CUBE_REGISTRY_OBJECTS =
      new LinkedHashMap<>();

  static {
    for (Map.Entry<String, ResourceLocation> entry :
        HoloLogRegistry.getHoloCubeRegistry().entrySet()) {
      String itemId = entry.getKey();
      ResourceLocation holoLogId = entry.getValue();
      RegistryObject<BlockItem> registryObject =
          BLOCK_ITEMS.register(
              HoloCubeItem.ID_PREFIX + itemId,
              HoloLogItemRegistry.createHoloCubeSupplier(itemId, holoLogId));
      HOLO_CUBE_REGISTRY_OBJECTS.put(itemId, registryObject);
    }
  }

  private ForgeModBlockItems() {}

  public static void register(IEventBus eventBus) {
    BLOCK_ITEMS.register(eventBus);

    ModBlockItems.RECYCLER = RECYCLER_BLOCK_ITEM;
    ModBlockItems.RECHARGE_STATION = RECHARGE_STATION_BLOCK_ITEM;
    ModBlockItems.SCRAP_WIND_TURBINE = SCRAP_WIND_TURBINE_BLOCK_ITEM;
    ModBlockItems.BASIC_SCRAP_COLLECTOR_STATION = BASIC_SCRAP_COLLECTOR_STATION_BLOCK_ITEM;
    ModBlockItems.FLOATING_SCRAP_COLLECTOR = FLOATING_SCRAP_COLLECTOR_BLOCK_ITEM;
    ModBlockItems.NEON_TUBE = NEON_TUBE_BLOCK_ITEM;
    ModBlockItems.REPLICANT_TEST_LAMP = REPLICANT_TEST_LAMP_BLOCK_ITEM;
    ModBlockItems.MIXED_SCRAP_PILE = MIXED_SCRAP_PILE_BLOCK_ITEM;
    ModBlockItems.METAL_SCRAP_PILE = METAL_SCRAP_PILE_BLOCK_ITEM;
    ModBlockItems.TECH_SCRAP_PILE = TECH_SCRAP_PILE_BLOCK_ITEM;

    if (!HOLO_CUBE_REGISTRY_OBJECTS.isEmpty()) {
      ModBlockItems.HOLO_CUBE = HOLO_CUBE_REGISTRY_OBJECTS.values().iterator().next();
    }
  }
}
