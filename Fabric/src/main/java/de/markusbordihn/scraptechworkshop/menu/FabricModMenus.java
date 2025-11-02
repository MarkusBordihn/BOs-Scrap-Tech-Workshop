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

package de.markusbordihn.scraptechworkshop.menu;

import de.markusbordihn.scraptechworkshop.Constants;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class FabricModMenus {

  private FabricModMenus() {}

  public static void registerMenus() {
    // Register recycler menu and set the TYPE in the common class
    ExtendedScreenHandlerType<RecyclerMenu> recyclerMenuType =
        new ExtendedScreenHandlerType<>(RecyclerMenu::new);
    Registry.register(
        BuiltInRegistries.MENU,
        new ResourceLocation(Constants.MOD_ID, "recycler"),
        recyclerMenuType);

    // Register recharge station menu
    ExtendedScreenHandlerType<RechargeStationMenu> rechargeStationMenuType =
        new ExtendedScreenHandlerType<>(RechargeStationMenu::new);
    Registry.register(
        BuiltInRegistries.MENU,
        new ResourceLocation(Constants.MOD_ID, "recharge_station"),
        rechargeStationMenuType);

    // Register scrap wind turbine menu
    ExtendedScreenHandlerType<ScrapWindTurbineMenu> scrapWindTurbineMenuType =
        new ExtendedScreenHandlerType<>(ScrapWindTurbineMenu::new);
    Registry.register(
        BuiltInRegistries.MENU,
        new ResourceLocation(Constants.MOD_ID, "scrap_wind_turbine"),
        scrapWindTurbineMenuType);

    // Register scrap multitool menu
    ExtendedScreenHandlerType<ScrapMultitoolMenu> scrapMultitoolMenuType =
        new ExtendedScreenHandlerType<>(ScrapMultitoolMenu::new);
    Registry.register(
        BuiltInRegistries.MENU,
        new ResourceLocation(Constants.MOD_ID, "scrap_multitool"),
        scrapMultitoolMenuType);

    // Register collector station menu
    ExtendedScreenHandlerType<CollectorStationMenu> collectorStationMenuType =
        new ExtendedScreenHandlerType<>(CollectorStationMenu::new);
    Registry.register(
        BuiltInRegistries.MENU,
        new ResourceLocation(Constants.MOD_ID, "collector_station"),
        collectorStationMenuType);

    // Register floating scrap collector menu
    ExtendedScreenHandlerType<FloatingScrapCollectorMenu> floatingScrapCollectorMenuType =
        new ExtendedScreenHandlerType<>(FloatingScrapCollectorMenu::new);
    Registry.register(
        BuiltInRegistries.MENU,
        new ResourceLocation(Constants.MOD_ID, "floating_scrap_collector"),
        floatingScrapCollectorMenuType);

    // Set the menu types in the common class
    CollectorStationMenu.MENU_TYPE = collectorStationMenuType;
    FloatingScrapCollectorMenu.MENU_TYPE = floatingScrapCollectorMenuType;
    RechargeStationMenu.MENU_TYPE = rechargeStationMenuType;
    RecyclerMenu.MENU_TYPE = recyclerMenuType;
    ScrapMultitoolMenu.MENU_TYPE = scrapMultitoolMenuType;
    ScrapWindTurbineMenu.MENU_TYPE = scrapWindTurbineMenuType;
  }
}
