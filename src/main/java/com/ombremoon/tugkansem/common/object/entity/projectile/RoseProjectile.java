package com.ombremoon.tugkansem.common.object.entity.projectile;

import com.ombremoon.tugkansem.common.init.ParticleInit;
import com.ombremoon.tugkansem.common.init.ProjectileInit;
import com.ombremoon.tugkansem.common.object.entity.IceThorns;
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

public class RoseProjectile extends IceElementalProjectile {
    private int landTime;

    public RoseProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public RoseProjectile(Level pLevel, IceQueen queen) {
        super(ProjectileInit.ROSE.get(), pLevel);
        this.setOwner(queen);
        this.setPos(queen.getX() - (double) (queen.getBbWidth() + 1.0F) * 0.5 * (double) Mth.sin(queen.yBodyRot * ((float) Math.PI / 180F)), queen.getEyeY() - 0.8, queen.getZ() + (double) (queen.getBbWidth() + 1.0F) * 0.5 * (double) Mth.cos(queen.yBodyRot * ((float) Math.PI / 180F)));
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
            ++this.landTime;
            this.triggerAnim("Spawn", "ross_toss");
            if (this.landTime >= 40) {
                this.discard();
            } else if (this.landTime == 10) {
                double yPos = this.getOwner() != null ? this.getOwner().getY() : this.getY();
                IceThorns iceThorns = new IceThorns(this.level(), this.getX(), yPos - 2.0, this.getZ(), (LivingEntity) this.getOwner());
                this.level().addFreshEntity(iceThorns);
            }
        } else {
            this.setDeltaMovement(vec3.scale(0.99F));
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.06F, 0.0D));
            }
            this.setPos(d0, d1, d2);
        }

        if (this.tickCount >= 100) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity (EntityHitResult pResult){
        super.onHitEntity(pResult);
        if (!(pResult.getEntity() instanceof IceElementalMob)) {
            pResult.getEntity().hurt(this.damageSources().mobProjectile(this, (LivingEntity) this.getOwner()), 10.0F);
        }
    }
}