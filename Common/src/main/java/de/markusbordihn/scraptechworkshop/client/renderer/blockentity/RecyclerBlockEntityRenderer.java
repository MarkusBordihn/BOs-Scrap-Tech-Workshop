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

package de.markusbordihn.scraptechworkshop.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.markusbordihn.scraptechworkshop.block.RecyclerBlock;
import de.markusbordihn.scraptechworkshop.block.entity.RecyclerBlockEntity;
import de.markusbordihn.scraptechworkshop.data.recycler.RecyclerStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RecyclerBlockEntityRenderer implements BlockEntityRenderer<RecyclerBlockEntity> {

  private static final int INPUT_SLOT = 0;
  private static final int PROGRESS_DATA_INDEX = 0;
  private static final int MAX_PROGRESS_DATA_INDEX = 1;

  private static final double ITEM_X_OFFSET = 0.5;
  private static final double ITEM_Y_OFFSET = 0.85;
  private static final double ITEM_Z_OFFSET = 0.56;

  private static final float INITIAL_SCALE = 0.4f;
  private static final float FINAL_SCALE = 0.01f;
  private static final float ANIMATION_SPEED_MULTIPLIER = 2.0f;
  private static final float DOWNWARD_MOVEMENT_FACTOR = 0.3f;

  private static final int PARTICLE_SPAWN_INTERVAL = 30;
  private static final int DUST_PARTICLE_INTERVAL = 40;
  private static final float DUST_PARTICLE_CHANCE = 0.3f;

  private static final double CLOSE_RENDER_DISTANCE_SQUARED = 16.0 * 16.0;
  private static final int PROXIMITY_CHECK_INTERVAL = 20;
  private final ItemRenderer itemRenderer;
  private long lastProximityCheck = -1;
  private boolean cachedPlayerNearby = false;
  private BlockPos lastCheckedPos = null;

  public RecyclerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    this.itemRenderer = context.getItemRenderer();
  }

  @Override
  public void render(
      final RecyclerBlockEntity blockEntity,
      final float partialTick,
      final PoseStack poseStack,
      final MultiBufferSource bufferSource,
      final int packedLight,
      final int packedOverlay) {

    Level level = blockEntity.getLevel();
    if (level == null || !level.isClientSide) {
      return;
    }

    RecyclerStatus status = blockEntity.getBlockState().getValue(RecyclerBlock.STATUS);
    if (status != RecyclerStatus.WORKING) {
      return;
    }

    ItemStack inputStack = blockEntity.getItem(INPUT_SLOT);
    if (inputStack.isEmpty()) {
      return;
    }

    if (isSolidBlockAbove(level, blockEntity.getBlockPos())) {
      return;
    }

    if (!isPlayerNearbyCached(level, blockEntity.getBlockPos())) {
      return;
    }

    float progress = getProcessingProgress(blockEntity, partialTick);
    if (progress <= 0.0f) {
      return;
    }

    addParticleEffects(blockEntity, level, inputStack);
    renderAnimatedItem(
        blockEntity,
        partialTick,
        poseStack,
        bufferSource,
        packedLight,
        packedOverlay,
        inputStack,
        level,
        progress);
  }

  private void renderAnimatedItem(
      final RecyclerBlockEntity blockEntity,
      final float partialTick,
      final PoseStack poseStack,
      final MultiBufferSource bufferSource,
      final int packedLight,
      final int packedOverlay,
      final ItemStack inputStack,
      final Level level,
      final float progress) {
    poseStack.pushPose();
    poseStack.translate(ITEM_X_OFFSET, ITEM_Y_OFFSET, ITEM_Z_OFFSET);

    Direction facing = blockEntity.getBlockState().getValue(RecyclerBlock.FACING);
    switch (facing) {
      case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
      case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
      case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
      default -> {}
    }

    long gameTime = level.getGameTime();
    float animationTime = (gameTime + partialTick);
    int rotationAxis = (int) (gameTime % 3);
    float rotation = (animationTime * ANIMATION_SPEED_MULTIPLIER) % 360.0f;
    float scale = Mth.lerp(progress, INITIAL_SCALE, FINAL_SCALE);
    float scaleReduction = INITIAL_SCALE - scale;
    float downwardMovement = scaleReduction * DOWNWARD_MOVEMENT_FACTOR;

    poseStack.translate(0, -downwardMovement, 0);
    poseStack.scale(scale, scale, scale);

    switch (rotationAxis) {
      case 0 -> poseStack.mulPose(Axis.XP.rotationDegrees(rotation));
      case 1 -> poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
      case 2 -> poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
      default -> poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
    }

    itemRenderer.renderStatic(
        inputStack,
        ItemDisplayContext.FIXED,
        packedLight,
        packedOverlay,
        poseStack,
        bufferSource,
        level,
        (int) blockEntity.getBlockPos().asLong());
    poseStack.popPose();
  }

  private void addParticleEffects(
      final RecyclerBlockEntity blockEntity, final Level level, final ItemStack inputStack) {
    long gameTime = level.getGameTime();
    if ((gameTime % PARTICLE_SPAWN_INTERVAL) != 0) {
      return;
    }

    double blockX = blockEntity.getBlockPos().getX() + 0.5;
    double blockY = blockEntity.getBlockPos().getY() + 0.75;
    double blockZ = blockEntity.getBlockPos().getZ() + 0.5;

    double offsetX = (level.getRandom().nextDouble() - 0.5) * 0.0375;
    double offsetY = (level.getRandom().nextDouble() - 0.5) * 0.025;
    double offsetZ = (level.getRandom().nextDouble() - 0.5) * 0.0375;

    double velocityX = (level.getRandom().nextDouble() - 0.5) * 0.0025;
    double velocityY = level.getRandom().nextDouble() * 0.00125;
    double velocityZ = (level.getRandom().nextDouble() - 0.5) * 0.0025;

    ItemParticleOption particleOption = new ItemParticleOption(ParticleTypes.ITEM, inputStack);
    level.addParticle(
        particleOption,
        blockX + offsetX,
        blockY + offsetY,
        blockZ + offsetZ,
        velocityX,
        velocityY,
        velocityZ);

    if ((gameTime % DUST_PARTICLE_INTERVAL) == 0
        && level.getRandom().nextFloat() < DUST_PARTICLE_CHANCE) {
      level.addParticle(
          ParticleTypes.SMOKE,
          blockX + (level.getRandom().nextDouble() - 0.5) * 0.025,
          blockY + 0.0125,
          blockZ + (level.getRandom().nextDouble() - 0.5) * 0.025,
          0,
          0.0025,
          0);
    }
  }

  private float getProcessingProgress(
      final RecyclerBlockEntity blockEntity, final float partialTick) {
    int progress = blockEntity.getContainerData().get(PROGRESS_DATA_INDEX);
    int maxProgress = blockEntity.getContainerData().get(MAX_PROGRESS_DATA_INDEX);

    if (maxProgress <= 0) {
      return 0.0f;
    }

    return Math.min(1.0f, (progress + partialTick) / maxProgress);
  }

  private boolean isSolidBlockAbove(final Level level, final BlockPos pos) {
    BlockPos abovePos = pos.above();
    BlockState stateAbove = level.getBlockState(abovePos);

    return stateAbove.isSolidRender(level, abovePos);
  }

  private boolean isPlayerNearbyCached(final Level level, final BlockPos pos) {
    long currentTime = level.getGameTime();
    if (lastProximityCheck == -1
        || currentTime - lastProximityCheck >= PROXIMITY_CHECK_INTERVAL
        || !pos.equals(lastCheckedPos)) {

      lastProximityCheck = currentTime;
      lastCheckedPos = pos.immutable();
      cachedPlayerNearby = calculatePlayerNearby(level, pos);
    }

    return cachedPlayerNearby;
  }

  private boolean calculatePlayerNearby(final Level level, final BlockPos pos) {
    Minecraft minecraft = Minecraft.getInstance();
    Player player = minecraft.player;
    if (player == null) {
      return false;
    }
    double deltaX = player.getX() - (pos.getX() + 0.5);
    double deltaY = player.getY() - (pos.getY() + 0.5);
    double deltaZ = player.getZ() - (pos.getZ() + 0.5);
    double distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    return distanceSquared <= CLOSE_RENDER_DISTANCE_SQUARED;
  }
}
