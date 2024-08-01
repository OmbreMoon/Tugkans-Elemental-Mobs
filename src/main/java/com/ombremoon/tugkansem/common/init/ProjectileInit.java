package com.ombremoon.tugkansem.common.init;

import com.ombremoon.tugkansem.common.object.entity.projectile.IceQueenProjectile;
import com.ombremoon.tugkansem.common.object.entity.projectile.IceSpriteProjectile;
import com.ombremoon.tugkansem.common.object.entity.projectile.RoseProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.registries.RegistryObject;

public class ProjectileInit {
    public static void init() {}

    public static final RegistryObject<EntityType<IceSpriteProjectile>> ICE_SPRITE_BULLET = registerProjectile("ice_sprite_bullet", IceSpriteProjectile::new, 0.5F, 0.5F);
    public static final RegistryObject<EntityType<IceQueenProjectile>> ICE_QUEEN_BULLET = registerProjectile("ice_queen_bullet", IceQueenProjectile::new, 0.5F, 0.5F);
    public static final RegistryObject<EntityType<RoseProjectile>> ROSE = registerProjectile("rose", RoseProjectile::new, 0.5F, 0.5F);

    protected static <T extends Projectile> RegistryObject<EntityType<T>> registerProjectile(String name, EntityType.EntityFactory<T> factory, float width, float height) {
        EntityType.Builder<T> builder = EntityType.Builder.of(factory, MobCategory.MISC).sized(width, height).clientTrackingRange(4);

        return EntityInit.ENTITIES.register(name, () -> {
            return builder.build(name);
        });
    }
}
