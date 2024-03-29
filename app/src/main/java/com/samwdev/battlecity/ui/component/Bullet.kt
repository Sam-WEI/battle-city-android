package com.samwdev.battlecity.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import com.samwdev.battlecity.core.BULLET_COLLISION_SIZE
import com.samwdev.battlecity.core.Bullet
import com.samwdev.battlecity.core.cell2mpx

private val BulletColor = Color(0xFFADADAD)

@Composable
fun Bullets(bullets: Collection<Bullet>) {
    val (hGridSize, vGridSize) = LocalGridSize.current.first
    PixelCanvas(
        widthInMapPixel = hGridSize.cell2mpx,
        heightInMapPixel = vGridSize.cell2mpx
    ) {
        bullets.forEach { bullet ->
            translate(bullet.x, bullet.y) {
                drawForDirection(direction = bullet.direction, pivot = Offset(1f, 1f)) {
                    this as PixelDrawScope
                    // bullet body
                    drawRect(
                        color = BulletColor,
                        topLeft = Offset(0f, 0f),
                        size = Size(BULLET_COLLISION_SIZE, BULLET_COLLISION_SIZE),
                    )
                    // bullet tip
                    drawPixel(color = BulletColor, topLeft = Offset(1f, -1f))
                }
            }
        }
    }
}