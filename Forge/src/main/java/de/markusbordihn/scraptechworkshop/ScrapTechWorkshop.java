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

import de.markusbordihn.scraptechworkshop.block.ForgeModBlocks;
import de.markusbordihn.scraptechworkshop.config.Config;
import de.markusbordihn.scraptechworkshop.debug.DebugManager;
import de.markusbordihn.scraptechworkshop.entity.ForgeModBlockEntities;
import de.markusbordihn.scraptechworkshop.entity.ForgeModEntities;
import de.markusbordihn.scraptechworkshop.item.ForgeModBlockItems;
import de.markusbordihn.scraptechworkshop.item.ForgeModItems;
import de.markusbordihn.scraptechworkshop.loot.ModLootModifiers;
import de.markusbordihn.scraptechworkshop.menu.ForgeMenuOpener;
import de.markusbordihn.scraptechworkshop.menu.ForgeModMenus;
import de.markusbordihn.scraptechworkshop.menu.MenuManager;
import de.markusbordihn.scraptechworkshop.recipe.ForgeModRecipes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(Constants.MOD_ID)
public class ScrapTechWorkshop {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public ScrapTechWorkshop(FMLJavaModLoadingContext context) {
    initialize(context.getModEventBus());
  }

  public ScrapTechWorkshop() {
    initialize(FMLJavaModLoadingContext.get().getModEventBus());
  }

  private void initialize(IEventBus modEventBus) {

    log.info("Initializing {} (Forge) ...", Constants.MOD_NAME);

    log.info("{} Debug Manager ...", Constants.LOG_REGISTER_PREFIX);
    DebugManager.setDevelopmentEnvironment(!FMLEnvironment.production);
    DebugManager.checkForDebugLogging(Constants.LOG_NAME);

    log.info("{} Constants ...", Constants.LOG_REGISTER_PREFIX);
    Constants.GAME_DIR = FMLPaths.GAMEDIR.get();
    Constants.CONFIG_DIR = FMLPaths.CONFIGDIR.get();
    Constants.IS_FORGE = true;

    log.info("{} Configuration ...", Constants.LOG_REGISTER_PREFIX);
    Config.register(FMLEnvironment.dist == Dist.DEDICATED_SERVER);

    log.info("{} Menu Manager ...", Constants.LOG_REGISTER_PREFIX);
    MenuManager.setMenuOpener(new ForgeMenuOpener());

    log.info("{} Blocks ...", Constants.LOG_REGISTER_PREFIX);
    ForgeModBlocks.register(modEventBus);

    log.info("{} Block Entities ...", Constants.LOG_REGISTER_PREFIX);
    ForgeModBlockEntities.register(modEventBus);

    log.info("{} Entities ...", Constants.LOG_REGISTER_PREFIX);
    ForgeModEntities.register(modEventBus);

    log.info("{} Block Items ...", Constants.LOG_REGISTER_PREFIX);
    ForgeModBlockItems.register(modEventBus);

    log.info("{} Items ...", Constants.LOG_REGISTER_PREFIX);
    ForgeModItems.register(modEventBus);

    log.info("{} Menus ...", Constants.LOG_REGISTER_PREFIX);
    ForgeModMenus.register(modEventBus);

    log.info("{} Recipes ...", Constants.LOG_REGISTER_PREFIX);
    ForgeModRecipes.register(modEventBus);

    log.info("{} Loot Modifiers ...", Constants.LOG_REGISTER_PREFIX);
    ModLootModifiers.register(modEventBus);

    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new ScrapTechWorkshopClient(modEventBus));
  }
}
