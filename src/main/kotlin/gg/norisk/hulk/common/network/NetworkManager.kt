package gg.norisk.hulk.common.network

import gg.norisk.hulk.common.ManagerCommon.toId
import gg.norisk.hulk.common.entity.isHulk
import gg.norisk.hulk.common.registry.SoundRegistry
import gg.norisk.hulk.common.utils.HulkUtils
import gg.norisk.hulk.common.utils.SimpleIntPos
import kotlinx.serialization.ExperimentalSerializationApi
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.Box
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.entity.directionVector
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.entity.posUnder
import net.silkmc.silk.core.kotlin.asMinecraftRandom
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.network.packet.ServerPacketContext
import net.silkmc.silk.network.packet.c2sPacket
import net.silkmc.silk.network.packet.s2cPacket
import java.util.UUID
import kotlin.random.Random

object NetworkManager {
    var flyingEntities = mutableSetOf<UUID>()

    @OptIn(ExperimentalSerializationApi::class)
    val jumpPacket = c2sPacket<Double>("jump-packet".toId())

    @OptIn(ExperimentalSerializationApi::class)
    val hulkTransformPacket = c2sPacket<Unit>("hulk-transform".toId())

    @OptIn(ExperimentalSerializationApi::class)
    val forceBreakBlock = c2sPacket<SimpleIntPos>("force-block-break".toId())

    @OptIn(ExperimentalSerializationApi::class)
    val thunderClapPacket = c2sPacket<Unit>("thunderclap".toId())

    @OptIn(ExperimentalSerializationApi::class)
    val punchPacket = c2sPacket<SimpleIntPos>("punch".toId())

    @OptIn(ExperimentalSerializationApi::class)
    val growlSoundPacket = s2cPacket<Unit>("growl".toId())


    fun init() {
        growlSoundPacket.receiveOnClient { packet, context ->
            if (context.client.player?.isHulk == true) {
                MinecraftClient.getInstance().soundManager.play(
                    PositionedSoundInstance.master(
                        SoundRegistry.getRandomGrowlSound(),
                        1f,
                        1f
                    )
                )
            }
        }
        jumpPacket.receiveOnServer(::onJumpPacket)
        hulkTransformPacket.receiveOnServer(::onHulkTransform)
        forceBreakBlock.receiveOnServer(::onForceBlockBreak)
        thunderClapPacket.receiveOnServer(::onThunderClap)
        punchPacket.receiveOnServer(::onPunch)
        UseEntityCallback.EVENT.register(UseEntityCallback { player, world, hand, entity, _ ->
            if (!world.isClient && hand == Hand.MAIN_HAND && player.isHulk) {
                entity?.startRiding(player)
                growlSoundPacket.send(Unit, player as ServerPlayerEntity)
                return@UseEntityCallback ActionResult.SUCCESS
            }
            return@UseEntityCallback ActionResult.PASS
        })
    }

    fun ServerPlayerEntity.throwPassengers() {
        if (this.isHulk && hasPassengers()) {
            growlSoundPacket.send(Unit, this)
            val passengers = passengerList.toList()
            removeAllPassengers()
            for (passenger in passengers) {
                passenger.modifyVelocity(directionVector.normalize().multiply(3.5))
                flyingEntities += passenger.uuid
            }
        }
    }

    @OptIn(ExperimentalSilkApi::class)
    private fun onPunch(startBlockPos: SimpleIntPos, context: ServerPacketContext) {
        val player = context.player
        for (blockPos in HulkUtils.generateSphere(startBlockPos.toMcBlockPos(), 3, false)) {
            player.world.breakBlock(blockPos, Random.nextBoolean(), player)
        }
        for (entity in player.world.getOtherEntities(
            player,
            Box.from(startBlockPos.toMcBlockPos().toCenterPos()).expand(5.0)
        )) {
            entity.damage(player.world.damageSources.playerAttack(player), Random.nextDouble(1.0, 3.0).toFloat())
            entity.modifyVelocity(
                entity.velocity.addRandom(
                    Random.asMinecraftRandom(),
                    Random.nextDouble(1.1, 3.0).toFloat()
                )
            )
        }
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
        //TODO somehow it stopped working so just to be sure
        context.player.networkHandler.sendPacket(
            BlockUpdateS2CPacket(
                blockPos,
                context.player.world.getBlockState(blockPos)
            )
        )
    }

    private fun onHulkTransform(unit: Unit, context: ServerPacketContext) {
        context.player.isHulk = !context.player.isHulk
        if (context.player.isHulk) {
            context.player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.baseValue = 0.188
            context.player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.baseValue = 60.0
        } else {
            context.player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.baseValue = 20.0
            context.player.damage(context.player.world.damageSources.generic(), 0.1f)
            context.player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.baseValue =
                0.10000000149011612
        }
    }

    private fun onJumpPacket(jumpStrength: Double, context: ServerPacketContext) {
        val player = context.player
        val startPos = context.player.posUnder
        val direction = context.player.directionVector.normalize().multiply(jumpStrength)
        context.player.modifyVelocity(direction)
    }
}
