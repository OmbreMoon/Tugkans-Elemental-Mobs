package com.ombremoon.tugkansem.common;

import com.ombremoon.tugkansem.Constants;
import com.ombremoon.tugkansem.common.object.entity.mob.IceGolem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class CommonModEvents {

    @SubscribeEvent
    public static void onEntityDismount(EntityMountEvent event) {
        Entity entity = event.getEntityBeingMounted();
        if (entity instanceof IceGolem golem) {
            if (event.getEntityMounting() instanceof Player && event.isDismounting() && !golem.canDismount()) {
                event.setCanceled(true);
            }
        }
    }
}
