package gg.norisk.hulk.common.network

import gg.norisk.hulk.common.utils.HulkUtils
import gg.norisk.hulk.common.ManagerCommon.toId
import kotlinx.serialization.ExperimentalSerializationApi
import net.silkmc.silk.core.entity.directionVector
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.entity.posUnder
import net.silkmc.silk.network.packet.ServerPacketContext
import net.silkmc.silk.network.packet.c2sPacket

object NetworkManager {
    @OptIn(ExperimentalSerializationApi::class)
    val jumpPacket = c2sPacket<Double>("jump-packet".toId())

    fun init() {
        jumpPacket.receiveOnServer(::onJumpPacket)
    }

    private fun onJumpPacket(jumpStrength: Double, context: ServerPacketContext) {
        val player = context.player
        val startPos = context.player.posUnder
        val direction = context.player.directionVector.normalize().multiply(jumpStrength)
        context.player.modifyVelocity(direction)
    }
}
