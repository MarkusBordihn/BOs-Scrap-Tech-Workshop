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
import de.markusbordihn.scraptechworkshop.block.entity.recycler.RecyclerBlockEntity;
import de.markusbordihn.scraptechworkshop.client.ClientEventHandler;
import de.markusbordihn.scraptechworkshop.client.model.robot.BaseRobotModel;
import de.markusbordihn.scraptechworkshop.client.model.robot.MixedScrapRobotModel;
import de.markusbordihn.scraptechworkshop.client.renderer.blockentity.HoloCubeBlockEntityRenderer;
import de.markusbordihn.scraptechworkshop.client.renderer.blockentity.RecyclerBlockEntityRenderer;
import de.markusbordihn.scraptechworkshop.client.renderer.blockentity.collectorstation.CollectorStationBlockEntityRenderer;
import de.markusbordihn.scraptechworkshop.client.renderer.entity.HoloLogHumanoidRenderer;
import de.markusbordihn.scraptechworkshop.client.renderer.entity.robot.CollectorStationRobotRenderer;
import de.markusbordihn.scraptechworkshop.client.renderer.entity.robot.MixedScrapRobotRenderer;
import de.markusbordihn.scraptechworkshop.client.screen.ClientScreens;
import de.markusbordihn.scraptechworkshop.registry.block.hololog.HoloLogBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.entity.CollectorStationRobotEntityRegistry;
import de.markusbordihn.scraptechworkshop.registry.entity.HoloLogEntityRegistry;
import de.markusbordihn.scraptechworkshop.registry.entity.MixedScrapRobotEntityRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScrapTechWorkshopClient implements ClientModInitializer {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  @Override
  public void onInitializeClient() {
    log.info("Initializing {} (Fabric-Client) ...", Constants.MOD_NAME);

    ClientScreens.registerScreens();

    // Register block entity renderers
    BlockEntityRenderers.register(RecyclerBlockEntity.TYPE, RecyclerBlockEntityRenderer::new);
    BlockEntityRenderers.register(HoloCubeBlockEntity.TYPE, HoloCubeBlockEntityRenderer::new);
    BlockEntityRenderers.register(
        CollectorStationBlockEntity.TYPE, CollectorStationBlockEntityRenderer::new);

    // Register entity renderers
    EntityRendererRegistry.register(
        HoloLogEntityRegistry.HOLO_LOG_HUMANOID_ENTITY_TYPE, HoloLogHumanoidRenderer::new);
    EntityRendererRegistry.register(
        CollectorStationRobotEntityRegistry.COLLECTOR_STATION_ROBOT_ENTITY_TYPE,
        CollectorStationRobotRenderer::new);
    EntityRendererRegistry.register(
        CollectorStationRobotEntityRegistry.COLLECTOR_STATION_ROBOT_STATIC_ENTITY_TYPE,
        CollectorStationRobotRenderer::new);
    EntityRendererRegistry.register(
        MixedScrapRobotEntityRegistry.MIXED_SCRAP_ROBOT_ENTITY_TYPE, MixedScrapRobotRenderer::new);

    // Register entity model layers
    EntityModelLayerRegistry.registerModelLayer(
        BaseRobotModel.LAYER_LOCATION, BaseRobotModel::createBodyLayer);
    EntityModelLayerRegistry.registerModelLayer(
        MixedScrapRobotModel.LAYER_LOCATION, BaseRobotModel::createBodyLayer);

    // Set render layers for transparent blocks
    BlockRenderLayerMap.INSTANCE.putBlock(
        HoloLogBlockRegistry.HOLO_CUBE_BLOCK, RenderType.translucent());

    // Register client event handlers
    ClientEventHandler.register();
  }
}
