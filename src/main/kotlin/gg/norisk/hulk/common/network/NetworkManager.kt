package gg.norisk.hulk.common.network

import gg.norisk.hulk.common.ManagerCommon.toId
import gg.norisk.hulk.common.entity.isHulk
import gg.norisk.hulk.common.utils.SimpleIntPos
import kotlinx.serialization.ExperimentalSerializationApi
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Box
import net.silkmc.silk.core.entity.directionVector
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.entity.posUnder
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.network.packet.ServerPacketContext
import net.silkmc.silk.network.packet.c2sPacket

object NetworkManager {
    @OptIn(ExperimentalSerializationApi::class)
    val jumpPacket = c2sPacket<Double>("jump-packet".toId())

    @OptIn(ExperimentalSerializationApi::class)
    val hulkTransformPacket = c2sPacket<Unit>("hulk-transform".toId())

    @OptIn(ExperimentalSerializationApi::class)
    val forceBreakBlock = c2sPacket<SimpleIntPos>("force-block-break".toId())

    @OptIn(ExperimentalSerializationApi::class)
    val thunderClapPacket = c2sPacket<Unit>("thunderclap".toId())

    fun init() {
        jumpPacket.receiveOnServer(::onJumpPacket)
        hulkTransformPacket.receiveOnServer(::onHulkTransform)
        forceBreakBlock.receiveOnServer(::onForceBlockBreak)
        thunderClapPacket.receiveOnServer(::onThunderClap)
    }

    private fun onThunderClap(unit: Unit, context: ServerPacketContext) {
        val player = context.player
        val startPos = player.pos.add(0.0, 0.3, 0.0)
        val direction = player.directionVector.normalize()

        mcCoroutineTask(client = false, howOften = 20) { task ->
            val center = startPos.add(direction.multiply(task.round.toDouble()))
            for (entity in (player.world as ServerWorld).getOtherEntities(player, Box.from(center).expand(5.0))) {
                entity.damage(player.world.damageSources.playerAttack(player), 2f)
                entity.modifyVelocity(direction.multiply(1.2))
            }
        }
    }

    private fun onForceBlockBreak(pos: SimpleIntPos, context: ServerPacketContext) {
        val blockPos = pos.toMcBlockPos()
        context.player.world.breakBlock(blockPos, true, context.player)
    }

    private fun onHulkTransform(unit: Unit, context: ServerPacketContext) {
        context.player.isHulk = !context.player.isHulk
        if (context.player.isHulk) {
            context.player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.baseValue = 60.0
        } else {
            context.player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.baseValue = 20.0
            context.player.damage(context.player.world.damageSources.generic(), 0.1f)
        }
    }

    private fun onJumpPacket(jumpStrength: Double, context: ServerPacketContext) {
        val player = context.player
        val startPos = context.player.posUnder
        val direction = context.player.directionVector.normalize().multiply(jumpStrength)
        context.player.modifyVelocity(direction)
    }
}
