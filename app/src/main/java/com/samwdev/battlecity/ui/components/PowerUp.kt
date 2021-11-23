package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.entity.PowerUp
import com.samwdev.battlecity.ui.theme.BattleCityTheme

private val PowerUpColorBlue = Color(43, 71, 121)
private val PowerUpColorGray = Color(181, 181, 181)
private val PowerUpColorShadow = Color.Transparent
private val PowerUpColorWhite = Color.White

@Composable
fun PowerUp(topLeft: Offset, powerUp: PowerUp) {
    PixelCanvas(
        topLeftInMapPixel = topLeft,
        widthInMapPixel = 1f.grid2mpx,
        heightInMapPixel = 1f.grid2mpx,
    ) {
        scale(2f, 2f, pivot = Offset.Zero) {
            this as PixelDrawScope
            drawHorizontalLine(color = PowerUpColorWhite, topLeft = Offset(1f, 0f), length = 13f)
            drawVerticalLine(color = PowerUpColorWhite, topLeft = Offset(0f, 1f), length = 12f)
            drawVerticalLine(color = PowerUpColorWhite, topLeft = Offset(14f, 1f), length = 12f)
            drawHorizontalLine(color = PowerUpColorWhite, topLeft = Offset(1f, 13f), length = 13f)
            drawPixel(color = PowerUpColorGray, topLeft = Offset(14f, 0f))
            drawPixel(color = PowerUpColorGray, topLeft = Offset(0f, 13f))
            drawPixel(color = PowerUpColorGray, topLeft = Offset(14f, 13f))

            drawHorizontalLine(color = PowerUpColorBlue, topLeft = Offset(1f, 14f), length = 14f)
            drawVerticalLine(color = PowerUpColorBlue, topLeft = Offset(15f, 1f), length = 13f)

//            drawRect(color = PowerUpColorBlue, topLeft = Offset(2f, 2f), size = Size(12f, 11f))
            val transparentPath = Path()
            translate(2f, 2f) {
                val drawStuff = when (powerUp) {
                    PowerUp.Helmet -> drawHelmet(transparentPath)
                    PowerUp.Star -> drawStar(transparentPath)
                    PowerUp.Grenade -> drawGrenade(transparentPath)
                    PowerUp.Tank -> drawTank(transparentPath)
                    PowerUp.Shovel -> drawShovel(transparentPath)
                    PowerUp.Timer -> drawTimer(transparentPath)
                }
                clipPath(transparentPath, ClipOp.Difference) {
                    this as PixelDrawScope
                    drawRect(color = PowerUpColorBlue, topLeft = Offset(0f, 0f), size = Size(12f, 11f))
                    drawStuff(this)
                }
            }
        }
    }
}

private fun drawHelmet(transparentPath: Path): PixelDrawScope.() -> Unit {
    with(transparentPath) {
        addPixel(Offset(8f, 2f))
        addVerticalLine(topLeft = Offset(9f, 3f), length = 4f)
        addPixel(Offset(10f, 7f))
        addHorizontalLine(topLeft = Offset(1f, 7f), length = 5f)
        addHorizontalLine(topLeft = Offset(6f, 8f), length = 5f)
    }

    return {
        drawRect(color = PowerUpColorGray, topLeft = Offset(3f, 2f), size = Size(5f, 4f))
        drawRect(color = PowerUpColorGray, topLeft = Offset(2f, 3f), size = Size(7f, 4f))
        drawHorizontalLine(color = PowerUpColorWhite, topLeft = Offset(3f, 2f), length = 3f)
        drawHorizontalLine(color = PowerUpColorWhite, topLeft = Offset(2f, 3f), length = 2f)
        drawPixel(color = PowerUpColorWhite, topLeft = Offset(2f, 4f))

        drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(6f, 7f), length = 4f)
        drawPixel(color = PowerUpColorGray, topLeft = Offset(1f, 6f))
    }
}

