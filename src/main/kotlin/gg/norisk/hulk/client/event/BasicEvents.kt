package gg.norisk.hulk.client.event

import net.minecraft.client.MinecraftClient
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.event.Event

open class KeyEvent(val key: Int, val scanCode: Int, val action: Int, val client: MinecraftClient) {
    override fun toString(): String {
        return "KeyEvent(key=$key, scanCode=$scanCode, action=$action)"
    }
}

@OptIn(ExperimentalSilkApi::class)
val keyEvent = Event.onlySyncImmutable<KeyEvent>()
