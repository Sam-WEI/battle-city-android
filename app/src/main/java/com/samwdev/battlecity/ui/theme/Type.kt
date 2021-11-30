package com.samwdev.battlecity.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.R

val PixelFont = FontFamily(
    Font(R.font.pixel_font_regular)
)

// Set of Material typography styles to start with
val Typography = Typography(
    defaultFontFamily = PixelFont,
    body1 = TextStyle(
        fontFamily = PixelFont,
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