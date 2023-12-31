package gg.norisk.hulk.client.abilities

import gg.norisk.hulk.client.event.KeyEvent
import gg.norisk.hulk.client.event.keyEvent
import gg.norisk.hulk.common.network.NetworkManager
import gg.norisk.hulk.common.registry.SoundRegistry
import gg.norisk.hulk.common.utils.CameraShaker
import gg.norisk.hulk.common.utils.HulkUtils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.entity.posUnder
import org.lwjgl.glfw.GLFW
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object JumpManager : ClientTickEvents.EndTick, HudRenderCallback {
    private val ICONS = Identifier("textures/gui/icons.png")
    val jumpKey = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.hulk.boost", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_V, // The keycode of the key
            "category.hulk.abilities" // The translation key of the keybinding's category.
        )
    )
    var isCharging = false
    var jumpStrength = 0.0
    private var isFlying = false
    private const val MAX_JUMP_STRENGTH = 10.0
    var flyTick = 0

    @OptIn(ExperimentalSilkApi::class)
    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register(this)
        HudRenderCallback.EVENT.register(this)
        keyEvent.listen { onKeyEvent(it) }
    }

    private fun onKeyEvent(event: KeyEvent) {
        val isJumpKey = jumpKey.matchesKey(event.key, event.scanCode)
        if (isJumpKey && event.action == 2 && !isCharging) {
            isCharging = true
        } else if (isJumpKey && event.action == 0 && isCharging) {
            isCharging = false
            isFlying = true
            NetworkManager.jumpPacket.send(jumpStrength)
            handleCracks()
            jumpStrength = 0.0
        }
    }

    private fun handleCracks() {
        val player = MinecraftClient.getInstance().player ?: return

        CameraShaker.addEvent(CameraShaker.BoomShake(0.1 * jumpStrength, 0.1, 0.3))

        val startPos = player.posUnder
        MinecraftClient.getInstance().soundManager.play(
            PositionedSoundInstance.master(
                SoundRegistry.JUMP,
                1.0f,
                Random.nextDouble(0.7, 1.3).toFloat()
            )
        )
        val radius = jumpStrength.toInt() * 4
        createCracks(player, startPos, radius)
    }

    private fun createCracks(player: PlayerEntity, startPos: BlockPos, radius: Int) {
        for (blockPos in HulkUtils.generateSphere(startPos, radius, false)) {
            val state = player.world.getBlockState(blockPos)
            if (state.isAir) continue
            if (Direction.values().all { state.isSideInvisible(state, it) }) continue
            if (Random.nextInt(100) > 70) continue

            // Berechne die Distanz zwischen startPos und blockPos
            val distance = startPos.getSquaredDistance(blockPos)
            // Berechne den Prozentsatz der maximalen Distanz
            val distancePercentage = 1.0 - (distance / radius)
            // Skaliere die progressStrength basierend auf dem Prozentsatz
            var progressStrength = (distancePercentage * 10).toInt()
            // Füge einen zufälligen Wert zwischen -2 und 2 hinzu
            val randomBonus = Random.nextInt(-3, 4)
            progressStrength = min(9, max(-1, progressStrength + randomBonus))

            if (Random.nextBoolean()) {
                player.world.playSoundAtBlockCenter(
                    blockPos,
                    state.soundGroup.breakSound,
                    SoundCategory.BLOCKS,
                    0.3f,
                    Random.nextDouble(0.8, 1.0).toFloat(),
                    true
                )
            }
            //player.world.addBlockBreakParticles(blockPos,state)
            player.world.setBlockBreakingInfo(Random.nextInt(), blockPos, progressStrength)
        }
    }

    override fun onEndTick(client: MinecraftClient) {
        if (isCharging) {
            jumpStrength = min(jumpStrength + 0.2, MAX_JUMP_STRENGTH)
        }
        val player = client.player ?: return
        if (isFlying) {
            flyTick++
            if (player.isOnGround && flyTick > 5) {
                isFlying = false
                flyTick = 0
                handleLanding()
            }
        }
    }

    private fun handleLanding() {
        CameraShaker.addEvent(CameraShaker.BoomShake(Random.nextDouble(0.3, 0.5), 0.1, 0.3))

        MinecraftClient.getInstance().soundManager.play(
            PositionedSoundInstance.master(
                SoundRegistry.JUMP,
                1.0f,
                Random.nextDouble(0.5, 1.3).toFloat()
            )
        )

        val player = MinecraftClient.getInstance().player ?: return
        createCracks(player, player.posUnder, Random.nextInt(6, 14))
    }

    override fun onHudRender(drawContext: DrawContext, tickDelta: Float) {
        renderJumpStrengthBar(drawContext)
    }

    fun renderJumpStrengthBar(drawContext: DrawContext) {
        val client = MinecraftClient.getInstance()
        val i = client.window.scaledWidth / 2 - 91
        var l: Int
        var m: Int
        if (jumpStrength > 0.0) {
            l = (jumpStrength / MAX_JUMP_STRENGTH * 183.0f).toInt()
            m = client.window.scaledHeight - 32 + 3
            drawContext.drawTexture(ICONS, i, m, 0, 64, 182, 5)
            if (l > 0) {
                drawContext.drawTexture(ICONS, i, m, 0, 69, l, 5)
            }
        }
        if (jumpStrength > 0.0) {
            val percentage = (jumpStrength * MAX_JUMP_STRENGTH.toInt()).toInt()
            val string = "" + percentage
            l = (client.window.scaledWidth - client.textRenderer.getWidth(string)) / 2
            m = client.window.scaledHeight - 31 - 4
            drawContext.drawText(client.textRenderer, string, l + 1, m, 0, false)
            drawContext.drawText(client.textRenderer, string, l - 1, m, 0, false)
            drawContext.drawText(client.textRenderer, string, l, m + 1, 0, false)
            drawContext.drawText(client.textRenderer, string, l, m - 1, 0, false)
            drawContext.drawText(client.textRenderer, string, l, m, 8453920, false)
        }
    }
}
