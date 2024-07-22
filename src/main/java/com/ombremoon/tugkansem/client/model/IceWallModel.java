package com.ombremoon.tugkansem.client.model;

import com.ombremoon.tugkansem.CommonClass;
import com.ombremoon.tugkansem.common.object.entity.IceMist;
import com.ombremoon.tugkansem.common.object.entity.IceWall;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class IceWallModel extends DefaultedEntityGeoModel<IceWall> {
    public IceWallModel() {
        super(CommonClass.customLocation("ice_wall"));
    }
}
