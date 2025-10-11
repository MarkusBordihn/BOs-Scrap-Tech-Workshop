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

package de.markusbordihn.scraptechworkshop.registry.block;

import de.markusbordihn.scraptechworkshop.block.collectorstation.BasicScrapCollectorStationBlock;
import de.markusbordihn.scraptechworkshop.block.collectorstation.CollectorStationBlock;
import de.markusbordihn.scraptechworkshop.data.collectorstation.CollectorStationStatus;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class CollectorStationBlockRegistry {

  public static final BasicScrapCollectorStationBlock BASIC_SCRAP_COLLECTOR_STATION_BLOCK =
      new BasicScrapCollectorStationBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.METAL)
              .requiresCorrectToolForDrops()
              .strength(5.0F)
              .sound(SoundType.METAL)
              .noOcclusion()
              .lightLevel(
                  state -> {
                    CollectorStationStatus status = state.getValue(CollectorStationBlock.STATE);
                    return status == CollectorStationStatus.NO_POWER ? 0 : 7;
                  }));

  private CollectorStationBlockRegistry() {}
}
