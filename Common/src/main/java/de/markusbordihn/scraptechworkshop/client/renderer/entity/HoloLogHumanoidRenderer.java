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
import de.markusbordihn.scraptechworkshop.entity.hololog.HoloLogHumanoidEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HoloLogHumanoidRenderer
    extends MobRenderer<HoloLogHumanoidEntity, PlayerModel<HoloLogHumanoidEntity>> {

  private static final ResourceLocation DEFAULT_TEXTURE_STEVE =
      new ResourceLocation("textures/entity/player/wide/steve.png");
  private static final ResourceLocation DEFAULT_TEXTURE_ALEX =
      new ResourceLocation("textures/entity/player/slim/alex.png");

  private final PlayerModel<HoloLogHumanoidEntity> modelClassic;
  private final PlayerModel<HoloLogHumanoidEntity> modelSlim;

  public HoloLogHumanoidRenderer(final EntityRendererProvider.Context context) {
    super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.0f);
    this.modelClassic = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
    this.modelSlim = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
  }

  @Override
  public ResourceLocation getTextureLocation(HoloLogHumanoidEntity entity) {
    ResourceLocation customTexture = entity.getTexture();
    if (customTexture != null) {
      return customTexture;
    }
    return entity.isSlim() ? DEFAULT_TEXTURE_ALEX : DEFAULT_TEXTURE_STEVE;
  }

  @Override
  public void render(
      HoloLogHumanoidEntity entity,
      float entityYaw,
      float partialTicks,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight) {

    PlayerModel<HoloLogHumanoidEntity> model = entity.isSlim() ? this.modelSlim : this.modelClassic;
    this.model = model;
    model.setAllVisible(true);

    super.render(entity, entityYaw, partialTicks, poseStack, buffer, 15728880);
  }
}
