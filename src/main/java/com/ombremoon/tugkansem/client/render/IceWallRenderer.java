package com.ombremoon.tugkansem.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.tugkansem.client.model.IceWallModel;
import com.ombremoon.tugkansem.common.object.entity.IceWall;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceWallRenderer extends GeoEntityRenderer<IceWall> {
    public IceWallRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceWallModel());
        addRenderLayer(new EmissiveLayer<>(this));
    }

    @Override
    protected void applyRotations(IceWall animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        poseStack.mulPose(Axis.YP.rotationDegrees(-animatable.getYRot() - 180.0F));
        poseStack.translate(0.0F, 0.0F, -0.5F);
    }
}
