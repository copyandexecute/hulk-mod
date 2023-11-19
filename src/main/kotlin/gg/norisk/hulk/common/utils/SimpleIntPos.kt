package gg.norisk.hulk.common.utils

import kotlinx.serialization.Serializable
import net.minecraft.util.math.BlockPos

@Serializable
data class SimpleIntPos(val x: Int, val y: Int, val z: Int) {
    fun toMcBlockPos() = BlockPos(x.toInt(), y.toInt(), z.toInt())
}

