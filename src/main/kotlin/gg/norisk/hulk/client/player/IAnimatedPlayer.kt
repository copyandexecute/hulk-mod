package gg.norisk.hulk.client.player

import dev.kosmx.playerAnim.api.layered.IAnimation
import dev.kosmx.playerAnim.api.layered.ModifierLayer

interface IAnimatedPlayer {
    fun hulk_getModAnimation(): ModifierLayer<IAnimation>
}
