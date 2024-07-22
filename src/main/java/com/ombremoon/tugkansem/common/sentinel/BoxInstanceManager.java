package com.ombremoon.tugkansem.common.sentinel;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class BoxInstanceManager {
    private final List<BoxInstance> instances = new ObjectArrayList<>();
    private final ISentinel sentinel;

    public BoxInstanceManager(ISentinel sentinel) {
        this.sentinel = sentinel;
    }

    public boolean addInstance(SentinelBox sentinelBox) {
        sentinelBox.setSentinelOwner((Entity) sentinel);
        BoxInstance instance = new BoxInstance(sentinelBox);
        return false;
    }

    public List<BoxInstance> getInstances() {
        return this.instances;
    }
}
