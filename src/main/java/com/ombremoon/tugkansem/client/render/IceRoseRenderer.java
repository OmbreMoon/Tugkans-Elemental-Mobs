package com.ombremoon.tugkansem.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.tugkansem.client.model.IceRoseModel;
import com.ombremoon.tugkansem.common.object.entity.projectile.RoseProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceRoseRenderer extends GeoEntityRenderer<RoseProjectile> {
    public IceRoseRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceRoseModel());
    }

    protected void applyRotations(RoseProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        poseStack.translate(0.0F, -1.45F, 1.3F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(animatable.xRotO));
    }
}