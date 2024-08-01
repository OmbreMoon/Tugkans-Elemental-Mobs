package com.ombremoon.tugkansem.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ombremoon.tugkansem.CommonClass;
import com.ombremoon.tugkansem.common.object.entity.GeoIceEntity;
import com.ombremoon.tugkansem.common.object.entity.mob.IceBruiser;
import com.ombremoon.tugkansem.common.object.entity.mob.IceElementalMob;
import com.ombremoon.tugkansem.common.object.entity.projectile.IceElementalProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class EmissiveLayer<T extends GeoIceEntity> extends GeoRenderLayer<T> {

    public EmissiveLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        ResourceLocation resourceLocation = animatable instanceof IceBruiser ? CommonClass.customLocation("textures/entity/ice_bruiser_emissive.png") : this.getTextureResource(animatable);
        RenderType emissive = RenderType.eyes(resourceLocation);
        this.getRenderer().reRender(this.getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, emissive, bufferSource.getBuffer(emissive), partialTick, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (animatable instanceof IceBruiser) {
            RenderType translucent = RenderType.entityTranslucentEmissive(CommonClass.customLocation("textures/entity/ice_bruiser_mist.png"));
            this.getRenderer().reRender(this.getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, translucent, bufferSource.getBuffer(translucent), partialTick, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
