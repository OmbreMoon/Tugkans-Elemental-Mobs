package com.ombremoon.tugkansem.client.model;

import com.ombremoon.tugkansem.CommonClass;
import com.ombremoon.tugkansem.common.object.entity.mob.IceElementalMob;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class IceElementalModel extends GeoModel<IceElementalMob> {

    @Override
    public ResourceLocation getModelResource(IceElementalMob mob) {
        return CommonClass.customLocation( "geo/entity/" + getName(mob) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IceElementalMob mob) {
        return CommonClass.customLocation( "textures/entity/" + getName(mob) + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(IceElementalMob mob) {
        return CommonClass.customLocation( "animations/entity/" + getName(mob) + ".animation.json");
    }

    @Override
    public void setCustomAnimations(IceElementalMob animatable, long instanceId, AnimationState<IceElementalMob> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone(animatable.getHeadBone());

        if (head != null && !animatable.isDeadOrDying()) {
            EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(data.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }

    private String getName(IceElementalMob mob) {
        return ForgeRegistries.ENTITY_TYPES.getKey(mob.getType()).getPath();
    }
}
