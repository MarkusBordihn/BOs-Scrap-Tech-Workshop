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

import de.markusbordihn.scraptechworkshop.block.deco.NeonTubeBlock;
import de.markusbordihn.scraptechworkshop.block.deco.ReplicantTestLampBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class DecoBlockRegistry {

  public static final NeonTubeBlock NEON_TUBE_BLOCK =
      new NeonTubeBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.COLOR_LIGHT_BLUE)
              .strength(0.3F)
              .sound(SoundType.GLASS)
              .lightLevel(state -> state.getValue(NeonTubeBlock.LIT) ? 15 : 0)
              .noOcclusion()
              .dynamicShape());

  public static final ReplicantTestLampBlock REPLICANT_TEST_LAMP_BLOCK =
      new ReplicantTestLampBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.METAL)
              .strength(0.5F)
              .sound(SoundType.METAL)
              .lightLevel(
                  state ->
                      state.getValue(ReplicantTestLampBlock.LIT)
                          ? (state.getValue(ReplicantTestLampBlock.MODE) == 1 ? 10 : 12)
                          : 0)
              .noOcclusion());

  private DecoBlockRegistry() {}
}
