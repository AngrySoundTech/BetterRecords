package tech.feldman.betterrecords.api.event

import tech.feldman.betterrecords.api.sound.Sound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.Event

data class RadioInsertEvent(
        val pos: BlockPos,
        val dimension: Int,
        val playRadius: Float,
        val sound: Sound
) : Event()
