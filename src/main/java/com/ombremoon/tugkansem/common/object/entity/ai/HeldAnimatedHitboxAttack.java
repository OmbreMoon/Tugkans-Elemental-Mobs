package com.ombremoon.tugkansem.common.object.entity.ai;

import com.mojang.datafixers.util.Pair;
import com.ombremoon.sentinellib.api.box.SentinelBox;
import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.tugkansem.common.object.entity.mob.IceElementalMob;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.tslat.smartbrainlib.api.core.behaviour.HeldBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class HeldAnimatedHitboxAttack<E extends Mob> extends HeldBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT));
    private final IceElementalMob.TriggerableAnim anim;
    private final SentinelBox sentinelBox;
    protected Function<E, Integer> attackIntervalSupplier = entity -> 40;
    protected Pair<Integer, Integer> attackRange = Pair.of(0, 6);
    protected List<SoundEvent> attackSounds = new ArrayList<>();
    protected BiConsumer<E, Integer> attackConsumer = (mob, integer) -> {};
    protected int soundTime;
    @Nullable
    protected LivingEntity target = null;

    public HeldAnimatedHitboxAttack(IceElementalMob.TriggerableAnim anim) {
        this(anim, null);
    }

    public HeldAnimatedHitboxAttack(IceElementalMob.TriggerableAnim anim, @Nullable SentinelBox sentinelBox) {
        this.anim = anim;
        this.sentinelBox = sentinelBox;
    }

    public HeldAnimatedHitboxAttack<E> attackInterval(Function<E, Integer> supplier) {
        this.attackIntervalSupplier = supplier;

        return this;
    }

    public HeldAnimatedHitboxAttack<E> attackSound(SoundEvent... attackSound) {
        return attackSound(12, attackSound);
    }

    public HeldAnimatedHitboxAttack<E> attackSound(int soundTime, SoundEvent... attackSound) {
        this.attackSounds.addAll(List.of(attackSound));
        this.soundTime = soundTime;

        return this;
    }

    public HeldAnimatedHitboxAttack<E> attackRange(int minRange, int maxRange) {
        if (minRange >= maxRange) throw new IllegalArgumentException("Min range must be smaller than max range");
        this.attackRange = Pair.of(minRange, maxRange);

        return this;
    }

    public HeldAnimatedHitboxAttack<E> attackRange(int range) {
        return attackRange(0, range);
    }

    public HeldAnimatedHitboxAttack<E> attackConsumer(BiConsumer<E, Integer> consumer) {
        this.attackConsumer = consumer;

        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        this.target = BrainUtils.getTargetOfEntity(entity);

        if (this.target == null || !entity.getSensing().hasLineOfSight(this.target)) return false;

        return isWithinRange(entity);
    }

    private boolean isWithinRange(LivingEntity entity) {
        float dist = entity.distanceTo(this.target);
        var range = this.attackRange;
        return dist > range.getFirst() && dist < range.getSecond();
    }

    @Override
    protected void start(E entity) {
        BehaviorUtils.lookAtEntity(entity, this.target);
        ((GeoEntity)entity).triggerAnim(anim.controllerName(), anim.animName());
        BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, this.attackIntervalSupplier.apply(entity));
        if (this.sentinelBox != null)
            ((ISentinel)entity).triggerSentinelBox(this.sentinelBox);
    }

    @Override
    protected void tick(E entity) {
        super.tick(entity);
        this.attackConsumer.accept(entity, this.getRunningTime());
        if (this.getRunningTime() == this.soundTime) {
            for (SoundEvent soundEvent : this.attackSounds) {
                entity.level().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, entity.getSoundSource(), 1.0F, 1.0F + (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F);
            }
        }
    }

    @Override
    protected void stop(E entity) {
        super.stop(entity);
        if (this.sentinelBox != null)
            ((ISentinel)entity).removeSentinelInstance(this.sentinelBox);
    }
}
