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

package de.markusbordihn.scraptechworkshop.server;

import de.markusbordihn.scraptechworkshop.config.Config;
import de.markusbordihn.scraptechworkshop.saveddata.HoloCubeStorage;
import de.markusbordihn.scraptechworkshop.spawner.ScrapPileSpawner;
import de.markusbordihn.scraptechworkshop.tags.ScrapTypeBlockTags;
import net.minecraft.server.MinecraftServer;

public class ServerEvents {
  public static void handleServerStartedEvent(MinecraftServer minecraftServer) {
    // Initialize persistent storage
    HoloCubeStorage.init(minecraftServer.overworld());

    // Initialize cache
    ScrapTypeBlockTags.initializeCache();
  }

  public static void handleServerStartingEvent(MinecraftServer minecraftServer) {
    Config.registerDeferred(true);
  }

  public static void handleServerStartTickEvent(MinecraftServer minecraftServer) {
    ScrapPileSpawner.tick(minecraftServer);
  }

  public static void handleServerEndTickEvent(MinecraftServer minecraftServer) {}
}
