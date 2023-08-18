package com.samwdev.battlecity.ui.theme

import androidx.compose.material3.Typography
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
    bodyLarge = TextStyle(
        fontFamily = Ps2pFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    headlineSmall = TextStyle(
        fontFamily = Ps2pFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        background = Color.Transparent,
    ),

    bodyMedium = TextStyle(
        fontFamily = Ps2pFont,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        background = Color.Transparent,
        lineHeight = 18.sp,
    ),

    labelLarge = TextStyle(
        fontFamily = Ps2pFont,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        background = Color.Transparent,
    ),
)