package com.ombremoon.tugkansem.client.model;

import com.ombremoon.tugkansem.CommonClass;
import com.ombremoon.tugkansem.common.object.entity.projectile.IceQueenProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IceQueenBulletModel extends GeoModel<IceQueenProjectile> {
    @Override
    public ResourceLocation getModelResource(IceQueenProjectile animatable) {
        return CommonClass.customLocation("geo/entity/projectile/ice_queen_bullet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IceQueenProjectile animatable) {
        return CommonClass.customLocation("textures/entity/ice_queen.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IceQueenProjectile animatable) {
        return null;
    }
}