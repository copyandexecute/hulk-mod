package gg.norisk.example.client.player

import dev.kosmx.playerAnim.api.layered.IAnimation
import dev.kosmx.playerAnim.api.layered.ModifierLayer

interface IAnimatedPlayer {
    fun example_getModAnimation(): ModifierLayer<IAnimation>
}
