package com.samwdev.battlecity.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun BattleField(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).background(Color.Green)) {

    }
}