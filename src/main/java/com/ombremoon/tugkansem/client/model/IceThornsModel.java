package com.ombremoon.tugkansem.client.model;

import com.ombremoon.tugkansem.CommonClass;
import com.ombremoon.tugkansem.common.object.entity.IceThorns;
import com.ombremoon.tugkansem.common.object.entity.IceWall;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class IceThornsModel extends DefaultedEntityGeoModel<IceThorns> {
    public IceThornsModel() {
        super(CommonClass.customLocation("ice_thorns"));
    }
}
