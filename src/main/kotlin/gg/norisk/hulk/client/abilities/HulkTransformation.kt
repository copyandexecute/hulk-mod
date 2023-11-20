package gg.norisk.hulk.client.abilities

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry
import gg.norisk.hulk.client.event.KeyEvent
import gg.norisk.hulk.client.event.keyEvent
import gg.norisk.hulk.client.player.IAnimatedPlayer
import gg.norisk.hulk.common.ManagerCommon.toId
import gg.norisk.hulk.common.entity.isHulk
import gg.norisk.hulk.common.network.NetworkManager
import gg.norisk.hulk.common.registry.SoundRegistry
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.InputUtil
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.text.literal
import org.lwjgl.glfw.GLFW
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object HulkTransformation : ClientTickEvents.StartTick {
    val hulkTexture = "textures/hulk_skin.png".toId()
    val transformationKey = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.hulk.transformation", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_RIGHT_SHIFT, // The keycode of the key
            "category.hulk.abilities" // The translation key of the keybinding's category.
        )
    )

    const val defaultSize = 0.9375f
    const val hulkSize = 1.3f
    var currentSize = defaultSize
    var useHulkSkin = false
    var currentTick = 0

    @OptIn(ExperimentalSilkApi::class)
    fun init() {
        keyEvent.listen { onKeyEvent(it) }
        ClientTickEvents.START_CLIENT_TICK.register(this)
    }

    fun handleTransformation(player: AbstractClientPlayerEntity) {
        if (player.isHulk) {
            val animationContainer = (player as IAnimatedPlayer).hulk_getModAnimation()
            var anim = PlayerAnimationRegistry.getAnimation("transformation".toId())
                ?: error("No transformation animation ${"transformation".toId()}")

            // Requested API, disable parts of animation.
            // Following code disables the left leg (since API 0.4.0)
            val builder = anim.mutableCopy();
            val leftLeg = builder.getPart("leftLeg")
            leftLeg?.setEnabled(false);
            val rightLeg = builder.getPart("rightLeg")
            rightLeg?.setEnabled(false);
            // done modifying rules
            anim = builder.build();

            animationContainer.animation = KeyframeAnimationPlayer(anim).setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL)
        }
    }

    private fun onKeyEvent(event: KeyEvent) {
        if (transformationKey.matchesKey(event.key, event.scanCode) && event.action == 1) {
            NetworkManager.hulkTransformPacket.send(Unit)
        }
    }

    override fun onStartTick(client: MinecraftClient) {
        val player = client.player ?: return
        val animationContainer = (player as IAnimatedPlayer).hulk_getModAnimation()

        if (!player.isHulk) {
            currentSize = max(defaultSize, currentSize - 0.01f)
        }

        val keyFrameAnimation = animationContainer.animation as? KeyframeAnimationPlayer? ?: return
        if (keyFrameAnimation.isActive) {

            if (keyFrameAnimation.currentTick == 0) {
                MinecraftClient.getInstance().soundManager.play(
                    PositionedSoundInstance.master(
                        SoundRegistry.BREATH,
                        0.8f,
                        0.7f
                    )
                )
            }

            if (keyFrameAnimation.currentTick <= 60) {
                currentTick++
                if (currentTick == 5) useHulkSkin = false
                if (keyFrameAnimation.currentTick.mod(10) == 0) {
                    useHulkSkin = true
                    currentTick = 0
                }
            } else {
                useHulkSkin = true
            }

            if (player.isHulk) {
                currentSize = min(hulkSize, currentSize + 0.01f)
            }
            // Lerp zwischen currentSize und hulkSize

            if (keyFrameAnimation.currentTick == 60) {
                MinecraftClient.getInstance().soundManager.play(
                    PositionedSoundInstance.master(
                        SoundRegistry.SCREAM,
                        1.0f,
                        Random.nextDouble(0.7, 1.3).toFloat()
                    )
                )
            }
        }
    }
}
