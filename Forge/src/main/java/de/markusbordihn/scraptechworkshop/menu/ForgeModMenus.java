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
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgeModMenus {

  public static final DeferredRegister<MenuType<?>> MENU_TYPES =
      DeferredRegister.create(ForgeRegistries.MENU_TYPES, Constants.MOD_ID);

  // Menu registrations
  public static final RegistryObject<MenuType<RecyclerMenu>> RECYCLER_MENU =
      MENU_TYPES.register("recycler", () -> IForgeMenuType.create(RecyclerMenu::new));

  public static final RegistryObject<MenuType<RechargeStationMenu>> RECHARGE_STATION_MENU =
      MENU_TYPES.register(
          "recharge_station", () -> IForgeMenuType.create(RechargeStationMenu::new));

  public static final RegistryObject<MenuType<ScrapWindTurbineMenu>> SCRAP_WIND_TURBINE_MENU =
      MENU_TYPES.register(
          "scrap_wind_turbine", () -> IForgeMenuType.create(ScrapWindTurbineMenu::new));

  public static final RegistryObject<MenuType<ScrapMultitoolMenu>> SCRAP_MULTITOOL_MENU =
      MENU_TYPES.register("scrap_multitool", () -> IForgeMenuType.create(ScrapMultitoolMenu::new));

  public static final RegistryObject<MenuType<CollectorStationMenu>> COLLECTOR_STATION_MENU =
      MENU_TYPES.register(
          "collector_station", () -> IForgeMenuType.create(CollectorStationMenu::new));

  public static final RegistryObject<MenuType<FloatingScrapCollectorMenu>>
      FLOATING_SCRAP_COLLECTOR_MENU =
          MENU_TYPES.register(
              "floating_scrap_collector",
              () -> IForgeMenuType.create(FloatingScrapCollectorMenu::new));

  private ForgeModMenus() {}

  public static void register(IEventBus eventBus) {
    MENU_TYPES.register(eventBus);

    // Set the menu type in the common class after registration
    eventBus.addListener(
        (net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) -> {
          RecyclerMenu.TYPE = RECYCLER_MENU.get();
          RechargeStationMenu.TYPE = RECHARGE_STATION_MENU.get();
          ScrapWindTurbineMenu.TYPE = SCRAP_WIND_TURBINE_MENU.get();
          ScrapMultitoolMenu.TYPE = SCRAP_MULTITOOL_MENU.get();
          CollectorStationMenu.TYPE = COLLECTOR_STATION_MENU.get();
          FloatingScrapCollectorMenu.TYPE = FLOATING_SCRAP_COLLECTOR_MENU.get();
        });
  }
}
