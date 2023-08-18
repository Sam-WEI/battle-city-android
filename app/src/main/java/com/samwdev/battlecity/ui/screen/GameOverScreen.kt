package com.samwdev.battlecity.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.samwdev.battlecity.ui.component.BrickTitle
import com.samwdev.battlecity.ui.component.Grid

@Composable
fun GameOverScreen() {
    Grid(
        gridSize = 15,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Black)
    ) {
        BrickTitle(
            texts = arrayOf("GAME", "OVER"),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .scale(0.5f),
        )
    }
}

@Preview
@Composable
fun GameOverScreenPreview() {
    GameOverScreen()
}