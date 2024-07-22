package com.ombremoon.tugkansem.common.init;

import com.ombremoon.tugkansem.common.object.entity.mob.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MobInit {
    public static void init() {}
    public static final List<AttributesRegister<?>> attributeSuppliers = new ArrayList<>();

    public static final RegistryObject<EntityType<IceBruiser>> ICE_BRUISER = registerMob("ice_bruiser", IceBruiser::new, false, 0x6fdaef, 0x39347b, IceBruiser::createBruiserAttributes);
    public static final RegistryObject<EntityType<IceGolem>> ICE_GOLEM = registerMob("ice_golem", IceGolem::new, false, 0x276ecf, 0x332a61, IceGolem::createGolemAttributes);
    public static final RegistryObject<EntityType<IceSprite>> ICE_SPRITE = registerMob("ice_sprite", IceSprite::new, false, 0xd5fff9, 0xffffff, IceSprite::createSpriteAttributes);
    public static final RegistryObject<EntityType<IceQueen>> ICE_QUEEN = registerMob("ice_queen", IceQueen::new, MobCategory.MONSTER, false, 0.6F, 2.8F, 0x87f5f2, 0xffffff, 24, IceQueen::createQueenAttributes);
    public static final RegistryObject<EntityType<PossessedAztecArmor>> AZTEC_ARMOR = registerMob("possessed_aztec_armor", PossessedAztecArmor::new, MobCategory.MONSTER, false, 0.6F, 2.8F, 0x87f5f2, 0xffffff, 24, PossessedAztecArmor::createSpriteAttributes);

    private static <T extends Mob> RegistryObject<EntityType<T>> registerMob(String name, EntityType.EntityFactory<T> factory, boolean fireImmune, int primaryColor, int secondaryColor, Supplier<AttributeSupplier.Builder> attributeSupplier) {
        return registerMob(name, factory, MobCategory.MONSTER, fireImmune, 0.6F, 1.95F, primaryColor, secondaryColor, 24, attributeSupplier);
    }

    private static <T extends Mob> RegistryObject<EntityType<T>> registerMob(String name, EntityType.EntityFactory<T> factory, MobCategory mobCategory, boolean fireImmune, float width, float height, int primaryColor, int secondaryColor, int clientTrackingRange, Supplier<AttributeSupplier.Builder> attributeSupplier) {
        EntityType.Builder<T> builder = EntityType.Builder.of(factory, mobCategory).sized(width, height).clientTrackingRange(clientTrackingRange);

        if (fireImmune) {
            builder.fireImmune();
        }

        RegistryObject<EntityType<T>> registryObject = EntityInit.ENTITIES.register(name, () -> {
            EntityType<T> entityType = builder.build(name);
            attributeSuppliers.add(new AttributesRegister<>(() -> entityType, attributeSupplier));
            return entityType;
        });

        registerSpawnEgg(name, registryObject, primaryColor, secondaryColor);

        return registryObject;
    }

    private static RegistryObject<Item> registerSpawnEgg(String name, Supplier<? extends EntityType<? extends Mob>> entityType, int primaryColor, int secondaryColor) {
        return ItemInit.registerItem(name + "_spawn_egg", () -> new ForgeSpawnEggItem(entityType, primaryColor, secondaryColor, new Item.Properties()), ItemInit.SPAWN_EGG_LIST);
    }

    public record AttributesRegister<E extends LivingEntity>(Supplier<EntityType<E>> entityTypeSupplier, Supplier<AttributeSupplier.Builder> attributeSupplier) {}
}
