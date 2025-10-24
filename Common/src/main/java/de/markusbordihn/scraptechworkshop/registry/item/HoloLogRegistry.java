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

package de.markusbordihn.scraptechworkshop.registry.item;

import de.markusbordihn.scraptechworkshop.Constants;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoloLogRegistry {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final Map<String, ResourceLocation> HOLO_PAD_REGISTRY = new LinkedHashMap<>();
  private static final Map<String, ResourceLocation> HOLO_CUBE_REGISTRY = new LinkedHashMap<>();

  static {
    // Register HoloPads
    registerHoloPad(
        "introduction", new ResourceLocation(Constants.MOD_ID, "holologs/intro/introduction"));
    registerHoloPad(
        "scavenging_field_briefing_01",
        new ResourceLocation(Constants.MOD_ID, "holologs/tutorial/scavenging_field_briefing_01"));
    registerHoloPad(
        "scavenging_field_briefing_02",
        new ResourceLocation(Constants.MOD_ID, "holologs/tutorial/scavenging_field_briefing_02"));
    registerHoloPad(
        "engineering_briefing_01",
        new ResourceLocation(Constants.MOD_ID, "holologs/tutorial/engineering_briefing_01"));
    registerHoloPad(
        "engineering_briefing_02",
        new ResourceLocation(Constants.MOD_ID, "holologs/tutorial/engineering_briefing_02"));

    // Register HoloCubes
    registerHoloCube(
        "introduction", new ResourceLocation(Constants.MOD_ID, "holologs/intro/introduction"));
  }

  private HoloLogRegistry() {}

  public static void registerHoloPad(String itemId, ResourceLocation holoLogLocation) {
    HOLO_PAD_REGISTRY.put(itemId, holoLogLocation);
    log.debug("Registered HoloPad: holo_pad_{} -> {}", itemId, holoLogLocation);
  }

  public static void registerHoloCube(String itemId, ResourceLocation holoLogLocation) {
    HOLO_CUBE_REGISTRY.put(itemId, holoLogLocation);
    log.debug("Registered HoloCube: holo_cube_{} -> {}", itemId, holoLogLocation);
  }

  public static Map<String, ResourceLocation> getHoloPadRegistry() {
    return HOLO_PAD_REGISTRY;
  }

  public static Map<String, ResourceLocation> getHoloCubeRegistry() {
    return HOLO_CUBE_REGISTRY;
  }

  public static ResourceLocation getHoloPadHoloLog(String itemId) {
    return HOLO_PAD_REGISTRY.get(itemId);
  }

  public static ResourceLocation getHoloCubeHoloLog(String itemId) {
    return HOLO_CUBE_REGISTRY.get(itemId);
  }
}
