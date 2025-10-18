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

  private static final Logger LOGGER = LogManager.getLogger(Constants.LOG_NAME);

  @SubscribeEvent
  public static void attachBlockEntityCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
    BlockEntity blockEntity = event.getObject();
    if (blockEntity instanceof EnergyPowerConsumer energyConsumer) {
      event.addCapability(
          ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "forge_energy"),
          new ForgeEnergyCapabilityProvider(energyConsumer));

      LOGGER.debug(
          "Attached Forge Energy capability to {}", blockEntity.getClass().getSimpleName());
    }
  }

  private static class ForgeEnergyCapabilityProvider implements ICapabilityProvider {

    private final LazyOptional<ForgeEnergyWrapper> forgeEnergyOptional;

    public ForgeEnergyCapabilityProvider(EnergyPowerConsumer energyConsumer) {
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
