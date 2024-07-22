package com.ombremoon.tugkansem.client.render;

import com.ombremoon.tugkansem.client.model.IceElementalModel;
import com.ombremoon.tugkansem.common.object.entity.mob.IceElementalMob;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeRenderTypes;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PossessedArmorRenderer extends GeoEntityRenderer<IceElementalMob> {
    public PossessedArmorRenderer(EntityRendererProvider.Context context) {
        super(context, new IceElementalModel());
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

    @Override
    public RenderType getRenderType(IceElementalMob animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return ForgeRenderTypes.getItemLayeredSolid(getTextureLocation(animatable));
//        return RenderType.entitySolid(getTextureLocation(animatable));
    }
}
