package gg.norisk.hulk.common.registry

import gg.norisk.hulk.common.ManagerCommon.toId
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent

object SoundRegistry {
    var JUMP = Registry.register(Registries.SOUND_EVENT, "jump".toId(), SoundEvent.of("jump".toId()))
    var SCREAM = Registry.register(Registries.SOUND_EVENT, "scream".toId(), SoundEvent.of("scream".toId()))
    var BREATH = Registry.register(Registries.SOUND_EVENT, "breath".toId(), SoundEvent.of("breath".toId()))
    var IMPACT = Registry.register(Registries.SOUND_EVENT, "impact".toId(), SoundEvent.of("impact".toId()))
    var CLAP = Registry.register(Registries.SOUND_EVENT, "clap".toId(), SoundEvent.of("clap".toId()))
    var BOOM = Registry.register(Registries.SOUND_EVENT, "boom".toId(), SoundEvent.of("boom".toId()))
    var CRACK = Registry.register(Registries.SOUND_EVENT, "crack".toId(), SoundEvent.of("crack".toId()))
    var PUNCH = Registry.register(Registries.SOUND_EVENT, "punch".toId(), SoundEvent.of("punch".toId()))
    var STEPSOUND = Registry.register(Registries.SOUND_EVENT, "step_sound".toId(), SoundEvent.of("step_sound".toId()))

    var GROWLS = buildList {
        repeat(6) {
            val index = it + 1
            add(Registry.register(Registries.SOUND_EVENT, "growl_$index".toId(), SoundEvent.of("growl_$index".toId())))
        }
    }

    fun init() {
    }

    fun getRandomGrowlSound(): SoundEvent {
        return GROWLS.random()
    }
}
