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

package de.markusbordihn.scraptechworkshop.client;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.client.renderer.hololog.HoloLogPlayerManager;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogManager;
import de.markusbordihn.scraptechworkshop.item.ItemPropertyFunctions;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloPadItem;
import de.markusbordihn.scraptechworkshop.registry.item.hololog.HoloLogItemRegistry;
import de.markusbordihn.scraptechworkshop.utils.ColorUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {

  @SubscribeEvent
  public static void onClientTick(TickEvent.ClientTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      HoloLogPlayerManager.tickAll();
    }
  }

  @SubscribeEvent
  public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
    ClientEvents.handleClientDisconnect();
  }
}

@Mod.EventBusSubscriber(
    modid = Constants.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT)
class ClientModEventHandler {
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(
        () -> {
          ItemProperties.register(
              ModItems.SCRAP_FISHING_ROD.get(),
              ResourceLocation.fromNamespaceAndPath("minecraft", "cast"),
              ItemPropertyFunctions::getFishingRodCastValue);

          ItemProperties.register(
              ModItems.MAGNET_FISHING_ROD.get(),
              ResourceLocation.fromNamespaceAndPath("minecraft", "cast"),
              ItemPropertyFunctions::getFishingRodCastValue);
        });
  }

  @SubscribeEvent
  public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
    event.registerReloadListener(
        (preparationBarrier,
            resourceManager,
            profilerFiller,
            profilerFiller2,
            executor,
            executor2) -> {
          return preparationBarrier
              .wait(new Object())
              .thenRunAsync(
                  () -> {
                    log.info("Reloading holologs after resource pack change...");
                    HoloLogManager.clearCache();
                  },
                  executor2);
        });
  }

  @SubscribeEvent
  public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
    log.info("{} Item Colors for HoloPads ...", Constants.LOG_REGISTER_PREFIX);
    
    // Register color handler for all HoloPad items
    for (HoloPadItem item : HoloLogItemRegistry.getHoloPadItems().values()) {
      event.register((stack, tintIndex) -> {
        if (tintIndex == 0 && stack.getItem() instanceof HoloPadItem holoPad) {
          return HoloLogManager.loadHoloLog(holoPad.getHoloLogId())
              .map(data -> ColorUtils.parseHexColor(data.color()))
              .orElse(0x00FFFF);
        }
        return 0xFFFFFF;
      }, item);
    }
  }
}
