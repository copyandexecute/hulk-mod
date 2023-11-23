package gg.norisk.hulk.client.abilities

import gg.norisk.hulk.common.entity.isHulk
import gg.norisk.hulk.common.network.NetworkManager.punchPacket
import gg.norisk.hulk.common.registry.SoundRegistry
import gg.norisk.hulk.common.utils.HulkUtils
import gg.norisk.hulk.common.utils.SimpleIntPos
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import kotlin.random.Random

object Punch {
    fun init() {
    }
    fun onAttack(startBlockPos: BlockPos) {
        val player = MinecraftClient.getInstance().player ?: return
        if (MinecraftClient.getInstance().player!!.isHulk) {
            MinecraftClient.getInstance().soundManager.play(
                PositionedSoundInstance.master(
                    SoundRegistry.getRandomGrowlSound(),
                    1f,
                    1f
                )
            )
            punchPacket.send(SimpleIntPos(startBlockPos.x, startBlockPos.y, startBlockPos.z))
            MinecraftClient.getInstance().soundManager.play(
                PositionedSoundInstance.master(
                    SoundRegistry.BOOM,
                    1f,
                    5f
                )
            )
            for (blockPos in HulkUtils.generateSphere(startBlockPos, 4, false)) {
                val state = player.world.getBlockState(blockPos)
                if (state.isAir) continue
                if (Direction.values().all { state.isSideInvisible(state, it) }) continue

                val damage = Random.nextInt(1, 10)

                if (Random.nextInt(100) > 60) continue
                if (Random.nextInt(100) > 90) {
                    player.world.playSoundAtBlockCenter(
                        blockPos,
                        SoundRegistry.CRACK,
                        SoundCategory.BLOCKS,
                        1f,
                        Random.nextDouble(0.6, 1.0).toFloat(),
                        false
                    )
                }

                player.world.setBlockBreakingInfo(Random.nextInt(), blockPos, damage)
            }
        }
    }
}