package com.ombremoon.tugkansem.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.tugkansem.client.model.IceThornsModel;
import com.ombremoon.tugkansem.client.model.IceWallModel;
import com.ombremoon.tugkansem.common.object.entity.IceThorns;
import com.ombremoon.tugkansem.common.object.entity.IceWall;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceThornsRenderer extends GeoEntityRenderer<IceThorns> {
    public IceThornsRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceThornsModel());
        addRenderLayer(new EmissiveLayer<>(this));
    }

    @Override
    protected void applyRotations(IceThorns animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        poseStack.translate(0.0F, 3.0F, 0.0F);
    }
}
