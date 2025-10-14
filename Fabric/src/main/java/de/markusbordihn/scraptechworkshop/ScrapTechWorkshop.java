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

import de.markusbordihn.scraptechworkshop.block.BlockEventHandler;
import de.markusbordihn.scraptechworkshop.block.FabricModBlocks;
import de.markusbordihn.scraptechworkshop.config.Config;
import de.markusbordihn.scraptechworkshop.debug.DebugManager;
import de.markusbordihn.scraptechworkshop.entity.FabricModBlockEntities;
import de.markusbordihn.scraptechworkshop.entity.FabricModEntities;
import de.markusbordihn.scraptechworkshop.item.FabricModBlockItems;
import de.markusbordihn.scraptechworkshop.item.FabricModItems;
import de.markusbordihn.scraptechworkshop.loot.VanillaChestLootModifier;
import de.markusbordihn.scraptechworkshop.menu.FabricMenuOpener;
import de.markusbordihn.scraptechworkshop.menu.FabricModMenus;
import de.markusbordihn.scraptechworkshop.menu.MenuManager;
import de.markusbordihn.scraptechworkshop.player.FabricPlayerEventHandler;
import de.markusbordihn.scraptechworkshop.recipe.FabricModRecipes;
import de.markusbordihn.scraptechworkshop.server.ServerEventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScrapTechWorkshop implements ModInitializer {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  @Override
  public void onInitialize() {

    log.info("Initializing {} (Fabric) ...", Constants.MOD_NAME);

    log.info("{} Debug Manager ...", Constants.LOG_REGISTER_PREFIX);
    DebugManager.setDevelopmentEnvironment(System.getProperty("fabric.development") != null);
    DebugManager.checkForDebugLogging(Constants.LOG_NAME);

    log.info("{} Constants ...", Constants.LOG_REGISTER_PREFIX);
    Constants.GAME_DIR = FabricLoader.getInstance().getGameDir();
    Constants.CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    Constants.IS_FABRIC = true;
    Constants.HAS_FABRIC_TOOLTIPFIX_MOD = FabricLoader.getInstance().isModLoaded("tooltipfix");

    log.info("{} Configuration ...", Constants.LOG_REGISTER_PREFIX);
    Config.register(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER);

    log.info("{} Menu Manager ...", Constants.LOG_REGISTER_PREFIX);
    MenuManager.setMenuOpener(new FabricMenuOpener());

    log.info("{} Blocks ...", Constants.LOG_REGISTER_PREFIX);
    FabricModBlocks.registerBlocks();

    log.info("{} Block Entities ...", Constants.LOG_REGISTER_PREFIX);
    FabricModBlockEntities.registerBlockEntities();

    log.info("{} Entities ...", Constants.LOG_REGISTER_PREFIX);
    FabricModEntities.registerEntities();

    log.info("{} Block Items ...", Constants.LOG_REGISTER_PREFIX);
    FabricModBlockItems.registerBlockItems();

    log.info("{} Items ...", Constants.LOG_REGISTER_PREFIX);
    FabricModItems.registerModItems();

    log.info("{} Menus ...", Constants.LOG_REGISTER_PREFIX);
    FabricModMenus.registerMenus();

    log.info("{} Recipes ...", Constants.LOG_REGISTER_PREFIX);
    FabricModRecipes.registerRecipes();

    log.info("{} Creative Mode Tabs ...", Constants.LOG_REGISTER_PREFIX);
    FabricModItems.registerCreativeModeTabs();

    log.info("{} Block Events ...", Constants.LOG_REGISTER_PREFIX);
    BlockEventHandler.register();

    log.info("{} Player Events ...", Constants.LOG_REGISTER_PREFIX);
    FabricPlayerEventHandler.register();

    log.info("{} Loot Table Modifiers ...", Constants.LOG_REGISTER_PREFIX);
    VanillaChestLootModifier.register();

    log.info("{} Server Event Handler ...", Constants.LOG_REGISTER_PREFIX);
    ServerEventHandler.registerServerEvents();

    log.info("{} Entity Spawns ...", Constants.LOG_REGISTER_PREFIX);
    de.markusbordihn.scraptechworkshop.entity.FabricEntitySpawnHandler.registerSpawns();
  }
}
