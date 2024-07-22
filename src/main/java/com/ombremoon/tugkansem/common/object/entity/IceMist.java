package com.ombremoon.tugkansem.common.object.entity;

import com.ombremoon.tugkansem.common.init.EntityInit;
import com.ombremoon.tugkansem.common.object.entity.mob.IceElementalMob;
import com.ombremoon.tugkansem.common.object.entity.mob.IceQueen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class IceMist extends Entity implements TraceableEntity, GeoIceEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation MIST = RawAnimation.begin().thenPlay("mist");
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public IceMist(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public IceMist(Level pLevel, double pX, double pY, double pZ, IceQueen iceQueen) {
        super(EntityInit.ICE_MIST.get(), pLevel);
        this.setOwner(iceQueen);
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
        this.refreshDimensions();
        List<Entity> entityList = this.level().getEntities(this.getOwner(), this.getBoundingBox(), entity -> !(entity instanceof IceElementalMob));
        for (Entity entity : entityList) {
            if (entity instanceof LivingEntity livingEntity && this.tickCount % 20 == 0) {
                livingEntity.hurt(this.level().damageSources().freeze(), 2);
                //TODO: CHECK
                livingEntity.setIsInPowderSnow(true);
            }
        }
        if (this.tickCount >= 400) {
            this.discard();
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        float width = super.getDimensions(pPose).width;
        float scaledWidth = this.tickCount < 320 ? width * (0.4F * (1 + this.tickCount / 80.0F)) : width * 2.0F;
        return super.getDimensions(pPose).scale(scaledWidth, 1.0F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Mist", 0, state -> state.setAndContinue(MIST)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
