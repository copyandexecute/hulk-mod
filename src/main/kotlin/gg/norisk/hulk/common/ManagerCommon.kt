package gg.norisk.hulk.common

import gg.norisk.hulk.common.registry.EntityRegistry
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier

object ManagerCommon : ModInitializer {
    override fun onInitialize() {
        EntityRegistry.init()
    }

    fun String.toId() = Identifier("hulk", this)
}
