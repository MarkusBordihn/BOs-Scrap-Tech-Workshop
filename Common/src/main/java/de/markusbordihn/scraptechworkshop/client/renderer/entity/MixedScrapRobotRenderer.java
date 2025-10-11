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

package de.markusbordihn.scraptechworkshop.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.client.model.MixedScrapRobotModel;
import de.markusbordihn.scraptechworkshop.entity.scraprobot.MixedScrapRobotEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MixedScrapRobotRenderer
    extends MobRenderer<MixedScrapRobotEntity, MixedScrapRobotModel> {

  // Texture location for the robot
  protected static final ResourceLocation TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/entity/collector_station_robot/default.png");

  public MixedScrapRobotRenderer(EntityRendererProvider.Context context) {
    super(
        context,
        new MixedScrapRobotModel(context.bakeLayer(MixedScrapRobotModel.LAYER_LOCATION)),
        0.4f);
  }

  @Override
  public ResourceLocation getTextureLocation(MixedScrapRobotEntity entity) {
    return TEXTURE;
  }

  @Override
  public void render(
      MixedScrapRobotEntity entity,
      float entityYaw,
      float partialTicks,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight) {
    poseStack.pushPose();
    poseStack.scale(0.8f, 0.8f, 0.8f);
    super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    poseStack.popPose();
  }
}
