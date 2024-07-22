package com.ombremoon.tugkansem.common.object.entity.mob;

import com.ombremoon.tugkansem.common.object.entity.IceSpike;
import com.ombremoon.tugkansem.common.object.entity.ai.AnimatedHitboxAttack;
import com.ombremoon.tugkansem.common.object.entity.ai.HeldAnimatedHitboxAttack;
import com.ombremoon.tugkansem.common.object.entity.ai.TestAnimatedHitboxAttack;
import com.ombremoon.tugkansem.common.object.entity.ai.TestHeldAnimatedHitboxAttack;
import com.ombremoon.tugkansem.common.sentinel.BoxInstanceManager;
import com.ombremoon.tugkansem.common.sentinel.ISentinel;
import com.ombremoon.tugkansem.common.sentinel.SentinelBox;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

public class IceBruiser extends IceElementalMob implements ISentinel {
    private static final TriggerableAnim RIGHT_SCRATCH = new TriggerableAnim("MeleeRight", "right_scratch");
    private static final TriggerableAnim LEFT_SCRATCH = new TriggerableAnim("MeleeLeft", "left_scratch");
    private static final TriggerableAnim HEAD_BUTT = new TriggerableAnim("MeleeFront", "headbutt");
    private static final TriggerableAnim LEAP = new TriggerableAnim("RangedLeap", "leap");
    private static final TriggerableAnim SPIKES = new TriggerableAnim("RangedSpike", "spikes");
    private static final TriggerableAnim MIST = new TriggerableAnim("RangedMist", "mist_breath");
    private final BoxInstanceManager boxInstanceManager = new BoxInstanceManager(this);

    public final SentinelBox SCRATCH = SentinelBox.Builder.of()
            .sizeAndOffset(0.3F, 1, 0, 1, 1)
            .boxDuration(100)
            .activeTicks((entity, integer) -> integer == 14)
            .typeDamage(DamageTypes.FREEZE, 1).build();

    public static final SentinelBox HEADBUTT = SentinelBox.Builder.of()
            .sizeAndOffset(0.3F, 0, 2, 1)
            .boxDuration(100)
            .activeTicks((entity, integer) -> integer == 14)
            .typeDamage(DamageTypes.FREEZE, 1).build();

    public static final SentinelBox TEST_SCRATCH = SentinelBox.Builder.of()
            .sizeAndOffset(0.3F, 1, 0, 2, -1)
            .boxDuration(100)
            .activeTicks((entity, integer) -> integer % 5 == 0)
            .typeDamage(DamageTypes.FREEZE, 1).build();

    public IceBruiser(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        tickBoxes();
    }

    @Override
    public BrainActivityGroup<? extends IceElementalMob> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>(),
                new FirstApplicableBehaviour<>(
                        new OneRandomBehaviour<>(
                                new AnimatedHitboxAttack<>(17, LEAP)
                                        .attackRange(4, 8)
                                        .attackSound(SoundEvents.PHANTOM_BITE)
                                        .attackInterval(mob -> 90)
                                        .whenActivating(mob -> {
                                            Vec3 vec3 = mob.getTarget().getPosition(0.1F);
                                            Vec3 vec31 = new Vec3(mob.getTarget().getX() - mob.getX(), 0.0D, mob.getTarget().getZ() - mob.getZ());
                                            vec31 = vec31.normalize().scale(0.5D).add(vec3);
                                            mob.getNavigation().moveTo(vec31.x, vec3.y, vec31.z, 3.0);
                                        })
                                        .whenStarting(mob -> {
                                            this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_SHOOT, this.getSoundSource(), 1.0F, 1.0F + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F);
                                        }),
                                new HeldAnimatedHitboxAttack<>(SPIKES)
                                        .attackRange(5, 10)
                                        .attackInterval(mob -> 120)
                                        .onTick(mob -> {
                                            lockMovement(mob);
                                            return true;
                                        })
                                        .whenStarting(mob -> {
                                            IceSpike iceSpike = new IceSpike(mob.level(), mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), (IceBruiser) mob);
                                            mob.level().addFreshEntity(iceSpike);
                                        })
                                        .runFor(mob -> 45)
                        ),
                        new OneRandomBehaviour<>(
                                new TestAnimatedHitboxAttack<>(14, RIGHT_SCRATCH, SCRATCH)
                                        .attackRange(4)
                                        .attackSound(SoundEvents.PHANTOM_BITE)
                                        .attackInterval(mob -> 60),
                                new TestAnimatedHitboxAttack<>(14, LEFT_SCRATCH, SCRATCH)
                                        .attackRange(4)
                                        .attackSound(SoundEvents.PHANTOM_BITE)
                                        .attackInterval(mob -> 60),
                                new TestAnimatedHitboxAttack<>(14, HEAD_BUTT, HEADBUTT)
                                        .attackRange(3)
                                        .attackSound(SoundEvents.IRON_GOLEM_DAMAGE)
                                        .attackInterval(mob -> 40),
                                new TestHeldAnimatedHitboxAttack<>(MIST, TEST_SCRATCH)
                                        .attackRange(4)
                                        .attackInterval(mob -> 100)
                                        .onTick(mob -> {
                                            lockMovement(mob);
                                            return true;
                                        })
                                        .runFor(mob -> 160)
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
        return ObjectArrayList.of(SCRATCH, TEST_SCRATCH, HEADBUTT);
    }

    @Override
    public BoxInstanceManager getBoxManager() {
        return this.boxInstanceManager;
    }
}
