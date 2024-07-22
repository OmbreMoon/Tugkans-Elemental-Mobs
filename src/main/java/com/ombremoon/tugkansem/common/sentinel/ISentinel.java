package com.ombremoon.tugkansem.common.sentinel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface ISentinel {

    List<SentinelBox> getSentinelBoxes();

    BoxInstanceManager getBoxManager();

    default void triggerSentinelBox(SentinelBox sentinelBox) {
        Entity entity = (Entity) this;
        if (entity.level().isClientSide) {
            getBoxManager().addInstance(sentinelBox);
        } else {
            getBoxManager().addInstance(sentinelBox);
            //SEND TRIGGER PACKET
        }
    }

    default void tickBoxes() {
        getSentinelBoxes().forEach(SentinelBox::tick);
    }

    default void tickBoxes1() {
        getBoxManager().getInstances().forEach(BoxInstance::tick);
    }

    default void renderBox(SentinelBox sentinelBox, Entity entity, PoseStack poseStack, VertexConsumer vertexConsumer, float partialTicks) {
        poseStack.pushPose();
        float activeColor = sentinelBox.isActive() ? 0.0F : 1.0F;
        Vec3 center = sentinelBox.getCenter(partialTicks);
        Vec3 vec = sentinelBox.getBoxOffset();
//        Vec3 lookVec = entity.getViewVector(partialTicks);
//        double x = Mth.atan2(lookVec.x, lookVec.z);
        float xLookAngle = (-entity.getYHeadRot() + 90) * Mth.DEG_TO_RAD;
        float zLookAngle = -entity.getYHeadRot() * Mth.DEG_TO_RAD;
        Vec3 f = new Vec3(vec.z * Math.sin(zLookAngle), 0.0, vec.z * Math.cos(zLookAngle));
        Vec3 f1 = new Vec3(vec.x * Math.sin(xLookAngle), 0.0, vec.x * Math.cos(xLookAngle));
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, sentinelBox.getSentinelBox(partialTicks).move(-center.x + f.x + f1.x, -center.y + vec.y, -center.z + f.z + f1.z), 1.0F, activeColor, activeColor, 1.0F);
        poseStack.popPose();
    }
}
