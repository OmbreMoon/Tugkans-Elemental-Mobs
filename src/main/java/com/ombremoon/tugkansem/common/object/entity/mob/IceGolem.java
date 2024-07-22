package com.ombremoon.tugkansem.common.object.entity.mob;

import com.ombremoon.tugkansem.common.object.entity.ai.AnimatedHitboxAttack;
import com.ombremoon.tugkansem.common.object.entity.ai.HeldAnimatedHitboxAttack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class IceGolem extends IceElementalMob {
    protected static final RawAnimation SPAWN = RawAnimation.begin().thenPlay("spawn");
    private static final TriggerableAnim RIGHT_BACKHAND = new TriggerableAnim("MeleeBackRight", "right_backhand");
    private static final TriggerableAnim LEFT_BACKHAND = new TriggerableAnim("MeleeBackLeft", "left_backhand");
    private static final TriggerableAnim RIGHT_PUNCH = new TriggerableAnim("MeleeRight", "right_punch");
    private static final TriggerableAnim LEFT_PUNCH = new TriggerableAnim("MeleeLeft", "left_punch");
    private static final TriggerableAnim SMASH = new TriggerableAnim("MeleeAoE", "smash");

    public IceGolem(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public BrainActivityGroup<? extends IceElementalMob> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>(),
                new OneRandomBehaviour<>(
                        new AnimatedHitboxAttack<>(14, RIGHT_BACKHAND)
                                .attackRange(4)
                                .attackSound(SoundEvents.GLASS_BREAK, SoundEvents.SNIFFER_EGG_HATCH, SoundEvents.IRON_GOLEM_DAMAGE, SoundEvents.WARDEN_ATTACK_IMPACT)
                                .attackInterval(mob -> 60),
                        new AnimatedHitboxAttack<>(14, LEFT_BACKHAND)
                                .attackRange(4)
                                .attackSound(SoundEvents.GLASS_BREAK, SoundEvents.SNIFFER_EGG_HATCH, SoundEvents.IRON_GOLEM_DAMAGE, SoundEvents.WARDEN_ATTACK_IMPACT)
                                .attackInterval(mob -> 60),
                        new AnimatedHitboxAttack<>(14, RIGHT_PUNCH)
                                .attackRange(4)
                                .attackSound(SoundEvents.GLASS_BREAK, SoundEvents.SNIFFER_EGG_HATCH, SoundEvents.IRON_GOLEM_DAMAGE, SoundEvents.WARDEN_ATTACK_IMPACT)
                                .attackInterval(mob -> 80),
                        new AnimatedHitboxAttack<>(14, LEFT_PUNCH)
                                .attackRange(4)
                                .attackSound(SoundEvents.GLASS_BREAK, SoundEvents.SNIFFER_EGG_HATCH, SoundEvents.IRON_GOLEM_DAMAGE, SoundEvents.WARDEN_ATTACK_IMPACT)
                                .attackInterval(mob -> 80),
                        new SmashAttack<>(SMASH)
                                .attackRange(5)
                                .attackInterval(mob -> 110)
                                .runFor(mob -> 60)
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
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isInvulnerable()) {
            lockMovement(this);
        }
    }

    @Override
    public boolean isInvulnerable() {
        return this.tickCount < 80;
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

    public static class SmashAttack<E extends IceGolem> extends HeldAnimatedHitboxAttack<E> {

        public SmashAttack(TriggerableAnim anim) {
            super(anim);
        }

        @Override
        protected void tick(E entity) {
            super.tick(entity);
            lockMovement(entity);

            if (this.getRunningTime() == 21) {
                ((IceGolem)entity).playGolemSound();
            }
        }
    }
}
