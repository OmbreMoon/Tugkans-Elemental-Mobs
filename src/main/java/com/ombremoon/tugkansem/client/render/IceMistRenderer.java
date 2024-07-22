package com.ombremoon.tugkansem.client.render;

import com.ombremoon.tugkansem.client.model.IceMistModel;
import com.ombremoon.tugkansem.client.model.IceSpikeModel;
import com.ombremoon.tugkansem.common.object.entity.IceMist;
import com.ombremoon.tugkansem.common.object.entity.IceSpike;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceMistRenderer extends GeoEntityRenderer<IceMist> {
    public IceMistRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceMistModel());
    }
}
