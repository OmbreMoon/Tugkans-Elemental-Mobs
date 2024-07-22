package com.ombremoon.tugkansem.common.object.entity;

import com.ombremoon.tugkansem.common.init.EntityInit;
import com.ombremoon.tugkansem.common.object.entity.mob.IceElementalMob;
import com.ombremoon.tugkansem.common.object.entity.mob.IceQueen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class IceWall extends Entity implements TraceableEntity, GeoIceEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation SPAWN = RawAnimation.begin().thenPlay("spawn");
    protected static final RawAnimation DEATH = RawAnimation.begin().thenPlay("death");
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public IceWall(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public IceWall(Level pLevel, double pX, double pY, double pZ, float pYRot, LivingEntity iceQueen) {
        super(EntityInit.ICE_WALL.get(), pLevel);
        this.setOwner(iceQueen);
        this.setYRot(pYRot);
        this.setPos(pX, pY, pZ);
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        this.owner = pOwner;
        this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount < 6 && (this.tickCount == 1 || this.tickCount % 2 == 1)) {
            this.playWallEmergeSound();
        }
        if (this.tickCount >= 1280) {
            LivingEntity livingEntity = this.getOwner();
            if (livingEntity != null && livingEntity instanceof IceQueen iceQueen && !iceQueen.isRemoved()) iceQueen.setEncased(false);
            this.discard();
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    private void playWallEmergeSound() {
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.GLASS_BREAK, this.getSoundSource(), 1.0F, 1.5F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.SNIFFER_EGG_HATCH, this.getSoundSource(), 1.0F, 2.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.IRON_GOLEM_DAMAGE, this.getSoundSource(), 1.0F, 2.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_ATTACK_IMPACT, this.getSoundSource(), 1.0F, 0.8F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 0, state -> {
            if (this.tickCount > 40 && this.tickCount < 1200) {
                return state.setAndContinue(IDLE);
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Spawn", 0, state -> {
            if (this.tickCount < 40) {
                return state.setAndContinue(SPAWN);
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "Death", 0, state -> {
            if (this.tickCount > 1200) {
                return state.setAndContinue(DEATH);
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
