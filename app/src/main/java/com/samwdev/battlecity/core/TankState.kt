package com.samwdev.battlecity.core

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.entity.BotTankLevel
import com.samwdev.battlecity.ui.components.mu
import java.util.concurrent.atomic.AtomicInteger

@Composable
fun rememberTankState(): TankState {
    return rememberSaveable(saver = TankState.Saver()) {
        TankState()
    }
}

private val playerSpawnPosition = Offset(4.5f, 12f)

class TankState(initial: Map<TankId, Tank> = mapOf()) {
    companion object {
        // todo to confirm this works as expected
        fun Saver() = Saver<TankState, Map<TankId, Tank>>(
            save = { it.tanks },
            restore = { TankState(it) }
        )

    }
    var tanks by mutableStateOf<Map<TankId, Tank>>(initial, policy = referentialEqualityPolicy())
        private set

    private var nextId = AtomicInteger(0)

    private fun addTank(id: TankId, tank: Tank) {
        tanks = tanks.toMutableMap().apply {
            put(id, tank)
        }
    }

    fun spawnPlayer(): Tank {
        return Tank(
            id = nextId.incrementAndGet(),
            x = playerSpawnPosition.x,
            y = playerSpawnPosition.y,
            direction = Direction.Up,
        ).also { addTank(nextId.get(), it) }
    }

    fun getTank(id: TankId): Tank? {
        return tanks[id]
    }
}

typealias TankId = Int

class Tank(
    val id: TankId,
    x: Float = 0f,
    y: Float = 0f,
    direction: Direction = Direction.Up,
    level: BotTankLevel = BotTankLevel.Basic,
    hp: Int = 1,
    val speed: Float = 0.01f,
) : Parcelable {
    var x: Float by mutableStateOf(x)
    var y: Float by mutableStateOf(y)
    var direction: Direction by mutableStateOf(direction)

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readFloat(),
    )

    fun getBulletStartPosition(): DpOffset {
        return DpOffset(x.dp, y.dp)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeFloat(x)
        parcel.writeFloat(y)
        // todo
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tank> {
        override fun createFromParcel(parcel: Parcel): Tank {
            return Tank(parcel)
        }

        override fun newArray(size: Int): Array<Tank?> {
            return arrayOfNulls(size)
        }
    }
}

enum class Direction(val degree: Float) {
    Up(0f),
    Down(180f),
    Left(270f),
    Right(90f),
    Unspecified(Float.NaN)
}

@Composable
fun Tank(tank: Tank) {
    Canvas(
        modifier = Modifier
            .size(1.mu, 1.mu)
            .offset(tank.x.mu, tank.y.mu)
            .rotate(tank.direction.degree)
    ) {
        drawRect(
            color = Color.Yellow,
            topLeft = Offset(0f, size.height / 5f),
//            alpha = (tickState.uptimeMillis % 1500) / 3000f + 0.5f
        )
        drawRect(
            color = Color.Yellow,
            topLeft = Offset(size.width / 2 - 24 / 2, 0f),
            size = Size(24f, size.height / 2)
        )
    }
}