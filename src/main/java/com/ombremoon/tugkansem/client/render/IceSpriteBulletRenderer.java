package com.ombremoon.tugkansem.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ombremoon.tugkansem.client.model.IceSpriteBulletModel;
import com.ombremoon.tugkansem.common.object.entity.projectile.IceSpriteProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceSpriteBulletRenderer extends GeoEntityRenderer<IceSpriteProjectile> {
    public IceSpriteBulletRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceSpriteBulletModel());
        addRenderLayer(new EmissiveLayer<>(this));
    }

    @Override
    protected void applyRotations(IceSpriteProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) + 90.0F));
        poseStack.translate(0, -0.425, 0);
    }
}
