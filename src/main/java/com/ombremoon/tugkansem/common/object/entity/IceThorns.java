package com.ombremoon.tugkansem.common.object.entity;

import com.ombremoon.tugkansem.common.init.EntityInit;
import com.ombremoon.tugkansem.common.object.entity.mob.IceElementalMob;
import com.ombremoon.tugkansem.common.object.entity.mob.IceQueen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class IceThorns extends Entity implements TraceableEntity, GeoIceEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation ROOT = RawAnimation.begin().thenPlay("root");
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public IceThorns(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public IceThorns(Level pLevel, double pX, double pY, double pZ, LivingEntity iceQueen) {
        super(EntityInit.ICE_THORNS.get(), pLevel);
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
        List<Entity> entityList = this.level().getEntities(this.getOwner(), this.getBoundingBox(), entity -> !(entity instanceof IceElementalMob));
        for (Entity entity : entityList) {
            if (entity instanceof LivingEntity livingEntity && this.tickCount % 20 == 0) {
                if (livingEntity.hurt(this.level().damageSources().thorns(this), 1))
                    this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_ATTACK_IMPACT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);

                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100));
            }
        }
        if (this.tickCount >= 240) {
            this.discard();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Root", 0, state -> state.setAndContinue(ROOT)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
