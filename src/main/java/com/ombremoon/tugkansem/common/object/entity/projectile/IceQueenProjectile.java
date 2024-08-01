package com.ombremoon.tugkansem.common.object.entity.projectile;

import com.ombremoon.tugkansem.common.init.ParticleInit;
import com.ombremoon.tugkansem.common.init.ProjectileInit;
import com.ombremoon.tugkansem.common.object.entity.mob.IceElementalMob;
import com.ombremoon.tugkansem.common.object.entity.mob.IceQueen;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class IceQueenProjectile extends IceElementalProjectile {
    public IceQueenProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public IceQueenProjectile(Level pLevel, IceQueen queen) {
        super(ProjectileInit.ICE_QUEEN_BULLET.get(), pLevel);
        this.setOwner(queen);
        this.setPos(queen.getX() - (double)(queen.getBbWidth() + 1.0F) * 0.5 * (double) Mth.sin(queen.yBodyRot * ((float)Math.PI / 180F)), queen.getEyeY() - 0.8, queen.getZ() + (double)(queen.getBbWidth() + 1.0F) * 0.5 * (double)Mth.cos(queen.yBodyRot * ((float)Math.PI / 180F)));
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vec3 = this.getDeltaMovement();
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult))
            this.onHit(hitresult);
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
            this.discard();
        } else {
            this.setDeltaMovement(vec3.scale(0.99F));
            this.setPos(d0, d1, d2);
        }

        if (this.tickCount >= 30) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!(pResult.getEntity() instanceof IceElementalMob)) {
            pResult.getEntity().hurt(this.damageSources().mobProjectile(this, (LivingEntity)this.getOwner()), 10.0F);
            this.discard();
        }
    }
}
