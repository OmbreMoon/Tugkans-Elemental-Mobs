package com.ombremoon.tugkansem.common.object.item;

import com.ombremoon.tugkansem.Constants;
import com.ombremoon.tugkansem.common.init.ParticleInit;
import com.ombremoon.tugkansem.common.object.entity.IceMist;
import com.ombremoon.tugkansem.common.object.entity.IceThorns;
import com.ombremoon.tugkansem.common.object.entity.IceWall;
import com.ombremoon.tugkansem.common.object.entity.mob.IceBruiser;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class DebugItem extends Item {

    public DebugItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide) {
            IceThorns iceWall = new IceThorns(pLevel, pPlayer.getX(), pPlayer.getY() - 2.5F, pPlayer.getZ(), pPlayer);
            pLevel.addFreshEntity(iceWall);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}