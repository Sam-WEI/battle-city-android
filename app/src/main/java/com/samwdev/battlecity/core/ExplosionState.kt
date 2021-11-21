package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import com.samwdev.battlecity.ui.components.ExplosionUiPattern
import java.util.concurrent.atomic.AtomicInteger


@Composable
fun rememberExplosionState(): ExplosionState {
    return remember {
        ExplosionState()
    }
}

typealias ExplosionId = Int

class ExplosionState : TickListener {
    private val nextId = AtomicInteger()

    var explosions by mutableStateOf<Map<ExplosionId, Explosion>>(mapOf())
        private set

    override fun onTick(tick: Tick) {
        val toUpdate = mutableListOf<Explosion>()
        val toRemove = mutableListOf<Explosion>()
        explosions.forEach { (_, exp) ->
            if (exp.complete) {
                toRemove += exp
            } else {
                toUpdate += exp.copy(progress = exp.progress + tick.delta)
            }
        }
        if (toUpdate.isNotEmpty() || toRemove.isNotEmpty()) {
            val new = explosions.toMutableMap()
            for (exp in toRemove) {
                new.remove(exp.id)
            }
            for (exp in toUpdate) {
                new[exp.id] = exp
            }
            explosions = new
        }
    }

    fun spawnExplosion(center: Offset, animation: ExplosionAnimation) {
        explosions = explosions.toMutableMap().apply {
            put(nextId.incrementAndGet(), Explosion(
                id = nextId.get(),
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