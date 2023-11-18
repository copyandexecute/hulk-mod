package gg.norisk.example.client.event

import net.minecraft.client.MinecraftClient
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.event.Event

open class KeyEvent(val key: Int, val scanCode: Int, val client: MinecraftClient)

@OptIn(ExperimentalSilkApi::class)
val onKeyPressedOnce = Event.onlySyncImmutable<KeyEvent>()