private fun PixelDrawScope.drawStar() {
    val mode = BlendMode.DstOut
    drawRect(color = PowerUpColorShadow, topLeft = Offset(1f, 3f), size = Size(7f, 7f), blendMode = BlendMode.DstOut)

}

private fun PixelDrawScope.drawGrenade() {
    drawRect(color = PowerUpColorShadow, topLeft = Offset(1f, 3f), size = Size(7f, 7f), blendMode = BlendMode.Clear)

}

private fun PixelDrawScope.drawTank() {
    drawRect(color = PowerUpColorShadow, topLeft = Offset(1f, 3f), size = Size(7f, 7f), blendMode = BlendMode.Color)

}

private fun PixelDrawScope.drawShovel() {
    drawRect(color = PowerUpColorShadow, topLeft = Offset(1f, 3f), size = Size(7f, 7f), blendMode = BlendMode.Exclusion)

}

private fun PixelDrawScope.drawTimer() {
    drawRect(color = PowerUpColorShadow, topLeft = Offset(1f, 3f), size = Size(7f, 7f), blendMode = BlendMode.Xor)

}

@Preview
@Composable
private fun PowerUpPreview() {
    BattleCityTheme {
        Map(modifier = Modifier.size(500.dp), sideBlockCount = 6) {
            PowerUp.values().forEachIndexed { index, powerUp ->
                PowerUp(topLeft = Offset(0f.grid2mpx, index.toFloat().grid2mpx), powerUp)
            }
        }
    }
}

@Composable
fun PowerUpTest(topLeft: Offset) {
    GraphicsLayerScope()
    PixelCanvas(
        topLeftInMapPixel = topLeft,
        widthInMapPixel = 7f.grid2mpx,
        heightInMapPixel = 3f.grid2mpx,
    ) {
//        drawRect(color = PowerUpColorBlue, topLeft = Offset(0f, 0f), size = Size(13f.grid2mpx, 8f.grid2mpx))
        listOf(
            BlendMode.Clear,
            BlendMode.Src,
            BlendMode.Dst,
            BlendMode.SrcOver,
            BlendMode.DstOver,
            BlendMode.SrcIn,
            BlendMode.DstIn,
            BlendMode.SrcOut,
            BlendMode.DstOut,
            BlendMode.SrcAtop,
            BlendMode.DstAtop,
            BlendMode.Xor,
            BlendMode.Plus,
            BlendMode.Modulate,
            BlendMode.Screen,
            BlendMode.Overlay,
            BlendMode.Darken,
            BlendMode.Lighten,
            BlendMode.ColorDodge,
            BlendMode.ColorBurn,
            BlendMode.Hardlight,
            BlendMode.Softlight,
            BlendMode.Difference,
            BlendMode.Exclusion,
            BlendMode.Multiply,
            BlendMode.Hue,
            BlendMode.Saturation,
            BlendMode.Color,
            BlendMode.Luminosity,
        ).forEachIndexed { index, blendMode ->
            val row = index / 13
            val col = index % 13

            val path = Path().apply {
                addRect(Rect(Offset(col.toFloat().grid2mpx, row.toFloat().grid2mpx), Size(.5f.grid2mpx, .5f.grid2mpx)))
            }
            clipPath(path, ClipOp.Difference) {
                drawRect(color = Color.Red, topLeft = Offset(col.toFloat().grid2mpx, row.toFloat().grid2mpx), size = Size(.7f.grid2mpx, .7f.grid2mpx))
            }
//            drawRect(
//                color = PowerUpColorBlue,
//                topLeft = Offset(col.toFloat().grid2mpx, row.toFloat().grid2mpx),
//                size = Size(.5f.grid2mpx, .5f.grid2mpx),
//                blendMode = blendMode
//            )
//            drawRect(
//                color = Color.Transparent,
//                topLeft = Offset((col.toFloat() + .5f).grid2mpx, row.toFloat().grid2mpx),
//                size = Size(.5f.grid2mpx, .5f.grid2mpx),
//                blendMode = BlendMode.Clear
//            )

        }
    }
}
