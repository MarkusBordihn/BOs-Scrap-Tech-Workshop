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

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.energy.EnergyConverter;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerGenerator;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MekanismEnergyGeneratorWrapper implements InvocationHandler {

  private static final Logger LOGGER = LogManager.getLogger(Constants.LOG_NAME);
  private static final String MEKANISM_ENERGY_PACKAGE = "mekanism.api.energy";
  private static final String STRICT_ENERGY_HANDLER_CLASS =
      MEKANISM_ENERGY_PACKAGE + ".IStrictEnergyHandler";

  private static Class<?> strictEnergyHandlerClass;
  private static Class<?> floatingLongClass;
  private static Method createFloatingLongMethod;
  private static Method floatingLongValueMethod;
  private static boolean initialized = false;

  private final EnergyPowerGenerator energyGenerator;

  public MekanismEnergyGeneratorWrapper(EnergyPowerGenerator energyGenerator) {
    this.energyGenerator = energyGenerator;
  }

  public static boolean initialize() {
    if (initialized) {
      return strictEnergyHandlerClass != null;
    }

    initialized = true;

    if (!MekanismEnergyCompat.isMekanismLoaded()) {
      return false;
    }

    try {
      strictEnergyHandlerClass = Class.forName(STRICT_ENERGY_HANDLER_CLASS);
      floatingLongClass = Class.forName(MEKANISM_ENERGY_PACKAGE + ".FloatingLong");
      createFloatingLongMethod = floatingLongClass.getMethod("create", long.class);
      floatingLongValueMethod = floatingLongClass.getMethod("longValue");

      LOGGER.info("Mekanism Energy API successfully loaded via reflection");
      return true;

    } catch (ClassNotFoundException | NoSuchMethodException e) {
      LOGGER.warn("Failed to load Mekanism Energy API via reflection: {}", e.getMessage());
      strictEnergyHandlerClass = null;
      return false;
    }
  }

  public static Object createProxy(EnergyPowerGenerator generator) {
    if (!initialize()) {
      return null;
    }

    MekanismEnergyGeneratorWrapper handler = new MekanismEnergyGeneratorWrapper(generator);
    return Proxy.newProxyInstance(
        strictEnergyHandlerClass.getClassLoader(),
        new Class<?>[] {strictEnergyHandlerClass},
        handler);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    String methodName = method.getName();

    try {
      switch (methodName) {
        case "insertEnergy":
          return createFloatingLong(0L);

        case "extractEnergy":
          if (args != null && args.length >= 1) {
            long requestedJoules = getFloatingLongValue(args[0]);
            long extractedJoules = extractEnergyInJoules(requestedJoules);
            return createFloatingLong(extractedJoules);
          }
          return createFloatingLong(0L);

        case "getEnergy":
          long currentJoules =
              EnergyConverter.milliampereHourToMekanismJoules(energyGenerator.getCurrentEnergy());
          return createFloatingLong(currentJoules);

        case "getMaxEnergy":
          long maxJoules =
              EnergyConverter.milliampereHourToMekanismJoules(energyGenerator.getEnergyCapacity());
          return createFloatingLong(maxJoules);

        case "setEnergy":
          return null;

        case "getEnergyContainers":
          return java.util.Collections.emptyList();

        case "getSideFor":
          return null;

        default:
          LOGGER.debug("Unhandled Mekanism method call: {}", methodName);
          return null;
      }
    } catch (Exception e) {
      LOGGER.error("Error handling Mekanism method {}: {}", methodName, e.getMessage());
      return null;
    }
  }

  private long extractEnergyInJoules(long requestedJoules) {
    int currentMah = energyGenerator.getCurrentEnergy();
    if (currentMah <= 0) {
      return 0L;
    }

    // Convert requested Joules to mAh
    int requestedMah = EnergyConverter.mekanismJoulesToMilliampereHour(requestedJoules);
    int extractedMah = Math.min(requestedMah, currentMah);
    if (extractedMah > 0) {
      energyGenerator.extractEnergy(extractedMah, false);
      energyGenerator.markDirty();
    }

    // Convert extracted mAh back to Joules
    return EnergyConverter.milliampereHourToMekanismJoules(extractedMah);
  }

  private Object createFloatingLong(long value) throws Exception {
    return createFloatingLongMethod.invoke(null, value);
  }

  private long getFloatingLongValue(Object floatingLong) throws Exception {
    return (long) floatingLongValueMethod.invoke(floatingLong);
  }
}
