package gg.norisk.hulk.common

import gg.norisk.hulk.common.network.NetworkManager
import gg.norisk.hulk.common.registry.EntityRegistry
import gg.norisk.hulk.common.registry.SoundRegistry
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier

object ManagerCommon : ModInitializer {
    override fun onInitialize() {
        EntityRegistry.init()
        NetworkManager.init()
        SoundRegistry.init()
    }

    fun String.toId() = Identifier("hulk", this)
}
