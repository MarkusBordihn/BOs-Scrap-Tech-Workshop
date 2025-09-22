package de.markusbordihn.scraptechworkshop.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.markusbordihn.scraptechworkshop.item.tool.ScrapMultitoolItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ScrapMultitoolRenderer {

  private static final Minecraft minecraft = Minecraft.getInstance();

  public static void renderHologram(
      PoseStack poseStack, MultiBufferSource bufferSource, int light) {
    Player player = minecraft.player;
    if (player == null) return;

    ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
    if (!(heldItem.getItem() instanceof ScrapMultitoolItem multitool)) return;

    // Check if tool has energy
    if (!multitool.hasEnergy(heldItem, 1)) return;

    HitResult hitResult = minecraft.hitResult;
    if (hitResult == null) return;

    ScrapMultitoolItem.ToolMode mode = ScrapMultitoolItem.ToolMode.NONE;
    Vec3 renderPos = null;

    // Determine tool mode based on target
    if (hitResult.getType() == HitResult.Type.ENTITY
        && hitResult instanceof EntityHitResult entityHit) {
      if (entityHit.getEntity() instanceof LivingEntity) {
        mode = ScrapMultitoolItem.ToolMode.SWORD;
        renderPos = entityHit.getLocation();
      }
    } else if (hitResult.getType() == HitResult.Type.BLOCK
        && hitResult instanceof BlockHitResult blockHit) {
      BlockState state = player.level().getBlockState(blockHit.getBlockPos());
      mode = multitool.getToolModeForBlock(state);
      renderPos = Vec3.atCenterOf(blockHit.getBlockPos());
    }

    if (mode == ScrapMultitoolItem.ToolMode.NONE || renderPos == null) return;

    // Render hologram
    renderToolHologram(
        poseStack,
        bufferSource,
        renderPos,
        mode,
        multitool.getData(heldItem).hologramColor(),
        light);
  }

  private static void renderToolHologram(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      Vec3 pos,
      ScrapMultitoolItem.ToolMode mode,
      int color,
      int light) {
    poseStack.pushPose();

    Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();
    poseStack.translate(pos.x - cameraPos.x, pos.y - cameraPos.y + 0.5, pos.z - cameraPos.z);

    // Face camera
    poseStack.mulPose(minecraft.gameRenderer.getMainCamera().rotation());

    float scale = 0.3f;
    poseStack.scale(scale, scale, scale);

    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.translucent());

    // Render icon based on mode
    renderModeIcon(poseStack, vertexConsumer, mode, color, light);

    poseStack.popPose();
  }

  private static void renderModeIcon(
      PoseStack poseStack,
      VertexConsumer consumer,
      ScrapMultitoolItem.ToolMode mode,
      int color,
      int light) {
    float r = ((color >> 16) & 255) / 255.0f;
    float g = ((color >> 8) & 255) / 255.0f;
    float b = (color & 255) / 255.0f;
    float a = 0.8f;

    // Simple quad for now - replace with actual icons later
    float size = 0.5f;

    PoseStack.Pose pose = poseStack.last();
    consumer
        .vertex(pose.pose(), -size, -size, 0)
        .color(r, g, b, a)
        .uv(0, 0)
        .overlayCoords(0)
        .uv2(light)
        .normal(pose.normal(), 0, 0, 1)
        .endVertex();
    consumer
        .vertex(pose.pose(), size, -size, 0)
        .color(r, g, b, a)
        .uv(1, 0)
        .overlayCoords(0)
        .uv2(light)
        .normal(pose.normal(), 0, 0, 1)
        .endVertex();
    consumer
        .vertex(pose.pose(), size, size, 0)
        .color(r, g, b, a)
        .uv(1, 1)
        .overlayCoords(0)
        .uv2(light)
        .normal(pose.normal(), 0, 0, 1)
        .endVertex();
    consumer
        .vertex(pose.pose(), -size, size, 0)
        .color(r, g, b, a)
        .uv(0, 1)
        .overlayCoords(0)
        .uv2(light)
        .normal(pose.normal(), 0, 0, 1)
        .endVertex();
  }
}
