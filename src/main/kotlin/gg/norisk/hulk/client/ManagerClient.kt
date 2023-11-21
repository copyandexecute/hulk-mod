package gg.norisk.hulk.client

import gg.norisk.hulk.client.abilities.HulkTransformation
import gg.norisk.hulk.client.abilities.JumpManager
import gg.norisk.hulk.client.abilities.ThunderClap
import gg.norisk.hulk.client.renderer.AbilityRenderer
import gg.norisk.hulk.client.renderer.entity.HulkRenderer
import gg.norisk.hulk.common.registry.EntityRegistry
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

object ManagerClient : ClientModInitializer {
    override fun onInitializeClient() {
        EntityRendererRegistry.register(EntityRegistry.HULK, ::HulkRenderer)
        HulkTransformation.init()
        JumpManager.init()
        ThunderClap.init()
        AbilityRenderer.init()
    }
}
