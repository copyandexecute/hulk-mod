package gg.norisk.hulk.client.abilities

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry
import gg.norisk.hulk.client.event.KeyEvent
import gg.norisk.hulk.client.event.keyEvent
import gg.norisk.hulk.client.player.IAnimatedPlayer
import gg.norisk.hulk.client.renderer.NamedKeyframeAnimationPlayer
import gg.norisk.hulk.common.ManagerCommon.toId
import gg.norisk.hulk.common.network.NetworkManager
import gg.norisk.hulk.common.registry.SoundRegistry
import gg.norisk.hulk.common.utils.CameraShaker
import gg.norisk.hulk.common.utils.HulkUtils
import gg.norisk.hulk.common.utils.SimpleIntPos
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.entity.directionVector
import net.silkmc.silk.core.task.mcCoroutineTask
import org.lwjgl.glfw.GLFW
import java.time.Duration
import kotlin.math.min
import kotlin.random.Random

object ThunderClap : ClientTickEvents.StartTick {
    val thunderClapKey = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.hulk.thunderclap", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_G, // The keycode of the key
            "category.hulk.abilities" // The translation key of the keybinding's category.
        )
    )

    private val blockBreakingInfos: Cache<BlockPos, Pair<Int, Int>> =
        CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(1)).build()

    @OptIn(ExperimentalSilkApi::class)
    fun init() {
        keyEvent.listen { onKeyEvent(it) }
        ClientTickEvents.START_CLIENT_TICK.register(this)
    }

    private fun onKeyEvent(event: KeyEvent) {
        val isThunderClapKey = thunderClapKey.matchesKey(event.key, event.scanCode)
        if (isThunderClapKey && event.action == 1) {
            val player = MinecraftClient.getInstance().player ?: return
            val animationContainer = (player as IAnimatedPlayer).hulk_getModAnimation()
            var anim = PlayerAnimationRegistry.getAnimation("thunderclap".toId())
                ?: error("No transformation animation ${"thunderclap".toId()}")

            // Requested API, disable parts of animation.
            // Following code disables the left leg (since API 0.4.0)
            val builder = anim.mutableCopy();
            builder.getPart("leftLeg")?.isEnabled = false
            builder.getPart("rightLeg")?.isEnabled = false
            builder.getPart("head")?.isEnabled = false
            // done modifying rules
            anim = builder.build();

            animationContainer.animation = NamedKeyframeAnimationPlayer("thunderclap", anim)
        }
    }

    private fun createShockWave(player: PlayerEntity) {
        val startEyePos = player.eyePos
        val direction = player.directionVector.normalize()
        val startPos = player.pos.add(0.0, 0.3, 0.0).add(direction.multiply(2.0))

        mcCoroutineTask(client = true, howOften = 20) { task ->
            val center = startPos.add(direction.multiply(task.round.toDouble()))
            val centerEyePos = startEyePos.add(direction.multiply(task.round.toDouble()))
            val centerBlockPos = BlockPos(center.x.toInt(), center.y.toInt(), center.z.toInt())
            player.world.addParticle(
                ParticleTypes.EXPLOSION,
                centerEyePos.x,
                centerEyePos.y,
                centerEyePos.z,
                5.0,
                0.0,
                0.0
            )
            for (blockPos in HulkUtils.generateSphere(centerBlockPos, 3, false)) {
                val state = player.world.getBlockState(blockPos)
                if (state.isAir) continue
                if (Direction.values().all { state.isSideInvisible(state, it) }) continue

                val cachedDamage = blockBreakingInfos.getIfPresent(blockPos) ?: Pair(Random.nextInt(), 0)

                val damage = if (cachedDamage.second == 0) {
                    Random.nextInt(1, 5)
                } else {
                    min(10, cachedDamage.second + Random.nextInt(1, 3))
                }

                if (damage == 10) {
                    NetworkManager.forceBreakBlock.send(SimpleIntPos(blockPos.x, blockPos.y, blockPos.z))
                } else {
                    if (Random.nextInt(100) > 95) {
                        player.world.playSoundAtBlockCenter(
                            blockPos,
                            SoundRegistry.CRACK,
                            SoundCategory.BLOCKS,
                            1f,
                            Random.nextDouble(0.6, 1.0).toFloat(),
                            false
                        )
                    }
                }

                blockBreakingInfos.put(blockPos, Pair(cachedDamage.first, damage))
                player.world.setBlockBreakingInfo(cachedDamage.first, blockPos, damage)
            }
        }
    }

    override fun onStartTick(client: MinecraftClient) {
        val player = client.player ?: return
        val animationContainer = (player as IAnimatedPlayer).hulk_getModAnimation()

        val keyFrameAnimation = animationContainer.animation as? NamedKeyframeAnimationPlayer? ?: return
        if (keyFrameAnimation.isActive && keyFrameAnimation.name == "thunderclap") {
            if (keyFrameAnimation.currentTick == 7) {

                CameraShaker.addEvent(CameraShaker.BoomShake(1.0, 0.1, 0.3))
                createShockWave(player)
                NetworkManager.thunderClapPacket.send(Unit)

                MinecraftClient.getInstance().soundManager.play(
                    PositionedSoundInstance.master(
                        SoundRegistry.getRandomGrowlSound(),
                        1f,
                        1f
                    )
                )

                MinecraftClient.getInstance().soundManager.play(
                    PositionedSoundInstance.master(
                        SoundRegistry.BOOM,
                        0.5f,
                        1f
                    )
                )
                MinecraftClient.getInstance().soundManager.play(
                    PositionedSoundInstance.master(
                        SoundRegistry.CLAP,
                        0.3f,
                        1f
                    )
                )
            }
        }
    }
}