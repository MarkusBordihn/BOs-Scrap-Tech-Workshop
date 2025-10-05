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

package de.markusbordihn.scraptechworkshop.entity.hololog;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class HolologHumanoidEntity extends PathfinderMob {

  public static final String ID = "hololog_humanoid";

  private static final EntityDataAccessor<String> TEXTURE =
      SynchedEntityData.defineId(HolologHumanoidEntity.class, EntityDataSerializers.STRING);

  private static final EntityDataAccessor<Boolean> SLIM =
      SynchedEntityData.defineId(HolologHumanoidEntity.class, EntityDataSerializers.BOOLEAN);

  private ResourceLocation textureLocation;

  public HolologHumanoidEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
    super(entityType, level);
    this.setNoAi(true);
    this.setNoGravity(true);
    this.setInvulnerable(true);
    this.setSilent(true);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return PathfinderMob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 20.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.0D);
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(TEXTURE, "");
    this.entityData.define(SLIM, false);
  }

  public ResourceLocation getTexture() {
    if (this.textureLocation == null) {
      String textureString = this.entityData.get(TEXTURE);
      if (!textureString.isEmpty()) {
        this.textureLocation = new ResourceLocation(textureString);
      }
    }
    return this.textureLocation;
  }

  public void setTexture(ResourceLocation texture) {
    if (texture != null) {
      this.textureLocation = texture;
      this.entityData.set(TEXTURE, texture.toString());
    }
  }

  public boolean isSlim() {
    return this.entityData.get(SLIM);
  }

  public void setSlim(boolean slim) {
    this.entityData.set(SLIM, slim);
  }

  @Override
  public void addAdditionalSaveData(CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    if (this.textureLocation != null) {
      tag.putString("Texture", this.textureLocation.toString());
    }
    tag.putBoolean("Slim", this.entityData.get(SLIM));
  }

  @Override
  public void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    if (tag.contains("Texture")) {
      String textureString = tag.getString("Texture");
      if (!textureString.isEmpty()) {
        this.textureLocation = new ResourceLocation(textureString);
        this.entityData.set(TEXTURE, textureString);
      }
    }
    if (tag.contains("Slim")) {
      this.entityData.set(SLIM, tag.getBoolean("Slim"));
    }
  }

  @Override
  public void tick() {
    if (!this.level().isClientSide) {
      return;
    }
    super.tick();
  }

  @Override
  public boolean isPushable() {
    return false;
  }

  @Override
  public boolean canBeCollidedWith() {
    return false;
  }

  @Override
  protected void doPush(Entity entity) {
    // No pushing
  }

  @Override
  protected void pushEntities() {
    // No pushing
  }

  @Override
  public boolean isPickable() {
    return false;
  }

  @Override
  public boolean hurt(DamageSource source, float amount) {
    return false;
  }
}
