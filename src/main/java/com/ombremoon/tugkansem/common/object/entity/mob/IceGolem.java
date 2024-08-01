package com.ombremoon.tugkansem.common.object.entity.mob;

import com.ombremoon.sentinellib.api.BoxUtil;
import com.ombremoon.sentinellib.api.box.AABBSentinelBox;
import com.ombremoon.sentinellib.api.box.OBBSentinelBox;
import com.ombremoon.sentinellib.api.box.SentinelBox;
import com.ombremoon.tugkansem.common.object.entity.ai.AnimatedHitboxAttack;
import com.ombremoon.tugkansem.common.object.entity.ai.HeldAnimatedHitboxAttack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.util.BrainUtils;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class IceGolem extends IceElementalMob {
    protected static final EntityDataAccessor<Integer> CATCH_TIME = SynchedEntityData.defineId(IceGolem.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> CAN_DISMOUNT = SynchedEntityData.defineId(IceGolem.class, EntityDataSerializers.BOOLEAN);
    protected static final RawAnimation SPAWN = RawAnimation.begin().thenPlay("spawn");
    private static final TriggerableAnim RIGHT_BACKHAND = new TriggerableAnim("MeleeBackRight", "right_backhand");
    private static final TriggerableAnim LEFT_BACKHAND = new TriggerableAnim("MeleeBackLeft", "left_backhand");
    private static final TriggerableAnim RIGHT_PUNCH = new TriggerableAnim("MeleeRight", "right_punch");
    private static final TriggerableAnim LEFT_PUNCH = new TriggerableAnim("MeleeLeft", "left_punch");
    private static final TriggerableAnim SMASH = new TriggerableAnim("MeleeAoE", "smash");
    private static final TriggerableAnim CATCH = new TriggerableAnim("MeleeFrontHigh", "catch");
    private static final TriggerableAnim CRUSH = new TriggerableAnim("MeleeFrontLow", "crush");

    private static final OBBSentinelBox BACKHAND = OBBSentinelBox.Builder.of("backhand")
            .sizeAndOffset(1.0F, 0.5F, 0.5F, 0.0F, 1.5F, 1.0F)
            .activeTicks((entity, integer) -> integer == 14)
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 10).build();

    private static final OBBSentinelBox PUNCH_RIGHT = OBBSentinelBox.Builder.of("punch_right")
            .sizeAndOffset(0.5F, -0.7F, 1.7F, 1.0F)
            .activeTicks((entity, integer) -> integer == 14)
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 10).build();

    private static final OBBSentinelBox PUNCH_LEFT = OBBSentinelBox.Builder.of("punch_left")
            .sizeAndOffset(0.5F, 0.7F, 1.7F, 1.0F)
            .activeTicks((entity, integer) -> integer == 14)
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 10).build();

    private static final AABBSentinelBox SMASH_BOX = AABBSentinelBox.Builder.of("smash_box")
            .sizeAndOffset(2.0F, 1.0F, 0.0F, 1.0F, 0.0F)
            .activeTicks((entity, integer) -> integer == 21)
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 10).build();

    private static final OBBSentinelBox CATCH_BOX = OBBSentinelBox.Builder.of("crush")
            .sizeAndOffset(0.5F, 0.0F, 1.0F, 1.0F)
            .activeTicks((entity, integer) -> integer == 20)
            .boxDuration(25)
            .attackConsumer((attacker, target) -> {
                IceGolem iceGolem = (IceGolem)attacker;
                if (!iceGolem.hasCaughtEntity()) {
                    LivingEntity living = BrainUtils.getMemory(attacker, MemoryModuleType.ATTACK_TARGET);
                    if (living != null && living.getId() == target.getId() && !target.isBlocking()) {
                        iceGolem.setCatchTime(1);
                        target.startRiding(attacker, true);
                        iceGolem.triggerAnim(CRUSH.controllerName(), CRUSH.animName());
                        iceGolem.setCanDismount(false);
                    }
                }
            })
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 10).build();

    public IceGolem(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CATCH_TIME, 0);
        this.entityData.define(CAN_DISMOUNT, false);
    }

    @Override
    public BrainActivityGroup<? extends IceElementalMob> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>(),
                new OneRandomBehaviour<>(
                        new AnimatedHitboxAttack<>(14, RIGHT_BACKHAND, BACKHAND)
                                .attackRange(4)
                                .attackSound(SoundEvents.GLASS_BREAK, SoundEvents.SNIFFER_EGG_HATCH, SoundEvents.IRON_GOLEM_DAMAGE, SoundEvents.WARDEN_ATTACK_IMPACT)
                                .attackInterval(mob -> 60),
                        new AnimatedHitboxAttack<>(14, LEFT_BACKHAND, BACKHAND)
                                .attackRange(4)
                                .attackSound(SoundEvents.GLASS_BREAK, SoundEvents.SNIFFER_EGG_HATCH, SoundEvents.IRON_GOLEM_DAMAGE, SoundEvents.WARDEN_ATTACK_IMPACT)
                                .attackInterval(mob -> 60),
                        new AnimatedHitboxAttack<>(14, RIGHT_PUNCH, PUNCH_RIGHT)
                                .attackRange(4)
                                .attackSound(SoundEvents.GLASS_BREAK, SoundEvents.SNIFFER_EGG_HATCH, SoundEvents.IRON_GOLEM_DAMAGE, SoundEvents.WARDEN_ATTACK_IMPACT)
                                .attackInterval(mob -> 80),
                        new AnimatedHitboxAttack<>(14, LEFT_PUNCH, PUNCH_LEFT)
                                .attackRange(4)
                                .attackSound(SoundEvents.GLASS_BREAK, SoundEvents.SNIFFER_EGG_HATCH, SoundEvents.IRON_GOLEM_DAMAGE, SoundEvents.WARDEN_ATTACK_IMPACT)
                                .attackInterval(mob -> 80),
                        new HeldAnimatedHitboxAttack<IceGolem>(CATCH, CATCH_BOX)
                                .attackRange(5)
                                .attackInterval(iceGolem -> 80)
                                .onTick(iceGolem -> {
                                    if (iceGolem.getCatchTime() != 0)
                                        lockMovement();

                                    return true;
                                })
                                .runFor(iceGolem -> iceGolem.hasCaughtEntity() ? 80 : 60)
                                .whenStopping(iceGolem -> iceGolem.setCatchTime(0)),
                        new SmashAttack()
                )
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "Spawn", 0, state -> {
            if (this.isInvulnerable())
                return state.setAndContinue(SPAWN);
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<IceElementalMob>(this, RIGHT_BACKHAND.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(RIGHT_BACKHAND.animName(), RawAnimation.begin().thenPlay(RIGHT_BACKHAND.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, LEFT_BACKHAND.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(LEFT_BACKHAND.animName(), RawAnimation.begin().thenPlay(LEFT_BACKHAND.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, RIGHT_PUNCH.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(RIGHT_PUNCH.animName(), RawAnimation.begin().thenPlay(RIGHT_PUNCH.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, LEFT_PUNCH.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(LEFT_PUNCH.animName(), RawAnimation.begin().thenPlay(LEFT_PUNCH.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, SMASH.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(SMASH.animName(), RawAnimation.begin().thenPlay(SMASH.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, CATCH.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(CATCH.animName(), RawAnimation.begin().thenPlay(CATCH.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, CRUSH.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(CRUSH.animName(), RawAnimation.begin().thenPlay(CRUSH.animName())));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isInvulnerable()) {
            lockMovement();
        }

        if (this.hasCaughtEntity()) {
            int i = this.getCatchTime();
            this.setCatchTime(i + 1);
            if (this.getCatchTime() >= 21) {
                this.setCanDismount(true);
                LivingEntity living = (LivingEntity)this.getPassengers().get(0);
                float f = -this.getYHeadRot() * Mth.DEG_TO_RAD;
                living.hurt(BoxUtil.sentinelDamageSource(this.level(), DamageTypes.FREEZE, this), 20.0F);
                living.dismountTo(this.getX() + 2.0 * Math.sin(f), this.getY() - 1.0, this.getZ() + 2.0 * Math.cos(f));
                this.playGolemSound();
            }
        }
    }

    @Override
    protected void positionRider(Entity pPassenger, MoveFunction pCallback) {
        if (pPassenger instanceof LivingEntity livingEntity) {
            float f = -this.getYHeadRot() * Mth.DEG_TO_RAD;
            float f1 = (-this.getYHeadRot() + 90.0F) * Mth.DEG_TO_RAD;;
            pCallback.accept(livingEntity, this.getX() + 1.2F * (Math.sin(f) - Math.sin(f1)), this.getY() + ((float)this.getCatchTime() * 0.15F), this.getZ() + 1.2F * (Math.cos(f) - Math.cos(f1)));
        }
    }

    @Override
    public boolean isInvulnerable() {
        return this.tickCount < 80;
    }

    public boolean hasCaughtEntity() {
        return this.entityData.get(CATCH_TIME) != 0 && !this.getPassengers().isEmpty();
    }

    public int getCatchTime() {
        return this.entityData.get(CATCH_TIME);
    }

    public void setCatchTime(int catchTime) {
        this.entityData.set(CATCH_TIME, catchTime);
    }

    public boolean canDismount() {
        return this.entityData.get(CAN_DISMOUNT);
    }

    public void setCanDismount(boolean canDismount) {
        this.entityData.set(CAN_DISMOUNT, canDismount);
    }

    private void playGolemSound() {
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.TOTEM_USE, this.getSoundSource(), 1.0F, 2.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.GLASS_BREAK, this.getSoundSource(), 1.0F, 1.5F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.SNIFFER_EGG_HATCH, this.getSoundSource(), 1.0F, 1.2F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.IRON_GOLEM_DAMAGE, this.getSoundSource(), 1.0F, 1.5F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_ATTACK_IMPACT, this.getSoundSource(), 1.0F, 0.8F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
    }

    @Override
    public int getDeathTime() {
        return 400;
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        if (this.deathTime == 21) {
            this.playGolemSound();
        }
    }

    public static AttributeSupplier.Builder createGolemAttributes() {
        return IceElementalMob.createElementalAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public List<SentinelBox> getSentinelBoxes() {
        return ObjectArrayList.of(
                BACKHAND,
                PUNCH_RIGHT,
                PUNCH_LEFT,
                SMASH_BOX,
                CATCH_BOX
        );
    }

    public static class SmashAttack extends HeldAnimatedHitboxAttack<IceGolem> {

        public SmashAttack() {
            super(SMASH, SMASH_BOX);
            attackRange(5);
            runFor(iceGolem -> 60);
            cooldownFor(iceGolem -> 110);
        }

        @Override
        protected void tick(IceGolem entity) {
            super.tick(entity);
            entity.lockMovement();

            if (this.getRunningTime() == 21) {
                entity.playGolemSound();
            }
        }
    }
}
