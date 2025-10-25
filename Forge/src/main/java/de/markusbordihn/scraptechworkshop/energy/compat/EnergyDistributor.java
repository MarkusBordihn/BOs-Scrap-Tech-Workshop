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

package de.markusbordihn.scraptechworkshop.energy.compat;

import de.markusbordihn.scraptechworkshop.energy.EnergyConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

@SuppressWarnings("unused")
public class EnergyDistributor {

  public static int distributeToAdjacentBlocks(
      Level level, BlockPos blockPos, int availableEnergyMah, int maxTransferRateMah) {

    int totalDistributedMah = 0;
    int remainingMah = Math.min(availableEnergyMah, maxTransferRateMah);

    for (Direction direction : Direction.values()) {
      if (remainingMah <= 0) {
        break;
      }

      BlockPos adjacentPos = blockPos.relative(direction);
      BlockEntity adjacentBE = level.getBlockEntity(adjacentPos);

      if (adjacentBE != null) {
        int finalRemainingMah = remainingMah;
        final int[] transferredInThisDirection = {0};

        adjacentBE
            .getCapability(ForgeCapabilities.ENERGY, direction.getOpposite())
            .ifPresent(
                energyStorage -> {
                  if (energyStorage.canReceive()) {
                    int forgeEnergyToTransfer =
                        EnergyConverter.milliampereHourToForgeEnergy(finalRemainingMah);
                    int transferred = energyStorage.receiveEnergy(forgeEnergyToTransfer, false);
                    if (transferred > 0) {
                      int transferredMah =
                          EnergyConverter.forgeEnergyToMilliampereHour(transferred);
                      transferredInThisDirection[0] = transferredMah;
                    }
                  }
                });

        totalDistributedMah += transferredInThisDirection[0];
        remainingMah -= transferredInThisDirection[0];
      }
    }

    return totalDistributedMah;
  }
}
