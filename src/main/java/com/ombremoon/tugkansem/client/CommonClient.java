package com.ombremoon.tugkansem.client;

import com.ombremoon.tugkansem.client.render.*;
import com.ombremoon.tugkansem.common.init.EntityInit;
import com.ombremoon.tugkansem.common.init.MobInit;
import com.ombremoon.tugkansem.common.init.ProjectileInit;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CommonClient {
    public static <T extends Entity> List<Renderers> getRenderers() {
        return List.of(
                new Renderers(MobInit.ICE_BRUISER, IceElementalRenderer::new),
                new Renderers(MobInit.ICE_SPRITE, IceElementalRenderer::new),
                new Renderers(MobInit.ICE_GOLEM, IceElementalRenderer::new),
                new Renderers(MobInit.ICE_QUEEN, IceElementalRenderer::new),
                new Renderers(MobInit.AZTEC_ARMOR, PossessedArmorRenderer::new),

                new Renderers(ProjectileInit.ROSE, IceRoseRenderer::new),
                new Renderers(ProjectileInit.ICE_QUEEN_BULLET, IceQueenBulletRenderer::new),
                new Renderers(ProjectileInit.ICE_SPRITE_BULLET, IceSpriteBulletRenderer::new),
                new Renderers(EntityInit.ICE_SPIKE, IceSpikeRenderer::new),
                new Renderers(EntityInit.ICE_MIST, IceMistRenderer::new),
                new Renderers(EntityInit.ICE_WALL, IceWallRenderer::new),
                new Renderers(EntityInit.ICE_THORNS, IceThornsRenderer::new)
        );
    }

    public record Renderers<T extends Entity>(Supplier<EntityType<T>> type, EntityRendererProvider<T> renderer) {
    }
}
