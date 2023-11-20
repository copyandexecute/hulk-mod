package gg.norisk.hulk.common.entity

import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity

val hulkTracker: TrackedData<Boolean> =
    DataTracker.registerData(PlayerEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)

var PlayerEntity.isHulk: Boolean
    get() = this.dataTracker.get(hulkTracker)
    set(value) = this.dataTracker.set(hulkTracker, value)
