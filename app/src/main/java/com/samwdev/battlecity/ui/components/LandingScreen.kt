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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.Direction
import com.samwdev.battlecity.core.MapPixel
import com.samwdev.battlecity.core.grid2mpx
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val gridUnitNum = 16

enum class LandingScreenMenuItem(val title: String) {
    Player1("1 PLAYER"),
    Player2("2 PLAYERS"),
    Stages("STAGES"),
    Editor("CONSTRUCTION");
}

@Composable
fun LandingScreen(onMenuSelect: (LandingScreenMenuItem) -> Unit) {
    val fistMenuItemPos = Offset(6.grid2mpx, 9.5f.grid2mpx)
    var selectedMenuItem: LandingScreenMenuItem by remember { mutableStateOf(LandingScreenMenuItem.Player1) }
    var selected: Boolean by remember { mutableStateOf(false) }

    var menuVisibility: Map<LandingScreenMenuItem, Boolean> by remember {
        mutableStateOf(LandingScreenMenuItem.values().associateWith { true })
    }

    LaunchedEffect(selectedMenuItem, selected) {
        if (selected) {
            launch {
                var i = 0
                while (i < 4) {
                    menuVisibility = menuVisibility.toMutableMap().apply {
                        this[selectedMenuItem] = i % 2 == 1
                    }
                    i++
                    delay(150)
                }
                onMenuSelect(selectedMenuItem)
            }
        }
    }

    Grid(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Black),
        gridUnitNum = gridUnitNum,
    ) {
        PixelText(
            text = "I-    00 HI- 20000",
            charHeight = 0.5f.grid2mpx,
            topLeft = Offset(1f.grid2mpx, 1f.grid2mpx)
        )

        BrickTitle("BATTLE", "CITY",
            modifier = Modifier
                .offset(0f.grid2mpx.mpx2dp, 2f.grid2mpx.mpx2dp)
                .scale(6f / 8f))

        LandingScreenMenuItem.values().forEachIndexed { i, menu ->
            PixelText(
                text = menu.title,
                charHeight = 0.5f.grid2mpx,
                topLeft = fistMenuItemPos + Offset(0f, i.grid2mpx),
                modifier = Modifier.alpha(if (menuVisibility.getValue(menu)) 1f else 0f)
            ) {
                if (!selected) {
                    selectedMenuItem = menu
                    selected = true
                }
            }
        }

        PixelText(
            text = "© 1980 1985 NAMCO LTD.",
            charHeight = 0.5f.grid2mpx,
            topLeft = Offset(3.grid2mpx, (gridUnitNum - 2).grid2mpx)
        )
        PixelText(
            text = "  ALL RIGHTS RESERVED",
            charHeight = 0.5f.grid2mpx,
            topLeft = Offset(3.grid2mpx, (gridUnitNum - 1).grid2mpx)
        )

        PixelCanvas(
            topLeftInMapPixel = fistMenuItemPos + Offset((-0.5f).grid2mpx,(-0.3f).grid2mpx + (selectedMenuItem.ordinal).grid2mpx)
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
    modifier: Modifier = Modifier,
    topLeft: Offset = Offset.Zero,
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