package com.ombremoon.tugkansem.common.object.entity.mob;

import com.ombremoon.sentinellib.api.box.OBBSentinelBox;
import com.ombremoon.sentinellib.api.box.SentinelBox;
import com.ombremoon.tugkansem.common.object.entity.IceSpike;
import com.ombremoon.tugkansem.common.object.entity.ai.AnimatedHitboxAttack;
import com.ombremoon.tugkansem.common.object.entity.ai.HeldAnimatedHitboxAttack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class IceBruiser extends IceElementalMob {
    private static final TriggerableAnim RIGHT_SCRATCH = new TriggerableAnim("MeleeRight", "right_scratch");
    private static final TriggerableAnim LEFT_SCRATCH = new TriggerableAnim("MeleeLeft", "left_scratch");
    private static final TriggerableAnim HEAD_BUTT = new TriggerableAnim("MeleeFront", "headbutt");
    private static final TriggerableAnim LEAP = new TriggerableAnim("RangedLeap", "leap");
    private static final TriggerableAnim SPIKES = new TriggerableAnim("RangedSpike", "spikes");
    private static final TriggerableAnim MIST = new TriggerableAnim("RangedMist", "mist_breath");

    public static final OBBSentinelBox SCRATCH = OBBSentinelBox.Builder.of("scratch")
            .sizeAndOffset(0.3F, 1.0F, 0.0F, 1.0F, 1.0F)
            .activeTicks((entity, integer) -> integer == 14)
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 15.0F).build();

    public static final OBBSentinelBox HEADBUTT = OBBSentinelBox.Builder.of("headbutt")
            .sizeAndOffset(0.3F, 0.0F, 2.0F, 1.0F)
            .activeTicks((entity, integer) -> integer == 14)
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 8.0F).build();

    public static final OBBSentinelBox DASH = OBBSentinelBox.Builder.of("dash")
            .sizeAndOffset(0.3F, 0.0F, 1.0F, 1.0F)
            .activeTicks((entity, integer) -> integer > 17)
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 12.0F).build();

    public static final OBBSentinelBox SPIKE_1 = OBBSentinelBox.Builder.of("spike_1")
            .sizeAndOffset(1.0F, 0.0F, 1.0F, 2.0F)
            .activeTicks((entity, integer) -> integer > 3 && integer % 2 == 0)
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 2.0F).build();

    public static final OBBSentinelBox SPIKE_2 = OBBSentinelBox.Builder.of("spike_2")
            .sizeAndOffset(1.0F, 1.5F, 1.0F, 1.5F, 4.0F)
            .activeTicks((entity, integer) -> integer > 3 && integer % 2 == 0)
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 2.0F).build();

    public static final OBBSentinelBox SPIKE_3 = OBBSentinelBox.Builder.of("spike_3")
            .sizeAndOffset(1.0F, 2.0F, 1.0F, 2.0F, 6.0F)
            .activeTicks((entity, integer) -> integer > 3 && integer % 2 == 0)
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 2.0F).build();

    public static final OBBSentinelBox SPIKE_4 = OBBSentinelBox.Builder.of("spike_4")
            .sizeAndOffset(1.0F, 2.5F, 1.0F, 2.5F, 8.0F)
            .activeTicks((entity, integer) -> integer > 3 && integer % 2 == 0)
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 2.0F).build();

    public static final OBBSentinelBox SPIKE_5 = OBBSentinelBox.Builder.of("spike_5")
            .sizeAndOffset(1.0F, 1.0F, 1.0F, 1.0F, 10.0F)
            .activeTicks((entity, integer) -> integer > 3 && integer % 2 == 0)
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 2.0F).build();

    public static final OBBSentinelBox MIST_BOX = OBBSentinelBox.Builder.of("mist")
            .sizeAndOffset(2.0F, 1.0F, 0.0F, 1.0F, 3.0F)
            .boxDuration(100)
            .activeTicks((entity, integer) -> integer > 40 && integer % 10 == 0)
            .attackCondition(ICE_ELEMENTAL_PREDICATE)
            .typeDamage(DamageTypes.FREEZE, 1.0F).build();

    public IceBruiser(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public BrainActivityGroup<? extends IceElementalMob> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>(),
                new FirstApplicableBehaviour<>(
                        new OneRandomBehaviour<>(
                                new AnimatedHitboxAttack<>(17, LEAP, DASH)
                                        .attackRange(4, 8)
                                        .attackSound(SoundEvents.PHANTOM_BITE)
                                        .keepHitbox()
                                        .whenActivating(mob -> {
                                            Vec3 vec3 = mob.getTarget().getPosition(0.1F);
                                            Vec3 vec31 = new Vec3(mob.getTarget().getX() - mob.getX(), 0.0D, mob.getTarget().getZ() - mob.getZ());
                                            vec31 = vec31.normalize().scale(0.5D).add(vec3);
                                            mob.getNavigation().moveTo(vec31.x, vec3.y, vec31.z, 3.0);
                                        })
                                        .whenStarting(mob -> {
                                            this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_SHOOT, this.getSoundSource(), 1.0F, 1.0F + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F);
                                        })
                                        .cooldownFor(mob -> 90),
                                new HeldAnimatedHitboxAttack<IceBruiser>(SPIKES, SPIKE_1)
                                        .attackRange(5, 10)
                                        .attackInterval(mob -> 80)
                                        .attackConsumer((iceBruiser, integer) -> {
                                            if (integer == 0) {
                                                BehaviorUtils.lookAtEntity(iceBruiser, iceBruiser.getTarget());
                                                IceSpike iceSpike = new IceSpike(iceBruiser.level(), iceBruiser.getX(), iceBruiser.getY(), iceBruiser.getZ(), iceBruiser.getYRot(), iceBruiser);
                                                iceBruiser.level().addFreshEntity(iceSpike);
                                            }

                                            this.lockMovement();
                                            if (integer == 4) this.triggerSentinelBox(SPIKE_2);
                                            if (integer == 8) this.triggerSentinelBox(SPIKE_3);
                                            if (integer == 12) this.triggerSentinelBox(SPIKE_4);
                                            if (integer == 16) this.triggerSentinelBox(SPIKE_5);
                                        })
                                        .runFor(mob -> 45)
                                        .cooldownFor(iceBruiser -> 130)
                        ),
                        new OneRandomBehaviour<>(
                                new AnimatedHitboxAttack<>(14, RIGHT_SCRATCH, SCRATCH)
                                        .attackRange(4)
                                        .attackSound(SoundEvents.PHANTOM_BITE)
                                        .cooldownFor(mob -> 60),
                                new AnimatedHitboxAttack<>(14, LEFT_SCRATCH, SCRATCH)
                                        .attackRange(4)
                                        .attackSound(SoundEvents.PHANTOM_BITE)
                                        .cooldownFor(mob -> 60),
                                new AnimatedHitboxAttack<>(14, HEAD_BUTT, HEADBUTT)
                                        .attackRange(3)
                                        .attackSound(SoundEvents.IRON_GOLEM_DAMAGE)
                                        .cooldownFor(mob -> 40),
                                new HeldAnimatedHitboxAttack<>(MIST, MIST_BOX)
                                        .attackRange(4)
                                        .attackInterval(mob -> 160)
                                        .onTick(mob -> {
                                            lockMovement();
                                            return true;
                                        })
                                        .runFor(mob -> 130)
                                        .cooldownFor(mob -> 300)
                        )
                )
        );
    }

    @Override
    public int getDeathTime() {
        return 400;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<IceElementalMob>(this, RIGHT_SCRATCH.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(RIGHT_SCRATCH.animName(), RawAnimation.begin().thenPlay(RIGHT_SCRATCH.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, LEFT_SCRATCH.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(LEFT_SCRATCH.animName(), RawAnimation.begin().thenPlay(LEFT_SCRATCH.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, HEAD_BUTT.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(HEAD_BUTT.animName(), RawAnimation.begin().thenPlay(HEAD_BUTT.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, LEAP.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(LEAP.animName(), RawAnimation.begin().thenPlay(LEAP.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, SPIKES.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(SPIKES.animName(), RawAnimation.begin().thenPlay(SPIKES.animName())));
        controllers.add(new AnimationController<IceElementalMob>(this, MIST.controllerName(), 0, state -> PlayState.STOP)
                .triggerableAnim(MIST.animName(), RawAnimation.begin().thenPlay(MIST.animName())));
    }

    public static AttributeSupplier.Builder createBruiserAttributes() {
        return IceElementalMob.createElementalAttributes()
                .add(Attributes.MAX_HEALTH, 250.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public List<SentinelBox> getSentinelBoxes() {
        return ObjectArrayList.of(
                SCRATCH,
                HEADBUTT,
                DASH,
                SPIKE_1,
                SPIKE_2,
                SPIKE_3,
                SPIKE_4,
                SPIKE_5,
                MIST_BOX
        );
    }

}
