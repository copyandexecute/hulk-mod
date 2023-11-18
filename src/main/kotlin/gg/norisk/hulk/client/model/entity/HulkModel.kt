package gg.norisk.hulk.client.model.entity

import gg.norisk.hulk.common.ManagerCommon.toId
import gg.norisk.hulk.common.entity.HulkEntity
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier
import software.bernie.geckolib.model.DefaultedEntityGeoModel

class HulkModel : DefaultedEntityGeoModel<HulkEntity>("hulk".toId()) {
    // We want our model to render using the translucent render type
    override fun getRenderType(animatable: HulkEntity, texture: Identifier): RenderLayer {
        return RenderLayer.getEntityTranslucent(getTextureResource(animatable))
    }
}
