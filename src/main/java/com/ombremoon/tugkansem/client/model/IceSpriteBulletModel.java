package com.ombremoon.tugkansem.client.model;

import com.ombremoon.tugkansem.CommonClass;
import com.ombremoon.tugkansem.common.object.entity.projectile.IceSpriteProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IceSpriteBulletModel extends GeoModel<IceSpriteProjectile> {
    @Override
    public ResourceLocation getModelResource(IceSpriteProjectile animatable) {
        return CommonClass.customLocation("geo/entity/projectile/ice_sprite_bullet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IceSpriteProjectile animatable) {
        return CommonClass.customLocation("textures/entity/projectile/ice_sprite_bullet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IceSpriteProjectile animatable) {
        return null;
    }
}
