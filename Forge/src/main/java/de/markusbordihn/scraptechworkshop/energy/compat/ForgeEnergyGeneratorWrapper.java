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
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerGenerator;
import net.minecraftforge.energy.IEnergyStorage;

public class ForgeEnergyGeneratorWrapper implements IEnergyStorage {

  private final EnergyPowerGenerator energyGenerator;

  public ForgeEnergyGeneratorWrapper(EnergyPowerGenerator energyGenerator) {
    this.energyGenerator = energyGenerator;
  }

  @Override
  public int receiveEnergy(int forgeEnergyReceive, boolean simulate) {
    return 0;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    if (!canExtract()) {
      return 0;
    }

    int currentMilliampereHour = energyGenerator.getCurrentEnergy();
    int milliampereHourExtracted =
        Math.min(EnergyConverter.forgeEnergyToMilliampereHour(maxExtract), currentMilliampereHour);

    if (!simulate && milliampereHourExtracted > 0) {
      energyGenerator.extractEnergy(milliampereHourExtracted, false);
      energyGenerator.markDirty();
    }

    return EnergyConverter.milliampereHourToForgeEnergy(milliampereHourExtracted);
  }

  @Override
  public int getEnergyStored() {
    return EnergyConverter.milliampereHourToForgeEnergy(energyGenerator.getCurrentEnergy());
  }

  @Override
  public int getMaxEnergyStored() {
    return EnergyConverter.milliampereHourToForgeEnergy(energyGenerator.getEnergyCapacity());
  }

  @Override
  public boolean canExtract() {
    return energyGenerator.getCurrentEnergy() > 0;
  }

  @Override
  public boolean canReceive() {
    return false;
  }
}
