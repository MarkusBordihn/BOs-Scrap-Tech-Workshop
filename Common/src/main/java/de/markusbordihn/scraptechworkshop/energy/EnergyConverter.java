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

  public static final float BASE_VOLTAGE = 3.2f;
  public static final float MILLIAMPERE_HOUR_TO_WATT_HOUR = BASE_VOLTAGE / 1000f;
  public static final float WATT_HOUR_TO_FORGE_ENERGY = 1000f;
  public static final float FORGE_ENERGY_TO_MEKANISM_JOULES = 2.5f;
  public static final float MEKANISM_JOULES_TO_FORGE_ENERGY = 0.4f;
  public static final float MILLIAMPERE_HOUR_TO_FORGE_ENERGY =
    MILLIAMPERE_HOUR_TO_WATT_HOUR * WATT_HOUR_TO_FORGE_ENERGY;
  public static final float MILLIAMPERE_HOUR_TO_MEKANISM_JOULES =
    MILLIAMPERE_HOUR_TO_FORGE_ENERGY * FORGE_ENERGY_TO_MEKANISM_JOULES;
  public static final float MEKANISM_JOULES_TO_MILLIAMPERE_HOUR =
    1.0f / MILLIAMPERE_HOUR_TO_MEKANISM_JOULES;

  private EnergyConverter() {}

  public static int milliampereHourToForgeEnergy(int milliampereHour) {
    return Math.round(milliampereHour * MILLIAMPERE_HOUR_TO_FORGE_ENERGY);
  }

  public static int forgeEnergyToMilliampereHour(int forgeEnergy) {
    return Math.round(forgeEnergy / MILLIAMPERE_HOUR_TO_FORGE_ENERGY);
  }

  public static long milliampereHourToMekanismJoules(int milliampereHour) {
    return Math.round(milliampereHour * MILLIAMPERE_HOUR_TO_MEKANISM_JOULES);
  }

  public static int mekanismJoulesToMilliampereHour(long joules) {
    return Math.round(joules * MEKANISM_JOULES_TO_MILLIAMPERE_HOUR);
  }

  public static long forgeEnergyToMekanismJoules(int forgeEnergy) {
    return Math.round(forgeEnergy * FORGE_ENERGY_TO_MEKANISM_JOULES);
  }

  public static int mekanismJoulesToForgeEnergy(long joules) {
    return Math.round(joules * MEKANISM_JOULES_TO_FORGE_ENERGY);
  }
}
