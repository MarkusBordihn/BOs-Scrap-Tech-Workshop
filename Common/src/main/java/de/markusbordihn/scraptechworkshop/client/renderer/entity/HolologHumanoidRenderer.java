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
import de.markusbordihn.scraptechworkshop.entity.hololog.HolologHumanoidEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HolologHumanoidRenderer
    extends MobRenderer<HolologHumanoidEntity, PlayerModel<HolologHumanoidEntity>> {

  private static final ResourceLocation DEFAULT_TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/entity/hololog/hololog_humanoid.png");

  private final PlayerModel<HolologHumanoidEntity> modelClassic;
  private final PlayerModel<HolologHumanoidEntity> modelSlim;

  public HolologHumanoidRenderer(EntityRendererProvider.Context context) {
    super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.0f);
    this.modelClassic = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
    this.modelSlim = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
  }

  @Override
  public ResourceLocation getTextureLocation(HolologHumanoidEntity entity) {
    ResourceLocation customTexture = entity.getTexture();
    return customTexture != null ? customTexture : DEFAULT_TEXTURE;
  }

  @Override
  public void render(
      HolologHumanoidEntity entity,
      float entityYaw,
      float partialTicks,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight) {

    PlayerModel<HolologHumanoidEntity> model = entity.isSlim() ? this.modelSlim : this.modelClassic;
    this.model = model;
    model.setAllVisible(true);

    super.render(entity, entityYaw, partialTicks, poseStack, buffer, 15728880);
  }
}
