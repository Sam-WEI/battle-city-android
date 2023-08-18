package com.samwdev.battlecity.core.state

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import com.samwdev.battlecity.core.TickListener
import com.samwdev.battlecity.ui.component.ExplosionUiPattern
import java.util.concurrent.atomic.AtomicInteger

typealias ExplosionId = Int

class ExplosionState : TickListener() {
    private val idGen = AtomicInteger()

    var explosions by mutableStateOf<Map<ExplosionId, Explosion>>(mapOf())
        private set

    override fun onTick(tick: Tick) {
        explosions = explosions.filter { (_, exp) -> !exp.complete }
            .mapValues { (_, exp) -> exp.copy(progress = exp.progress + tick.delta) }
    }

    fun spawnExplosion(center: Offset, animation: ExplosionAnimation) {
        explosions = explosions.toMutableMap().apply {
            put(idGen.incrementAndGet(), Explosion(
                id = idGen.get(),
                centerOffset = center,
                animation,
            ))
        }
    }
}

data class Explosion(
    val id: ExplosionId,
    val centerOffset: Offset,
    val animation: ExplosionAnimation,
    val progress: Long = 0,
) {
    val duration: Int get() = animation.frames.sumOf { it.duration }
    val currFrameIndex: Int get() {
        var acc = 0
        for ((i, f) in animation.frames.withIndex()) {
            acc += f.duration
            if (progress < acc) { return i }
        }
        return Complete
    }
    val currFrameUiPattern: ExplosionUiPattern get() {
        val currFrameI = currFrameIndex
        return if (currFrameI == Complete) {
            animation.frames.last().pattern
        } else {
            animation.frames[currFrameI].pattern
        }
    }
    val complete: Boolean get() = currFrameIndex == Complete
    companion object {
        const val Complete = -1
    }
}

data class ExplosionFrame(val duration: Int, val pattern: ExplosionUiPattern)

data class ExplosionAnimation(val frames: List<ExplosionFrame>)

val ExplosionAnimationBig = ExplosionAnimation(listOf(
    ExplosionFrame(80, ExplosionUiPattern.Small0),
    ExplosionFrame(80, ExplosionUiPattern.Small1),
    ExplosionFrame(80, ExplosionUiPattern.Small2),
    ExplosionFrame(80, ExplosionUiPattern.Large0),
    ExplosionFrame(80, ExplosionUiPattern.Large1),
    ExplosionFrame(80, ExplosionUiPattern.Small2),
))

val ExplosionAnimationSmall = ExplosionAnimation(listOf(
    ExplosionFrame(50, ExplosionUiPattern.Small0),
    ExplosionFrame(50, ExplosionUiPattern.Small1),
    ExplosionFrame(50, ExplosionUiPattern.Small2),
))