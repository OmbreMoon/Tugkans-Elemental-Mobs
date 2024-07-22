package com.ombremoon.tugkansem.common.init;

import com.mojang.serialization.Codec;
import com.ombremoon.tugkansem.Constants;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class MemoryTypeInit {
    public static void init() {}

    public static final Supplier<MemoryModuleType<Boolean>> ULTIMATE_ATTACK_COOLDOWN = register("ultimate_attack_cooldown");

    public static <T> Supplier<MemoryModuleType<T>> register(String id) {
        return register(id, null);
    }

    public static <T> Supplier<MemoryModuleType<T>> register(String id, @Nullable Codec<T> codec) {
        return Constants.SBL_LOADER.registerMemoryType(id, codec);
    }
}
