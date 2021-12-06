package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.samwdev.battlecity.core.OnScreenScore
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.core.toMpx
import com.samwdev.battlecity.ui.theme.CiiFont

@Composable
fun OnScreenScore(onScreenScore: OnScreenScore) {
    val charHeightDp = 18f
    val fontScale = LocalDensity.current.fontScale
    val charHeightSp = (charHeightDp / fontScale).sp
    Box(
        modifier = Modifier.offset(
            onScreenScore.offset.x.mpx2dp,
            onScreenScore.offset.y.mpx2dp
        ).size(1f.grid2mpx.mpx2dp).zIndex(ZIndexOnScreenScore),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = onScreenScore.score.toString(),
            color = Color.White,
            fontSize = charHeightSp,
            letterSpacing = 1.sp,
            fontFamily = CiiFont,
        )
    }
}