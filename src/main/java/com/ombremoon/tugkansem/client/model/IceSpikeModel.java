package com.ombremoon.tugkansem.client.model;

import com.ombremoon.tugkansem.CommonClass;
import com.ombremoon.tugkansem.common.object.entity.IceSpike;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class IceSpikeModel extends DefaultedEntityGeoModel<IceSpike> {
    public IceSpikeModel() {
        super(CommonClass.customLocation("ice_spikes"));
    }
}
