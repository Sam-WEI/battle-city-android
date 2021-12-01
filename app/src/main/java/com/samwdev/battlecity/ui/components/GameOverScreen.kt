package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

@Composable
fun GameOverScreen() {
    BrickTitle(
        texts = arrayOf("GAME", "OVER"),
        modifier = Modifier.fillMaxSize().scale(0.5f),
    )
}