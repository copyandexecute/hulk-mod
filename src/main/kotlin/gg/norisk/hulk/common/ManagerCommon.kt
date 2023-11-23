package gg.norisk.hulk.common

import gg.norisk.hulk.common.entity.IHulkPlayer
import gg.norisk.hulk.common.network.NetworkManager
import gg.norisk.hulk.common.registry.EntityRegistry
import gg.norisk.hulk.common.registry.SoundRegistry
import net.fabricmc.api.ModInitializer
import net.minecraft.block.Blocks
import net.minecraft.util.Identifier
import net.silkmc.silk.commands.command

object ManagerCommon : ModInitializer {
    override fun onInitialize() {
        EntityRegistry.init()
        NetworkManager.init()
        SoundRegistry.init()
        command("blockstate") {
            runs {
                (this.source.player as IHulkPlayer).blockStateInHand = Blocks.STONE.defaultState
            }
        }
    }

    fun String.toId() = Identifier("hulk", this)
}
