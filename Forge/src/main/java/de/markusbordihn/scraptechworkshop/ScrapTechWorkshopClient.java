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

import de.markusbordihn.scraptechworkshop.block.entity.collectorstation.CollectorStationBlockEntity;
import de.markusbordihn.scraptechworkshop.block.entity.hololog.HoloCubeBlockEntity;
import de.markusbordihn.scraptechworkshop.block.entity.rechargestation.RechargeStationBlockEntity;
import de.markusbordihn.scraptechworkshop.block.entity.recycler.RecyclerBlockEntity;
import de.markusbordihn.scraptechworkshop.client.model.robot.BaseRobotModel;
import de.markusbordihn.scraptechworkshop.client.model.robot.MixedScrapRobotModel;
import de.markusbordihn.scraptechworkshop.client.renderer.blockentity.HoloCubeBlockEntityRenderer;
import de.markusbordihn.scraptechworkshop.client.renderer.blockentity.RecyclerBlockEntityRenderer;
import de.markusbordihn.scraptechworkshop.client.renderer.blockentity.collectorstation.CollectorStationBlockEntityRenderer;
import de.markusbordihn.scraptechworkshop.client.renderer.blockentity.rechargestation.RechargeStationBlockEntityRenderer;
import de.markusbordihn.scraptechworkshop.client.renderer.entity.HoloLogHumanoidRenderer;
import de.markusbordihn.scraptechworkshop.client.renderer.entity.robot.CollectorStationRobotRenderer;
import de.markusbordihn.scraptechworkshop.client.renderer.entity.robot.MixedScrapRobotRenderer;
import de.markusbordihn.scraptechworkshop.client.screen.ClientScreens;
import de.markusbordihn.scraptechworkshop.item.ForgeModItems;
import de.markusbordihn.scraptechworkshop.registry.entity.CollectorStationRobotEntityRegistry;
import de.markusbordihn.scraptechworkshop.registry.entity.HoloLogEntityRegistry;
import de.markusbordihn.scraptechworkshop.registry.entity.MixedScrapRobotEntityRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ScrapTechWorkshopClient {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public ScrapTechWorkshopClient(final IEventBus modEventBus) {
    log.info("Initializing {} (Forge-Client) ...", Constants.MOD_NAME);

    ForgeModItems.CREATIVE_MODE_TABS.register(modEventBus);
  }

  @SubscribeEvent
  public static void onClientSetup(final FMLClientSetupEvent event) {
    event.enqueueWork(ClientScreens::registerScreens);
  }

  @SubscribeEvent
  public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(RecyclerBlockEntity.TYPE, RecyclerBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(
        RechargeStationBlockEntity.TYPE, RechargeStationBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(HoloCubeBlockEntity.TYPE, HoloCubeBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(
        CollectorStationBlockEntity.TYPE, CollectorStationBlockEntityRenderer::new);
    event.registerEntityRenderer(
        HoloLogEntityRegistry.HOLO_LOG_HUMANOID_ENTITY_TYPE, HoloLogHumanoidRenderer::new);
    event.registerEntityRenderer(
        CollectorStationRobotEntityRegistry.COLLECTOR_STATION_ROBOT_ENTITY_TYPE,
        CollectorStationRobotRenderer::new);
    event.registerEntityRenderer(
        CollectorStationRobotEntityRegistry.COLLECTOR_STATION_ROBOT_STATIC_ENTITY_TYPE,
        CollectorStationRobotRenderer::new);
    event.registerEntityRenderer(
        MixedScrapRobotEntityRegistry.MIXED_SCRAP_ROBOT_ENTITY_TYPE, MixedScrapRobotRenderer::new);
  }

  @SubscribeEvent
  public static void onRegisterLayerDefinitions(
      EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(BaseRobotModel.LAYER_LOCATION, BaseRobotModel::createBodyLayer);
    event.registerLayerDefinition(
        MixedScrapRobotModel.LAYER_LOCATION, BaseRobotModel::createBodyLayer);
  }
}
