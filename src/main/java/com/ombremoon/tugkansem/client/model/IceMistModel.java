package com.ombremoon.tugkansem.client.model;

import com.ombremoon.tugkansem.CommonClass;
import com.ombremoon.tugkansem.common.object.entity.IceMist;
import com.ombremoon.tugkansem.common.object.entity.IceSpike;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class IceMistModel extends DefaultedEntityGeoModel<IceMist> {
    public IceMistModel() {
        super(CommonClass.customLocation("ice_mist"));
    }

    @Override
    public RenderType getRenderType(IceMist animatable, ResourceLocation texture) {
        return RenderType.entityTranslucentEmissive(getTextureResource(animatable));
    }
}
