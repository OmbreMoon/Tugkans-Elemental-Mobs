package com.ombremoon.tugkansem.common.sentinel;

import com.ombremoon.tugkansem.Constants;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SentinelBox {
    private Entity owner;
    private AABB aabb;
    private final Vec3 boxOffset;
    private final int duration;
    private final BiPredicate<Entity, Integer> activeDuration;
    private final Predicate<LivingEntity> attackCondition;
    private final Consumer<LivingEntity> attackConsumer;
    private final ResourceKey<DamageType> damageType;
    private final float damageAmount;
    private final List<LivingEntity> hurtEntities = new ObjectArrayList<>();
    private int tickCount;
    private boolean isActive;
    private boolean shouldTick;

    public SentinelBox(AABB aabb, Vec3 boxOffset, int duration, BiPredicate<Entity, Integer> activeDuration, Predicate<LivingEntity> attackCondition, Consumer<LivingEntity> attackConsumer, ResourceKey<DamageType> damageType, float damageAmount) {
        this.aabb = aabb;
        this.boxOffset = boxOffset;
        this.duration = duration;
        this.activeDuration = activeDuration;
        this.attackCondition = attackCondition;
        this.attackConsumer = attackConsumer;
        this.damageType = damageType;
        this.damageAmount = damageAmount;
    }

    public Entity getOwner() {
        return this.owner;
    }

    public void setSentinelOwner(Entity owner) {
        this.owner = owner;
    }

    public AABB getSentinelBox() {
        return getSentinelBox(0.1F);
    }

    public AABB getSentinelBox(float partialTicks) {
        return this.aabb.move(getBoxPosition(this.owner, partialTicks));
    }

    public Vec3 getBoxOffset() {
        return this.boxOffset;
    }

    public int getDuration() {
        return this.duration;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void tick () {
        if (this.shouldTick) {
            this.tickCount++;
            if (this.owner == null) {
                Constants.LOG.warn("Sentinel box does not have an owner and will not function as intended");
                return;
            }

            if (this.tickCount <= this.duration) {
                if (this.activeDuration.test(this.owner, this.tickCount)) {
                    this.isActive = true;
                    this.checkEntityInside();
                } else {
                    this.isActive = false;
                }
            } else {
                this.deactivateBox();
            }
        }
    }

    public boolean isTicking() {
        return this.shouldTick;
    }

    public void activateBox(Entity entity) {
        this.setSentinelOwner(entity);
        this.shouldTick = true;
    }

    public void deactivateBox() {
        this.shouldTick = false;
        this.tickCount = 0;
    }

    private void checkEntityInside() {
        List<Entity> entityList = this.owner.level().getEntities(owner, this.getSentinelBox());
        for (Entity entity : entityList) {
            if (entity instanceof LivingEntity livingEntity) {
                //TODO: TRY WHILE LOOP
                if (this.attackCondition.test(livingEntity)) {
                    if (!hurtEntities.contains(livingEntity)) {
                        this.attackConsumer.accept(livingEntity);
                        if (this.damageType != null) livingEntity.hurt(sentinelDamageSource(livingEntity.level(), this.damageType, this.getOwner()), this.damageAmount);
                        hurtEntities.add(livingEntity);
                    }
                }
            }
        }
        this.hurtEntities.clear();
    }

    public Vec3 getCenter(float partialTicks) {
        return this.getSentinelBox(partialTicks).getCenter();
    }

    private Vec3 getBoxPosition(Entity entity, float partialTicks) {
        Vec3 pos = entity.getPosition(partialTicks);
        float xLookAngle = (-entity.getYHeadRot() + 90) * Mth.DEG_TO_RAD;
        float zLookAngle = -entity.getYHeadRot() * Mth.DEG_TO_RAD;
        Vec3 f = new Vec3(boxOffset.z * Math.sin(zLookAngle), 0.0, boxOffset.z * Math.cos(zLookAngle));
        Vec3 f1 = new Vec3(boxOffset.x * Math.sin(xLookAngle), 0.0, boxOffset.x * Math.cos(xLookAngle));
        return new Vec3(pos.x + f.x + f1.x, pos.y + boxOffset.y, pos.z + f.z + f1.z);
    }

    public static DamageSource sentinelDamageSource(Level level, ResourceKey<DamageType> damageType, Entity attackEntity) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType), attackEntity);
    }

    private static void log(Object o) {
        Constants.LOG.info(String.valueOf(o));
    }

    public static class Builder {
        private AABB aabb;
        private Vec3 boxOffset;
        private int duration;
        private BiPredicate<Entity, Integer> activeDuration = (entity, integer) -> true;
        private Predicate<LivingEntity> attackCondition = livingEntity -> true;
        private Consumer<LivingEntity> attackConsumer = livingEntity -> {};
        private ResourceKey<DamageType> damageType;
        private float damageAmount;

        public Builder() {

        }

        public static Builder of() { return new Builder(); }

        public Builder sizeAndOffset(float xPos, float xOffset, float yOffset, float zOffset) {
            sizeAndOffset(xPos, xPos, xOffset, yOffset, zOffset);
            return this;
        }

        public Builder sizeAndOffset(float xPos, float yPos, float xOffset, float yOffset, float zOffset) {
            sizeAndOffset(xPos, yPos, xPos, xOffset, yOffset, zOffset);
            return this;
        }

        public Builder sizeAndOffset(float xPos, float yPos, float zPos, float xOffset, float yOffset, float zOffset) {
            double xSize = Math.abs(xPos);
            double ySize = Math.abs(yPos);
            double zSize = Math.abs(zPos);
            this.boxOffset = new Vec3(xOffset, yOffset, zOffset);
            this.aabb = new AABB(xSize, ySize, zSize, -xSize, -ySize, -zSize);
            return this;
        }

        public Builder boxDuration(int durationTicks) {
            this.duration = durationTicks;
            return this;
        }

        public Builder activeTicks(BiPredicate<Entity, Integer> activeDuration) {
            this.activeDuration = activeDuration;
            return this;
        }

        public Builder attackCondition(Predicate<LivingEntity> attackCondition) {
            this.attackCondition = attackCondition;
            return this;
        }

        public Builder attackConsumer(Consumer<LivingEntity> attackConsumer) {
            this.attackConsumer = attackConsumer;
            return this;
        }

        public Builder typeDamage(ResourceKey<DamageType> damageType, float damageAmount) {
            this.damageType = damageType;
            this.damageAmount = damageAmount;
            return this;
        }

        public SentinelBox build() {
            return new SentinelBox(this.aabb, this.boxOffset, this.duration, this.activeDuration, this.attackCondition, this.attackConsumer, this.damageType, this.damageAmount);
        }
    }
}
