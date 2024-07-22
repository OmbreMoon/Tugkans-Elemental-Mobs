package com.ombremoon.tugkansem.client.render;

import com.ombremoon.tugkansem.client.model.IceElementalModel;
import com.ombremoon.tugkansem.common.object.entity.mob.IceElementalMob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceElementalRenderer extends GeoEntityRenderer<IceElementalMob> {
    public IceElementalRenderer(EntityRendererProvider.Context context) {
        super(context, new IceElementalModel());
        addRenderLayer(new EmissiveLayer<>(this));
    }

    @Override
    protected float getDeathMaxRotation(IceElementalMob animatable) {
        return 0.0F;
    }

    @Override
    public int getPackedOverlay(IceElementalMob animatable, float u, float partialTick) {
        return OverlayTexture.pack(OverlayTexture.u(u),
                OverlayTexture.v(false));
    }
}
