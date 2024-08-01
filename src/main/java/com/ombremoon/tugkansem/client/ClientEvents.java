package com.ombremoon.tugkansem.client;

import com.ombremoon.tugkansem.Constants;
import com.ombremoon.tugkansem.client.model.IceElementalModel;
import com.ombremoon.tugkansem.client.particle.IceSpriteBulletParticle;
import com.ombremoon.tugkansem.common.init.ParticleInit;
import com.ombremoon.tugkansem.common.object.entity.mob.IceElementalMob;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModBusEvents {

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            CommonClient.getRenderers().forEach(
                    renderers -> event.registerEntityRenderer((EntityType<?>) renderers.type().get(), renderers.renderer())
            );
        }

        @SubscribeEvent
        public static void registerParticleProvider(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ParticleInit.ICE_SPRITE_BULLET_TRAIL.get(), IceSpriteBulletParticle.Provider::new);
        }

    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
    }
}
