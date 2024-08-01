package com.ombremoon.tugkansem.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.tugkansem.client.model.IceQueenBulletModel;
import com.ombremoon.tugkansem.common.object.entity.projectile.IceQueenProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceQueenBulletRenderer extends GeoEntityRenderer<IceQueenProjectile> {
    public IceQueenBulletRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceQueenBulletModel());
        addRenderLayer(new EmissiveLayer<>(this));
    }

    protected void applyRotations(IceQueenProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot())));
        poseStack.translate(1.05, -1.4, -1.35);
    }
}