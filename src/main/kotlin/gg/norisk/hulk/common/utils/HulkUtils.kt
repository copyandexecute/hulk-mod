package gg.norisk.hulk.common.utils

import gg.norisk.hulk.common.registry.SoundRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.silkmc.silk.core.entity.directionVector
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.math.geometry.Sphere
import net.silkmc.silk.core.task.mcCoroutineTask


object HulkUtils {
    fun generateSphere(centerBlock: BlockPos, radius: Int, hollow: Boolean): List<BlockPos> {
        val circleBlocks: MutableList<BlockPos> = ArrayList<BlockPos>()
        val bx: Int = centerBlock.x
        val by: Int = centerBlock.y
        val bz: Int = centerBlock.z
        for (x in bx - radius..bx + radius) {
            for (y in by - radius..by + radius) {
                for (z in bz - radius..bz + radius) {
                    val distance = ((bx - x) * (bx - x) + (bz - z) * (bz - z) + (by - y) * (by - y)).toDouble()
                    if (distance < radius * radius && !(hollow && distance < (radius - 1) * (radius - 1))) {
                        circleBlocks.add(BlockPos(x, y, z))
                    }
                }
            }
        }
        return circleBlocks
    }

    fun smashEntity(player: PlayerEntity, entity: Entity) {
        if (player is AbstractClientPlayerEntity) {
            MinecraftClient.getInstance().soundManager.play(
                PositionedSoundInstance.master(
                    SoundRegistry.BOOM,
                    1f,
                    5f
                )
            )
        } else if (player is ServerPlayerEntity) {
            entity.modifyVelocity(player.directionVector.normalize().multiply(5.0))
            mcCoroutineTask(howOften = 10, client = false) {
                for (blockPos in generateSphere(entity.blockPos, 3, false)) {
                    val blockState = player.world.getBlockState(blockPos)
                    if (blockState.isAir) continue
                    player.world.breakBlock(blockPos, false, player)
                }
            }
        }
    }
}
