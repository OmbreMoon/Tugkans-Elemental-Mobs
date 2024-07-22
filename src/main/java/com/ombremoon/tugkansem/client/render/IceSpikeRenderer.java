package com.ombremoon.tugkansem.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.tugkansem.client.model.IceSpikeModel;
import com.ombremoon.tugkansem.common.object.entity.IceSpike;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceSpikeRenderer extends GeoEntityRenderer<IceSpike> {
    public IceSpikeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceSpikeModel());
        addRenderLayer(new EmissiveLayer<>(this));
    }

    @Override
    protected void applyRotations(IceSpike animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        poseStack.mulPose(Axis.YP.rotationDegrees(-animatable.getYRot() - 90.0F));

    }
}
