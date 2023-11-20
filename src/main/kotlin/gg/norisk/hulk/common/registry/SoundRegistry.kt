package gg.norisk.hulk.common.registry

import gg.norisk.hulk.common.ManagerCommon.toId
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent

object SoundRegistry {
    var JUMP = Registry.register(Registries.SOUND_EVENT, "jump".toId(), SoundEvent.of("jump".toId()))
    var SCREAM = Registry.register(Registries.SOUND_EVENT, "scream".toId(), SoundEvent.of("scream".toId()))
    var BREATH = Registry.register(Registries.SOUND_EVENT, "breath".toId(), SoundEvent.of("breath".toId()))

    fun init() {
    }
}
