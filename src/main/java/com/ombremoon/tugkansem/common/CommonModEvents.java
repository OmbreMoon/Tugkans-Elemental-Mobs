package com.ombremoon.tugkansem.common;

import com.ombremoon.tugkansem.Constants;
import com.ombremoon.tugkansem.common.sentinel.ISentinel;
import com.ombremoon.tugkansem.common.sentinel.SentinelBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import software.bernie.geckolib.event.GeoRenderEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class CommonModEvents {

    @SubscribeEvent
    public static void renderSentinelBox(GeoRenderEvent.Entity.Post event) {
        Entity entity = event.getEntity();
        Minecraft minecraft = Minecraft.getInstance();

        if (entity.level() == null) {
            return;
        }

        if (minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes() && !minecraft.showOnlyReducedInfo() && entity instanceof ISentinel sentinel) {
            Matrix4f matrix = event.getPoseStack().last().pose();

            for (SentinelBox sentinelBox : sentinel.getSentinelBoxes()) {
//                Constants.LOG.info(String.valueOf(sentinelBox.isTicking()));
                if (sentinelBox.isTicking() && entity.getId() == sentinelBox.getOwner().getId()) {
                    sentinel.renderBox(sentinelBox, entity, event.getPoseStack(), event.getBufferSource().getBuffer(RenderType.lineStrip()), event.getPartialTick());
                }
            }
        }
    }

}
