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

package de.markusbordihn.scraptechworkshop.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.entity.BaseRobotEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BaseRobotModel<T extends BaseRobotEntity> extends EntityModel<T> {

  public static final ModelLayerLocation LAYER_LOCATION =
      new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "base_robot"), "main");
  protected final ModelPart left_arm;
  protected final ModelPart right_arm;
  protected final ModelPart left_front_wheel;
  protected final ModelPart left_back_wheel;
  protected final ModelPart right_front_wheel;
  protected final ModelPart right_back_wheel;
  private final ModelPart head;
  private final ModelPart body;
  private final ModelPart arms;
  private final ModelPart wheels;
  private final ModelPart left_wheel;
  private final ModelPart right_wheel;

  public BaseRobotModel(ModelPart root) {
    this.head = root.getChild("head");
    this.body = root.getChild("body");
    this.arms = root.getChild("arms");
    this.left_arm = this.arms.getChild("left_arm");
    this.right_arm = this.arms.getChild("right_arm");
    this.wheels = root.getChild("wheels");
    this.left_wheel = this.wheels.getChild("left_wheel");
    this.left_front_wheel = this.left_wheel.getChild("left_front_wheel");
    this.left_back_wheel = this.left_wheel.getChild("left_back_wheel");
    this.right_wheel = this.wheels.getChild("right_wheel");
    this.right_front_wheel = this.right_wheel.getChild("right_front_wheel");
    this.right_back_wheel = this.right_wheel.getChild("right_back_wheel");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshdefinition = new MeshDefinition();
    PartDefinition partdefinition = meshdefinition.getRoot();
    partdefinition.addOrReplaceChild(
        "head",
        CubeListBuilder.create()
            .texOffs(24, 30)
            .addBox(2.0F, -4.75F, -3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-4.0F, -2.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 15.0F, 0.0F));

    partdefinition.addOrReplaceChild(
        "body",
        CubeListBuilder.create()
            .texOffs(0, 30)
            .addBox(-1.5F, -1.875F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 12)
            .addBox(-3.0F, -0.625F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 18.125F, 0.0F));

    PartDefinition arms =
        partdefinition.addOrReplaceChild(
            "arms", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
    arms.addOrReplaceChild(
        "left_arm",
        CubeListBuilder.create()
            .texOffs(28, 30)
            .mirror()
            .addBox(-1.0F, -0.75F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false),
        PartPose.offset(-3.0F, -5.5F, 0.0F));
    arms.addOrReplaceChild(
        "right_arm",
        CubeListBuilder.create()
            .texOffs(28, 30)
            .addBox(0.0F, -0.75F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
        PartPose.offset(3.0F, -5.5F, 0.0F));

    PartDefinition wheels =
        partdefinition.addOrReplaceChild(
            "wheels",
            CubeListBuilder.create()
                .texOffs(24, 12)
                .addBox(-2.0F, -1.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 21.5F, 0.0F));

    PartDefinition left_wheel =
        wheels.addOrReplaceChild(
            "left_wheel",
            CubeListBuilder.create()
                .texOffs(0, 21)
                .addBox(-2.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-2.0F, 1.0F, 0.0F));
    left_wheel.addOrReplaceChild(
        "left_front_wheel",
        CubeListBuilder.create()
            .texOffs(12, 21)
            .addBox(-2.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.001F)),
        PartPose.offset(0.0F, 0.0F, -3.0F));
    left_wheel.addOrReplaceChild(
        "left_back_wheel",
        CubeListBuilder.create()
            .texOffs(12, 21)
            .addBox(-2.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.001F)),
        PartPose.offset(0.0F, 0.0F, 3.0F));

    PartDefinition right_wheel =
        wheels.addOrReplaceChild(
            "right_wheel",
            CubeListBuilder.create()
                .texOffs(0, 21)
                .mirror()
                .addBox(0.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .mirror(false),
            PartPose.offset(2.0F, 1.0F, 0.0F));
    right_wheel.addOrReplaceChild(
        "right_front_wheel",
        CubeListBuilder.create()
            .texOffs(12, 21)
            .mirror()
            .addBox(0.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.001F))
            .mirror(false),
        PartPose.offset(0.0F, 0.0F, -3.0F));
    right_wheel.addOrReplaceChild(
        "right_back_wheel",
        CubeListBuilder.create()
            .texOffs(12, 21)
            .mirror()
            .addBox(0.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.001F))
            .mirror(false),
        PartPose.offset(0.0F, 0.0F, 3.0F));

    return LayerDefinition.create(meshdefinition, 64, 64);
  }

  @Override
  public void setupAnim(
      T entity,
      float limbSwing,
      float limbSwingAmount,
      float ageInTicks,
      float netHeadYaw,
      float headPitch) {

    // Apply head animation based on entity's current state
    switch (entity.getHeadAnimation()) {
      case NOD_YES:
        // Nod animation: up and down
        float nodProgress = Mth.sin(ageInTicks * 0.6F);
        this.head.xRot = nodProgress * 0.6F;
        this.head.yRot = 0.0F;
        break;
      case SHAKE_NO:
        // Shake animation: left and right
        float shakeProgress = Mth.sin(ageInTicks * 0.6F);
        this.head.yRot = shakeProgress * 0.6F;
        this.head.xRot = 0.0F;
        break;
      case NONE:
      default:
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        break;
    }

    // Arm swing animation during movement
    float armSwing = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    this.left_arm.xRot = armSwing;
    this.right_arm.xRot = -armSwing;

    // Reset wheel rotations
    this.left_wheel.xRot = 0.0F;
    this.left_wheel.yRot = 0.0F;
    this.left_wheel.zRot = 0.0F;
    this.right_wheel.xRot = 0.0F;
    this.right_wheel.yRot = 0.0F;
    this.right_wheel.zRot = 0.0F;

    // Calculate body rotation effect for turning (differential steering)
    float bodyRotation = entity.yBodyRot - entity.yBodyRotO;
    while (bodyRotation > 180.0F) bodyRotation -= 360.0F;
    while (bodyRotation < -180.0F) bodyRotation += 360.0F;
    float rotationEffect = bodyRotation * 0.1F;

    // Apply wheel rotation with differential steering effect
    float wheelRotation = limbSwing * limbSwingAmount * 2.0F;
    this.left_front_wheel.xRot = wheelRotation + rotationEffect;
    this.left_back_wheel.xRot = wheelRotation + rotationEffect;
    this.right_front_wheel.xRot = wheelRotation - rotationEffect;
    this.right_back_wheel.xRot = wheelRotation - rotationEffect;
  }

  @Override
  public void renderToBuffer(
      PoseStack poseStack,
      VertexConsumer vertexConsumer,
      int packedLight,
      int packedOverlay,
      float red,
      float green,
      float blue,
      float alpha) {
    head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    arms.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    wheels.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
  }
}
