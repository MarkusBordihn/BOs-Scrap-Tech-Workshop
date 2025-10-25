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

package de.markusbordihn.scraptechworkshop.client.screen;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.client.screen.rechargestation.RechargeStationScreen;
import de.markusbordihn.scraptechworkshop.client.screen.recycler.RecyclerScreen;
import de.markusbordihn.scraptechworkshop.client.screen.windturbine.ScrapWindTurbineScreen;
import de.markusbordihn.scraptechworkshop.menu.CollectorStationMenu;
import de.markusbordihn.scraptechworkshop.menu.FloatingScrapCollectorMenu;
import de.markusbordihn.scraptechworkshop.menu.RechargeStationMenu;
import de.markusbordihn.scraptechworkshop.menu.RecyclerMenu;
import de.markusbordihn.scraptechworkshop.menu.ScrapMultitoolMenu;
import de.markusbordihn.scraptechworkshop.menu.ScrapWindTurbineMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientScreens {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private ClientScreens() {
    // Utility class
  }

  public static void registerScreens() {
    log.info("{} Client Screens ...", Constants.LOG_REGISTER_PREFIX);
    MenuScreens.register(RecyclerMenu.TYPE, RecyclerScreen::new);
    MenuScreens.register(RechargeStationMenu.TYPE, RechargeStationScreen::new);
    MenuScreens.register(ScrapWindTurbineMenu.TYPE, ScrapWindTurbineScreen::new);
    MenuScreens.register(ScrapMultitoolMenu.TYPE, ScrapMultitoolScreen::new);
    MenuScreens.register(CollectorStationMenu.TYPE, CollectorStationScreen::new);
    MenuScreens.register(FloatingScrapCollectorMenu.TYPE, FloatingScrapCollectorScreen::new);
  }
}
