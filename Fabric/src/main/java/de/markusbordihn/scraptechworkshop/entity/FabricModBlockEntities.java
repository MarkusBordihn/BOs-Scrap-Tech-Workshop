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

package de.markusbordihn.scraptechworkshop.entity;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.entity.collectorstation.CollectorStationBlockEntity;
import de.markusbordihn.scraptechworkshop.block.entity.hololog.HoloCubeBlockEntity;
import de.markusbordihn.scraptechworkshop.block.entity.recycler.RecyclerBlockEntity;
import de.markusbordihn.scraptechworkshop.block.hololog.HoloCubeBlock;
import de.markusbordihn.scraptechworkshop.block.recycler.RecyclerBlock;
import de.markusbordihn.scraptechworkshop.registry.block.CollectorStationBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.RecyclerBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.entity.CollectorStationBlockEntityRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.entity.HoloCubeBlockEntityRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.entity.RecyclerBlockEntityRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.hololog.HoloLogBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FabricModBlockEntities {

  private FabricModBlockEntities() {}

  public static void registerBlockEntities() {
    // Register recycler block
    BlockEntityType<RecyclerBlockEntity> recyclerBlockEntityType =
        BlockEntityType.Builder.of(RecyclerBlockEntity::new, RecyclerBlockRegistry.RECYCLER_BLOCK)
            .build(null);
    Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        new ResourceLocation(Constants.MOD_ID, RecyclerBlock.ID),
        recyclerBlockEntityType);
    RecyclerBlockEntityRegistry.setBlockEntityType(recyclerBlockEntityType);

    // Register holocube block entity
    BlockEntityType<HoloCubeBlockEntity> holoCubeBlockEntityType =
        BlockEntityType.Builder.of(HoloCubeBlockEntity::new, HoloLogBlockRegistry.HOLO_CUBE_BLOCK)
            .build(null);
    Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        new ResourceLocation(Constants.MOD_ID, HoloCubeBlock.ID),
        holoCubeBlockEntityType);
    HoloCubeBlockEntityRegistry.setBlockEntityType(holoCubeBlockEntityType);

    // Register collector station block entity
    BlockEntityType<CollectorStationBlockEntity> collectorStationBlockEntityType =
        BlockEntityType.Builder.of(
                CollectorStationBlockEntity::new,
                CollectorStationBlockRegistry.BASIC_SCRAP_COLLECTOR_STATION_BLOCK)
            .build(null);
    Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        new ResourceLocation(Constants.MOD_ID, "collector_station"),
        collectorStationBlockEntityType);
    CollectorStationBlockEntityRegistry.setBlockEntityType(collectorStationBlockEntityType);
  }
}
