package gg.norisk.hulk.common.registry

import gg.norisk.hulk.common.ManagerCommon.toId
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent

object SoundRegistry {
    var JUMP = Registry.register(Registries.SOUND_EVENT, "jump".toId(), SoundEvent.of("jump".toId()))

    fun init() {
    }
}
