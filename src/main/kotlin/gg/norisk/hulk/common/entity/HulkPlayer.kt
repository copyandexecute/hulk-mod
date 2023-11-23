package gg.norisk.hulk.common.entity

import net.minecraft.block.BlockState
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity

interface IHulkPlayer {
    var getCustomAttackReachDistance: Double
    var getCustomCreativeAttackReachDistance: Double
    var getCustomBlockReachDistance: Float
    var getCustomCreativeBlockReachDistance: Float
    var blockStateInHand: BlockState?
}

val hulkTracker: TrackedData<Boolean> =
    DataTracker.registerData(PlayerEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
var PlayerEntity.isHulk: Boolean
    get() = this.dataTracker.get(hulkTracker)
    set(value) = this.dataTracker.set(hulkTracker, value)
