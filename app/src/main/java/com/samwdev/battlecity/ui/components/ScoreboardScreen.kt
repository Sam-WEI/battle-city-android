package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.ui.theme.BattleCityTheme

private val ColorRed = Color(210, 81, 65)
private val ColorOrange = Color(241, 176, 96)

@Composable
fun ScoreboardScreen() {
    Grid(
        gridUnitNum = 15,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(1.grid2mpx.mpx2dp)
                .offset(y = 1.grid2mpx.mpx2dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Row(Modifier.weight(1f, true),
                horizontalArrangement = Arrangement.End) {
                PixelText(
                    text = "HI-SCORE",
                    charHeight = 0.5f.grid2mpx,
                    topLeft = Offset.Zero,
                    textColor = ColorRed,
                )
            }
            Row(Modifier.weight(1f, true),
                horizontalArrangement = Arrangement.Start) {
                PixelText(
                    text = "   20000",
                    charHeight = 0.5f.grid2mpx,
                    topLeft = Offset.Zero,
                    textColor = ColorOrange,
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(1.grid2mpx.mpx2dp)
                .offset(y = 2.grid2mpx.mpx2dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            PixelText(
                text = "STAGE   12",
                charHeight = 0.5f.grid2mpx,
                topLeft = Offset.Zero,
            )
        }
        Row(Modifier.offset(y = 3.grid2mpx.mpx2dp).fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f, true),
                horizontalAlignment = Alignment.End
            ) {
                PixelText(
                    text = "I-PLAYER",
                    charHeight = 0.5f.grid2mpx,
                    topLeft = Offset.Zero,
                    textColor = ColorRed,
                    modifier = Modifier.height(1f.grid2mpx.mpx2dp)
                )
                PixelText(
                    text = "22800",
                    charHeight = 0.5f.grid2mpx,
                    topLeft = Offset.Zero,
                    textColor = ColorOrange,
                    modifier = Modifier.height(1f.grid2mpx.mpx2dp)
                )
                
            }
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f, true)
                    .offset(y = 3f.grid2mpx.mpx2dp),
                horizontalAlignment = Alignment.End
            ) {

            }

            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f, true),
                horizontalAlignment = Alignment.Start
            ) {
                // for player 2
            }
        }

    }
}

@Preview
@Composable
private fun ScoreboardScreenPreview() {
    BattleCityTheme {
        Box(modifier = Modifier.size(500.dp)) {
            ScoreboardScreen()
        }
    }
}