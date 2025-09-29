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

package de.markusbordihn.scraptechworkshop;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.resources.ResourceLocation;

public final class Constants {

  public static final String MOD_ID = "scrap_tech_workshop";
  public static final String MOD_NAME = "Scrap Tech Workshop";
  public static final String MOD_COMMAND = MOD_ID;
  public static final String LOG_NAME = MOD_NAME;
  public static final String LOG_SUB_REGISTER_PREFIX = "- Register " + LOG_NAME;
  public static final String LOG_REGISTER_PREFIX = "Register " + MOD_NAME;

  public static final String COMPONENT_TYPE_PREFIX = "component_type." + MOD_ID + ".";
  public static final String GUI_PREFIX = "gui." + MOD_ID + ".";
  public static final String ITEM_PREFIX = "item." + MOD_ID + ".";
  public static final String SCRAP_CATEGORY_PREFIX = "scrap_category." + MOD_ID + ".";
  public static final String TEXT_PREFIX = "text." + MOD_ID + ".";
  public static final String TOOLTIP_PREFIX = "tooltip." + MOD_ID + ".";

  // Minecraft vanilla texture references
  public static final String MINECRAFT_PREFIX = "minecraft";
  public static final ResourceLocation TEXTURE_DEMO_BACKGROUND =
      new ResourceLocation(MINECRAFT_PREFIX, "textures/gui/demo_background.png");
  public static final ResourceLocation TEXTURE_INVENTORY =
      new ResourceLocation(MINECRAFT_PREFIX, "textures/gui/container/inventory.png");
  public static final ResourceLocation TEXTURE_FURNACE =
      new ResourceLocation(MINECRAFT_PREFIX, "textures/gui/container/furnace.png");

  public static Path GAME_DIR = Paths.get("").toAbsolutePath();
  public static Path CONFIG_DIR = GAME_DIR.resolve("config");

  public static boolean IS_FABRIC = false;
  public static boolean IS_FORGE = false;
  public static boolean IS_NEOFORGE = false;

  public static boolean HAS_FABRIC_TOOLTIPFIX_MOD = false;

  private Constants() {}
}
