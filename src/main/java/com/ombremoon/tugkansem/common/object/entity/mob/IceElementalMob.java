package com.ombremoon.tugkansem.common.object.entity.mob;

import com.ombremoon.tugkansem.common.object.entity.GeoIceEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.WalkOrRunToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.example.SBLSkeleton;
import net.tslat.smartbrainlib.util.BrainUtils;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public abstract class IceElementalMob extends Monster implements GeoIceEntity, SmartBrainOwner<IceElementalMob> {
    protected static final EntityDataAccessor<Boolean> TURN_LOCKED = SynchedEntityData.defineId(IceElementalMob.class, EntityDataSerializers.BOOLEAN);
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation DEATH = RawAnimation.begin().thenLoop("death");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public IceElementalMob(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TURN_LOCKED, false);
    }

    @Override
    public List<? extends ExtendedSensor<? extends IceElementalMob>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<>(),
                new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends IceElementalMob> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new WalkOrRunToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends IceElementalMob> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<SBLSkeleton>(
                        new TargetOrRetaliate<>()
                                .useMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                                .attackablePredicate(target -> target.isAlive() && !(target instanceof IceElementalMob) && (!(target instanceof Player player) || !player.getAbilities().invulnerable) && !isAlliedTo(target)),
                        new SetRandomLookTarget<>()),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<>().speedModifier(1).avoidWaterWhen(pathfinderMob -> true),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(genericWalkIdleController(this));
        controllers.add(deathAnimationController(this));
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public <T extends IceElementalMob> AnimationController<T> genericWalkIdleController(T animatable) {
        return new AnimationController<>(animatable, "Walk/Idle", 0, state -> state.setAndContinue(state.isMoving() ? WALK : IDLE));
    }

    public <T extends IceElementalMob> AnimationController<T> deathAnimationController(T animatable) {
        return new AnimationController<>(animatable, "Death", 0, state -> {
            if (animatable.isDeadOrDying()) {
                return state.setAndContinue(DEATH);
            }
            return PlayState.STOP;
        });
    }

    @Override
    public void die(DamageSource pDamageSource) {
        if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, pDamageSource)) return;
        if (!this.isRemoved() && !this.dead) {
            Entity entity = pDamageSource.getEntity();
            LivingEntity livingentity = this.getKillCredit();
            if (this.deathScore >= 0 && livingentity != null) {
                livingentity.awardKillScore(this, this.deathScore, pDamageSource);
            }

            this.dead = true;
            this.getCombatTracker().recheckStatus();
            Level level = this.level();
            if (level instanceof ServerLevel) {
                ServerLevel serverlevel = (ServerLevel)level;
                if (entity == null || entity.killedEntity(serverlevel, this)) {
                    this.gameEvent(GameEvent.ENTITY_DIE);
                    this.dropAllDeathLoot(pDamageSource);
                    this.createWitherRose(livingentity);
                }

                this.level().broadcastEntityEvent(this, (byte)3);
            }
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= this.getDeathTime() && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        if (pSource.is(DamageTypes.IN_WALL)) return true;
        return super.isInvulnerableTo(pSource);
    }

    public static void lockMovement(Mob mob) {
        mob.getNavigation().stop();
        BrainUtils.clearMemory(mob, MemoryModuleType.LOOK_TARGET);
        BrainUtils.clearMemory(mob, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
        BrainUtils.clearMemory(mob, MemoryModuleType.ATTACK_TARGET);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public String getHeadBone() {
        return "h_head";
    }

    public abstract int getDeathTime();

    public static AttributeSupplier.Builder createElementalAttributes() {
        return Mob.createMobAttributes().
                add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 30.0D);
    }

    public record TriggerableAnim(String controllerName, String animName) {}
}
