package com.ombremoon.tugkansem.common.init;

import com.ombremoon.tugkansem.Constants;
import com.ombremoon.tugkansem.common.object.entity.IceMist;
import com.ombremoon.tugkansem.common.object.entity.IceSpike;
import com.ombremoon.tugkansem.common.object.entity.IceThorns;
import com.ombremoon.tugkansem.common.object.entity.IceWall;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MOD_ID);

    public static final RegistryObject<EntityType<Entity>> ICE_SPIKE = ENTITIES.register("ice_spikes", () -> EntityType.Builder.of(IceSpike::new, MobCategory.MISC).sized(4, 4).clientTrackingRange(4).build("ice_spike"));
    public static final RegistryObject<EntityType<Entity>> ICE_MIST = ENTITIES.register("ice_mist", () -> EntityType.Builder.of(IceMist::new, MobCategory.MISC).sized(3, 3).clientTrackingRange(4).build("ice_mist"));
    public static final RegistryObject<EntityType<Entity>> ICE_WALL = ENTITIES.register("ice_wall", () -> EntityType.Builder.of(IceWall::new, MobCategory.MISC).sized(5, 7).clientTrackingRange(4).build("ice_wall"));
    public static final RegistryObject<EntityType<Entity>> ICE_THORNS = ENTITIES.register("ice_thorns", () -> EntityType.Builder.of(IceThorns::new, MobCategory.MISC).sized(5.5F, 4).clientTrackingRange(4).build("ice_thorns"));

    public static void initEntityRegisters() {
        MobInit.init();
        ProjectileInit.init();
    }

    public static void register( IEventBus eventBus) {
        initEntityRegisters();
        ENTITIES.register(eventBus);
    }
}
