package com.ombremoon.tugkansem.client.model;

import com.ombremoon.tugkansem.CommonClass;
import com.ombremoon.tugkansem.common.object.entity.projectile.IceSpriteProjectile;
import com.ombremoon.tugkansem.common.object.entity.projectile.RoseProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IceRoseModel extends GeoModel<RoseProjectile> {
    @Override
    public ResourceLocation getModelResource(RoseProjectile animatable) {
        return CommonClass.customLocation("geo/entity/projectile/ice_rose.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RoseProjectile animatable) {
        return CommonClass.customLocation("textures/entity/projectile/ice_rose.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RoseProjectile animatable) {
        return CommonClass.customLocation("animations/entity/projectile/ice_rose.animation.json");
    }
}
