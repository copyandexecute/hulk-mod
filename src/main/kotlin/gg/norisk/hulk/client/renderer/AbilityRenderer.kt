package gg.norisk.hulk.client.renderer

import gg.norisk.hulk.client.abilities.HulkTransformation
import gg.norisk.hulk.client.abilities.JumpManager
import gg.norisk.hulk.client.abilities.ThunderClap
import gg.norisk.hulk.common.entity.isHulk
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.render.RenderLayer
import net.minecraft.text.Text
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.world.pos.Pos2i

object AbilityRenderer : HudRenderCallback {
    enum class Abilities(
        val keybinding: KeyBinding,
        val description: String,
        val shouldRender: (() -> Boolean) = { true },
        val hold: Boolean = false,
    ) {
        TRANSFORMATION(HulkTransformation.transformationKey, "Transformation"),
        BOOST(JumpManager.jumpKey, "Boost", {
            MinecraftClient.getInstance().player?.isHulk == true
        }, hold = true),
        THUNDERCLAP(ThunderClap.thunderClapKey, "Thunderclap", {
            MinecraftClient.getInstance().player?.isHulk == true
        }),
        BLOCKSMASH(MinecraftClient.getInstance().options.attackKey, "Block Smash", {
            MinecraftClient.getInstance().player?.isHulk == true
        }),
        ENTITYSMASH(MinecraftClient.getInstance().options.attackKey, "Entity Smash", {
            MinecraftClient.getInstance().player?.isHulk == true
        }),
    }

    fun init() {
        HudRenderCallback.EVENT.register(this)
    }

    override fun onHudRender(drawContext: DrawContext, tickDelta: Float) {
        val offset = 2
        Abilities.values().filter { it.shouldRender() }.forEachIndexed { index, ability ->
            val text = literalText {
                if (ability.hold) {
                    text("Hold ") { color = 0x47CD45 }
                }
                text(ability.keybinding.boundKeyLocalizedText) { color = 0x47CD45 }
                text(" - ") { color = 0x919191 }
                text(ability.description)
            }
            val pos = Pos2i(5, 5 + (text.height + offset * 2) * index)
            drawContext.fill(
                RenderLayer.getGuiOverlay(),
                pos.x - offset,
                pos.z - offset,
                pos.x + text.width + offset,
                pos.z + text.height + offset,
                -1873784752
            )
            drawContext.drawText(
                MinecraftClient.getInstance().textRenderer,
                text,
                pos.x,
                pos.z,
                14737632,
                true
            )
        }
    }

    val Text.width
        get() = MinecraftClient.getInstance().textRenderer.getWidth(this)

    val Text.height
        get() = MinecraftClient.getInstance().textRenderer.fontHeight
}