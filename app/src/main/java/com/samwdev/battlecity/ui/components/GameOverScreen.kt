package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GameOverScreen() {
    BrickTitle(
        texts = arrayOf("GAME", "OVER"),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(0.5f),
    )
}

@Preview
@Composable
fun GameOverScreenPreview() {
    GameOverScreen()
}