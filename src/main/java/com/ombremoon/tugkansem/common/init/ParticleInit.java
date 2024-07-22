package com.ombremoon.tugkansem.common.init;

import com.ombremoon.tugkansem.Constants;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticleInit {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Constants.MOD_ID);

    public static final RegistryObject<SimpleParticleType> ICE_SPRITE_BULLET_TRAIL = PARTICLE_TYPES.register("ice_sprite_bullet_trail", () -> new SimpleParticleType(true));

    public static void register(IEventBus modEventBus) {
        PARTICLE_TYPES.register(modEventBus);
    }
}
