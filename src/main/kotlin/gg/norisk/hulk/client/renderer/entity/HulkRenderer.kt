package gg.norisk.hulk.client.renderer.entity

import gg.norisk.hulk.client.model.entity.HulkModel
import gg.norisk.hulk.common.entity.HulkEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import software.bernie.geckolib.renderer.GeoEntityRenderer

class HulkRenderer(renderManager: EntityRendererFactory.Context) : GeoEntityRenderer<HulkEntity>(renderManager, HulkModel())
