package com.samwdev.battlecity.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.samwdev.battlecity.core.state.OnScreenScore
import com.samwdev.battlecity.core.cell2mpx
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
        )
            .size(2f.cell2mpx.mpx2dp, 1f.cell2mpx.mpx2dp)
            .zIndex(ZIndexOnScreenScore),
        contentAlignment = Alignment.CenterStart,
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