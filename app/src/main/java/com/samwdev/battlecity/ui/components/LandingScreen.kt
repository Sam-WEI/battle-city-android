package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.Direction
import com.samwdev.battlecity.core.MapPixel
import com.samwdev.battlecity.core.grid2mpx

private const val gridUnitNum = 16

@Composable
fun LandingScreen() {
    var selectionIndex: Int by remember { mutableStateOf(0) }
    Grid(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Black),
        gridUnitNum = gridUnitNum,
    ) {
        PixelText(text = "I-    00 HI- 20000", charHeight = 0.5f.grid2mpx, topLeft = Offset(1f.grid2mpx, 1f.grid2mpx))

        BrickTitle("BATTLE", "CITY",
            modifier = Modifier
                .offset(0f.grid2mpx.mpx2dp, 2f.grid2mpx.mpx2dp)
                .scale(6f / 8f))

        val menuItems = listOf("1 PLAYER", "2 PLAYERS", "STAGES")

        menuItems.forEachIndexed { i, text ->
            PixelText(
                text = text,
                charHeight = 0.5f.grid2mpx,
                topLeft = Offset(6.grid2mpx, (10 + i).grid2mpx),
                onClick = { selectionIndex = i })
        }

        PixelText(text = "© 1980 1985 NAMCO LTD.", charHeight = 0.5f.grid2mpx, topLeft = Offset(3.grid2mpx, (gridUnitNum - 2).grid2mpx))
        PixelText(text = "  ALL RIGHTS RESERVED", charHeight = 0.5f.grid2mpx, topLeft = Offset(3.grid2mpx, (gridUnitNum - 1).grid2mpx))

        PixelCanvas(
            topLeftInMapPixel = Offset(5.5f.grid2mpx, 9.7f.grid2mpx + selectionIndex.grid2mpx)
        ) {
            drawForDirection(Direction.Right) {
                this as PixelDrawScope
                drawPlayerTankLevel1(0, PlayerYellowPalette)
            }
        }
    }
}

@Composable
fun PixelText(
    text: String,
    charHeight: MapPixel,
    topLeft: Offset,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    onClick: () -> Unit = {},
) {
    val charHeightDp = charHeight.mpx2dp
    val fontScale = LocalDensity.current.fontScale
    val charHeightSp = (charHeightDp / fontScale).value.sp // factor out possible font scale
    Box(
        modifier = modifier
            .offset(topLeft.x.mpx2dp, topLeft.y.mpx2dp)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = charHeightSp,
        )
    }
}