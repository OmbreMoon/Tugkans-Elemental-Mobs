package com.ombremoon.tugkansem.common.object.entity.mob;

import com.ombremoon.tugkansem.common.object.entity.projectile.IceSpriteProjectile;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableRangedAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class PossessedAztecArmor extends IceElementalMob {

    public PossessedAztecArmor(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public BrainActivityGroup<? extends IceElementalMob> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>());
    }

    @Override
    public BrainActivityGroup<? extends IceElementalMob> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new TargetOrRetaliate<>()
                        .useMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                        .attackablePredicate(target -> target.isAlive() && !(target instanceof IceElementalMob) && (!(target instanceof Player player) || !player.getAbilities().invulnerable) && !isAlliedTo(target)),
                new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
        );
    }

    @Override
    public BrainActivityGroup<? extends IceElementalMob> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>()

        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<IceElementalMob>(this, "Projectile", 0, state -> PlayState.STOP)
                .triggerableAnim("shoot", RawAnimation.begin().thenPlay("shoot")));
    }

    @Override
    public int getDeathTime() {
        return 400;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
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

    public static AttributeSupplier.Builder createSpriteAttributes() {
        return IceElementalMob.createElementalAttributes()
                .add(Attributes.MAX_HEALTH, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }
}
