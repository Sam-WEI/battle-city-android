package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.core.BULLET_COLLISION_SIZE
import com.samwdev.battlecity.core.Bullet

private val BulletColor = Color(0xFFADADAD)

@Composable
fun Bullet(bullet: Bullet) {
    val oneMapPixel = 1f.mpx2dp
    Canvas(modifier = Modifier
        .size(BULLET_COLLISION_SIZE.mpx2dp, BULLET_COLLISION_SIZE.mpx2dp)
        .offset(bullet.x.mpx2dp, bullet.y.mpx2dp)
        .rotate(bullet.direction.degree.toFloat())
    ) {
        // bullet body
        drawRect(
            color = BulletColor,
            topLeft = Offset.Zero,
            size = size,
        )
        // bullet tip
        drawRect(
            color = BulletColor,
            topLeft = Offset(1f * oneMapPixel.toPx(), -1f * oneMapPixel.toPx()),
            size = Size(oneMapPixel.toPx(), oneMapPixel.toPx())
        )
    }
}