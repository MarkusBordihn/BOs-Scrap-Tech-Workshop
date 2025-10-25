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
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerConsumer;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerGenerator;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class EnergyCapabilityHandler {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  @SubscribeEvent
  public static void attachBlockEntityCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
    BlockEntity blockEntity = event.getObject();

    if (blockEntity instanceof EnergyPowerGenerator energyGenerator) {
      // Forge Energy capability
      event.addCapability(
          ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "forge_energy"),
          new ForgeEnergyGeneratorCapabilityProvider(energyGenerator));

      // Mekanism Energy capability (optional)
      if (MekanismEnergyGeneratorWrapper.initialize()) {
        event.addCapability(
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "mekanism_energy"),
            new MekanismEnergyGeneratorCapabilityProvider(energyGenerator));
        log.debug(
            "Attached Mekanism Energy Generator capability to {}",
            blockEntity.getClass().getSimpleName());
      }

      log.debug(
          "Attached Forge Energy Generator capability to {}",
          blockEntity.getClass().getSimpleName());
    } else if (blockEntity instanceof EnergyPowerConsumer energyConsumer) {
      event.addCapability(
          ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "forge_energy"),
          new ForgeEnergyConsumerCapabilityProvider(energyConsumer));

      log.debug(
          "Attached Forge Energy Consumer capability to {}",
          blockEntity.getClass().getSimpleName());
    }
  }

  private static class ForgeEnergyGeneratorCapabilityProvider implements ICapabilityProvider {

    private final LazyOptional<ForgeEnergyGeneratorWrapper> forgeEnergyOptional;

    public ForgeEnergyGeneratorCapabilityProvider(EnergyPowerGenerator energyGenerator) {
      this.forgeEnergyOptional =
          LazyOptional.of(() -> new ForgeEnergyGeneratorWrapper(energyGenerator));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(
        @NotNull Capability<T> capability, @Nullable Direction side) {

      if (capability == ForgeCapabilities.ENERGY) {
        return forgeEnergyOptional.cast();
      }

      return LazyOptional.empty();
    }
  }

  private static class MekanismEnergyGeneratorCapabilityProvider implements ICapabilityProvider {

    private final Object mekanismProxy;
    private final Capability<?> mekanismCapability;

    public MekanismEnergyGeneratorCapabilityProvider(EnergyPowerGenerator energyGenerator) {
      this.mekanismProxy = MekanismEnergyGeneratorWrapper.createProxy(energyGenerator);

      // Get Mekanism capability via reflection
      Capability<?> cap = null;
      try {
        Class<?> capabilitiesClass = Class.forName("mekanism.common.capabilities.Capabilities");
        java.lang.reflect.Field energyField = capabilitiesClass.getDeclaredField("STRICT_ENERGY");
        cap = (Capability<?>) energyField.get(null);
      } catch (Exception e) {
        log.warn("Failed to get Mekanism STRICT_ENERGY capability: {}", e.getMessage());
      }
      this.mekanismCapability = cap;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(
        @NotNull Capability<T> capability, @Nullable Direction side) {

      if (mekanismCapability != null && capability == mekanismCapability && mekanismProxy != null) {
        @SuppressWarnings("unchecked")
        T proxy = (T) mekanismProxy;
        return LazyOptional.of(() -> proxy);
      }

      return LazyOptional.empty();
    }
  }

  private static class ForgeEnergyConsumerCapabilityProvider implements ICapabilityProvider {

    private final LazyOptional<ForgeEnergyWrapper> forgeEnergyOptional;

    public ForgeEnergyConsumerCapabilityProvider(EnergyPowerConsumer energyConsumer) {
      this.forgeEnergyOptional = LazyOptional.of(() -> new ForgeEnergyWrapper(energyConsumer));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(
        @NotNull Capability<T> capability, @Nullable Direction side) {

      if (capability == ForgeCapabilities.ENERGY) {
        return forgeEnergyOptional.cast();
      }

      return LazyOptional.empty();
    }
  }
}
