package com.ombremoon.tugkansem.common.object.entity.mob;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.tugkansem.common.init.MemoryTypeInit;
import com.ombremoon.tugkansem.common.init.MobInit;
import com.ombremoon.tugkansem.common.object.entity.IceMist;
import com.ombremoon.tugkansem.common.object.entity.IceWall;
import com.ombremoon.tugkansem.common.object.entity.ai.HeldAnimatedHitboxAttack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableRangedAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomDelayedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomHeldBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StayWithinDistanceOfAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.RandomUtil;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class IceQueen extends IceElementalMob implements RangedAttackMob {
    protected static final EntityDataAccessor<Boolean> ENCASED = SynchedEntityData.defineId(IceQueen.class, EntityDataSerializers.BOOLEAN);
    private static final TriggerableAnim BASIC = new TriggerableAnim("RangedBasic", "basic");
    private static final TriggerableAnim BEAM = new TriggerableAnim("RangedBeam", "ice_beam");
    private static final TriggerableAnim SHARDS = new TriggerableAnim("MeleeAoE", "shards");
    private static final TriggerableAnim MIST = new TriggerableAnim("MeleeLargeAoE", "mist");
    private static final TriggerableAnim WALLS = new TriggerableAnim("RangedAoE", "wall_summon");

    public IceQueen(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ENCASED, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BrainActivityGroup<? extends IceElementalMob> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<IceQueen>()
                        .startCondition(IceQueen::isEncased),
                new StayWithinDistanceOfAttackTarget<IceQueen>()
                        .minDistance(4)
                        .startCondition(iceQueen -> !iceQueen.isEncased()),
                new FirstApplicableBehaviour<>(
                        new SummonWalls(),
                        new BeamAttack(),
                        new OneRandomBehaviour<>(
                                new CustomHeldBehaviour<IceQueen>(IceElementalMob::lockMovement)
                                        .whenStarting(iceQueen -> {
                                            IceMist iceMist = new IceMist(iceQueen.level(), iceQueen.getX(), iceQueen.getY(), iceQueen.getZ(), iceQueen);
                                            iceQueen.level().addFreshEntity(iceMist);
                                            triggerAnim(MIST.controllerName(), MIST.animName());
                                        })
                                        .startCondition(IceQueen::isEncased)
                                        .runFor(iceQueen -> 400)
                                        .cooldownFor(iceQueen -> 2400),
                                new CustomBehaviour<>(livingEntity -> {
                                    IceQueen queen = (IceQueen) livingEntity;
                                    Vec3 spawnPos = Vec3.atBottomCenterOf(RandomUtil.getRandomPositionWithinRange(queen.blockPosition(), 3, 2, 3, true, queen.level()));
                                    IceSprite iceSprite = MobInit.ICE_SPRITE.get().create(queen.level());
                                    if (iceSprite != null) {
                                        iceSprite.setPos(spawnPos);
                                        iceSprite.setTarget(queen.getTarget());
                                        queen.level().addFreshEntity(iceSprite);
                                    }
                                })
                                        .cooldownFor(livingEntity -> 300),
                                new HeldAnimatedHitboxAttack<>(SHARDS)
                                        .attackRange(3)
                                        .attackSound(27, SoundEvents.TRIDENT_RETURN, SoundEvents.TRIDENT_RETURN, SoundEvents.TRIDENT_RETURN, SoundEvents.TRIDENT_RETURN, SoundEvents.TRIDENT_RETURN, SoundEvents.TRIDENT_RETURN)
                                        .attackInterval(mob -> 80)
                                        .onTick(mob -> {
                                            lockMovement(mob);
                                            return true;
                                        })
                                        .runFor(mob -> 70),
                                new AnimatableRangedAttack<>(12)
                                        .whenActivating(livingEntity -> {
                                            triggerAnim("RangedBasic", "basic");
                                            this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.TRIDENT_RETURN, this.getSoundSource(), 1.0F, 2.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                                        })
                                        .startCondition(livingEntity -> {
                                            float dist = this.getTarget().distanceTo(livingEntity);
                                            return dist > 3 && dist < 12;
                                        })
                        )
                )
        );
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pVelocity) {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(walkAndIdleController(this));
        controllers.add(deathAnimationController(this));

        controllers.add(new AnimationController<IceElementalMob>(this, BASIC.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(BASIC.animName(), RawAnimation.begin().thenPlay(BASIC.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, BEAM.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(BEAM.animName(), RawAnimation.begin().thenPlay(BEAM.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, SHARDS.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(SHARDS.animName(), RawAnimation.begin().thenPlay(SHARDS.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, MIST.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(MIST.animName(), RawAnimation.begin().thenPlay(MIST.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, WALLS.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(WALLS.animName(), RawAnimation.begin().thenPlay(WALLS.animName())));
    }

    public <T extends IceElementalMob> AnimationController<T> walkAndIdleController(T animatable) {
        return new AnimationController<>(animatable, "Walk/Idle", 0, state -> state.setAndContinue(IDLE));
    }

    public boolean isEncased() {
        return this.entityData.get(ENCASED);
    }

    public void setEncased(boolean encased) {
        this.entityData.set(ENCASED, encased);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.ALLAY_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 100;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    private void playBeamSound() {
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.ALLAY_THROW, this.getSoundSource(), 1.0F, 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
    }

    @Override
    public int getDeathTime() {
        return 600;
    }

    public static AttributeSupplier.Builder createQueenAttributes() {
        return IceElementalMob.createElementalAttributes()
                .add(Attributes.MAX_HEALTH, 250.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    public static class BeamAttack extends HeldAnimatedHitboxAttack<IceQueen> {
        private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(SBLMemoryTypes.SPECIAL_ATTACK_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT));

        public BeamAttack() {
            super(BEAM);
            attackRange(6, 25);
            attackSound(12, SoundEvents.ILLUSIONER_CAST_SPELL);
            attackInterval(iceQueen -> 150);
            runFor(iceQueen -> 100);
            whenStopping(iceQueen -> {
                BrainUtils.setForgettableMemory(iceQueen, SBLMemoryTypes.SPECIAL_ATTACK_COOLDOWN.get(), true, 400);
            });
        }

        @Override
        protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
            return MEMORY_REQUIREMENTS;
        }

        @Override
        protected void tick(IceQueen iceQueen) {
            super.tick(iceQueen);
            if (this.getRunningTime() <= 45) {
                LivingEntity target = iceQueen.getTarget();
                if (target != null) BehaviorUtils.lookAtEntity(iceQueen, iceQueen.getTarget());
            } else {
                lockMovement(iceQueen);
                iceQueen.playBeamSound();
            }
        }
    }

    public static class SummonWalls extends HeldAnimatedHitboxAttack<IceQueen> {
        private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT));

        public SummonWalls() {
            super(WALLS);
            startCondition(iceQueen -> iceQueen.getHealth() <= iceQueen.getMaxHealth() / 2);
            whenStopping(iceQueen -> {
                iceQueen.setEncased(true);
            });
            runFor(iceQueen -> 90);
            cooldownFor(iceQueen -> 12000);
        }

        @Override
        protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
            return MEMORY_REQUIREMENTS;
        }

        @Override
        protected void tick(IceQueen entity) {
            super.tick(entity);
            lockMovement(entity);
            if (this.getRunningTime() == 65) {
                Vec3 spawnPos = Vec3.atBottomCenterOf(RandomUtil.getRandomPositionWithinRange(entity.blockPosition(), 3, 2, 3, true, entity.level()));
                IceGolem iceGolem = MobInit.ICE_GOLEM.get().create(entity.level());
                if (iceGolem != null) {
                    iceGolem.setPos(spawnPos);
                    entity.level().addFreshEntity(iceGolem);
                }
            } else if (this.getRunningTime() == 80) {
                float delta = 22.5F * Mth.DEG_TO_RAD;
                int r = 14;
                for (int i = 0; i < 16; i++) {
                    IceWall iceWall = new IceWall(entity.level(), entity.getX() + (r * Math.sin(i * delta)), entity.getY(), entity.getZ() + (r * Math.cos(i * delta)), 180 - (i * 22.5F), entity);
                    entity.level().addFreshEntity(iceWall);
                }
            }
        }
    }
}
