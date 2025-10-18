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

package de.markusbordihn.scraptechworkshop.energy;

public final class EnergyConverter {

  public static final double MILLIAMPERE_HOUR_TO_FORGE_ENERGY = 1.0;
  public static final double FORGE_ENERGY_TO_MILLIAMPERE_HOUR = 1.0;

  public static final double FORGE_ENERGY_TO_MEKANISM_JOULES = 0.4;
  public static final double MEKANISM_JOULES_TO_FORGE_ENERGY = 2.5;

  public static final double MILLIAMPERE_HOUR_TO_MEKANISM_JOULES =
      MILLIAMPERE_HOUR_TO_FORGE_ENERGY * FORGE_ENERGY_TO_MEKANISM_JOULES;
  public static final double MEKANISM_JOULES_TO_MILLIAMPERE_HOUR =
      MEKANISM_JOULES_TO_FORGE_ENERGY * FORGE_ENERGY_TO_MILLIAMPERE_HOUR;

  private EnergyConverter() {}

  public static int milliampereHourToForgeEnergy(int milliampereHour) {
    return (int) (milliampereHour * MILLIAMPERE_HOUR_TO_FORGE_ENERGY);
  }

  public static int forgeEnergyToMilliampereHour(int forgeEnergy) {
    return (int) (forgeEnergy * FORGE_ENERGY_TO_MILLIAMPERE_HOUR);
  }

  public static long milliampereHourToMekanismJoules(int milliampereHour) {
    return (long) (milliampereHour * MILLIAMPERE_HOUR_TO_MEKANISM_JOULES);
  }

  public static int mekanismJoulesToMilliampereHour(long joules) {
    return (int) (joules * MEKANISM_JOULES_TO_MILLIAMPERE_HOUR);
  }

  public static long forgeEnergyToMekanismJoules(int forgeEnergy) {
    return (long) (forgeEnergy * FORGE_ENERGY_TO_MEKANISM_JOULES);
  }

  public static int mekanismJoulesToForgeEnergy(long joules) {
    return (int) (joules * MEKANISM_JOULES_TO_FORGE_ENERGY);
  }
}
