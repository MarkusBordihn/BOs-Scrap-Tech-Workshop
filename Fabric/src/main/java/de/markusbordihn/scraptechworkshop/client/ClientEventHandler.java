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
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientEventHandler {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private ClientEventHandler() {}

  public static void register() {
    registerItemProperties();
    registerItemColors();

    ClientTickEvents.END_CLIENT_TICK.register(
        client -> {
          HoloLogPlayerManager.tickAll();
        });

    ClientPlayConnectionEvents.DISCONNECT.register(
        (handler, client) -> {
          ClientEvents.handleClientDisconnect();
        });

    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
        .registerReloadListener(
            new SimpleSynchronousResourceReloadListener() {
              @Override
              public ResourceLocation getFabricId() {
                return new ResourceLocation(Constants.MOD_ID, "hololog_reload");
              }

              @Override
              public void onResourceManagerReload(ResourceManager resourceManager) {
                log.info("Reloading holologs after resource pack change...");
                HoloLogManager.clearCache();
              }
            });
  }

  private static void registerItemColors() {
    log.info("{} Item Colors for HoloPads ...", Constants.LOG_REGISTER_PREFIX);

    // Register color provider for all HoloPad items
    for (HoloPadItem item : HoloLogItemRegistry.getHoloPadItems().values()) {
      ColorProviderRegistry.ITEM.register(
          (stack, tintIndex) -> {
            if (tintIndex == 0 && stack.getItem() instanceof HoloPadItem holoPad) {
              return HoloLogManager.loadHoloLog(holoPad.getHoloLogId())
                  .map(data -> ColorUtils.parseHexColor(data.color()))
                  .orElse(0x00FFFF);
            }
            return 0xFFFFFF;
          },
          item);
    }
  }

  private static void registerItemProperties() {
    ItemProperties.register(
        ModItems.SCRAP_FISHING_ROD.get(),
        new ResourceLocation("minecraft", "cast"),
        ItemPropertyFunctions::getFishingRodCastValue);

    ItemProperties.register(
        ModItems.MAGNET_FISHING_ROD.get(),
        new ResourceLocation("minecraft", "cast"),
        ItemPropertyFunctions::getFishingRodCastValue);
  }
}
