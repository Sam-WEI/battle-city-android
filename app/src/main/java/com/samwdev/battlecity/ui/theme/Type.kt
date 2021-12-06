package com.samwdev.battlecity.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.R

val Ps2pFont = FontFamily(
    Font(R.font.ps2p_regular)
)

val CiiFont = FontFamily(
    Font(R.font.connection_ii)
)

// Set of Material typography styles to start with
val Typography = Typography(
    defaultFontFamily = Ps2pFont,
    body1 = TextStyle(
        fontFamily = Ps2pFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    button = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        background = Color.Magenta,
    ),

    /* Other default text styles to override
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)