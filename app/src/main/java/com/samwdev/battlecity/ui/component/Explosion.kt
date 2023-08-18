package com.samwdev.battlecity.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.state.Explosion
import com.samwdev.battlecity.core.MapPixel
import com.samwdev.battlecity.core.cell2mpx
import com.samwdev.battlecity.ui.theme.BattleCityTheme

@Composable
fun Explosion(explosion: Explosion) {
    ExplosionFrame(center = explosion.centerOffset, pattern = explosion.currFrameUiPattern)
}

private const val ExpSide: MapPixel = 32f

@Composable
fun ExplosionFrame(center: Offset, pattern: ExplosionUiPattern) {
    PixelCanvas(
        topLeftInMapPixel = center - Offset(ExpSide / 2, ExpSide / 2),
        widthInMapPixel = ExpSide,
        heightInMapPixel = ExpSide
    ) {
        drawExplosionPattern(pattern)
    }
}

private val ExpColorWhite = Color.White
private val ExpColorPurple = Color(89, 13, 121)
private val ExpColorRed = Color(181, 49, 33)

private val ColorMap = mapOf(
    'W' to ExpColorWhite,
    'R' to ExpColorRed,
    'P' to ExpColorPurple,
)

private fun PixelDrawScope.drawExplosionPattern(pattern: ExplosionUiPattern) {
    val frames = pattern.raw
    val width = frames.first().length
    val height = frames.size
    translate(left = (ExpSide - width) / 2f, top = (ExpSide - height) / 2f) {
        this as PixelDrawScope
        for ((row, str) in frames.withIndex()) {
            if (str.isBlank()) continue
            for ((col, char) in str.withIndex()) {
                if (char.isWhitespace()) continue
                drawPixel(
                    color = ColorMap[char]!!,
                    topLeft = Offset(row.toFloat(), col.toFloat())
                )
            }
        }
    }
}

@Preview
@Composable
fun ExplosionPreview() {
    BattleCityTheme {
        Grid(modifier = Modifier.size(500.dp), gridSize = 10) {
            repeat(5) { i ->
                ExplosionFrame(
                    center = Offset(5f.cell2mpx, (i * 2 + 1).cell2mpx),
                    pattern = ExplosionUiPattern.values()[i]
                )
            }
        }
    }
}

enum class ExplosionUiPattern(val raw: List<String>) {
    Small0(RawDataSmall0),
    Small1(RawDataSmall1),
    Small2(RawDataSmall2),
    Large0(RawDataLarge0),
    Large1(RawDataLarge1),
}

private val RawDataSmall0 = listOf(
    "                ",
    "                ",
    "       W     W  ",
    "   W   W  W W   ",
    "   PWW PW WWP   ",
    "    PPWWPPWP    ",
    "     PWRWRWPWW  ",
    "   WWWPWR RPP   ",
    "     PW RRWP    ",
    "     WWRWPRWP   ",
    "    WP WPWWPWP  ",
    "   WP PW PW  W  ",
    "      W   P     ",
    "                ",
    "                ",
)

private val RawDataSmall1 = listOf(
    "                ",
    "      P   W     ",
    " W  P WP WP   W ",
    " PWW  PW WP WWP ",
    "  PPWPPWWWPWPP  ",
    "   PWRWWWPRWW P ",
    " P  PWR RWWPP   ",
    "   WWWWRRR PWWW ",
    "WW WPW RR WWPP  ",
    "  PPWWPRRRWPP P ",
    "    WRWP PWRW   ",
    "  P PWRWWWRPWW  ",
    "   PWPWPPWW PPW ",
    "   WPPWP PW   P ",
    "  WP  W P PP    ",
    "                ",
)

private val RawDataSmall2 = listOf(
    "    P P    P  P ",
    " W   W  W P  WP ",
    " PPWW  WW   WP  ",
    "  PPWPPWPP WWP  ",
    "   PRWWWWPWWPP P",
    " W PWWRW WWRP   ",
    "WWWW WR  RWWWWWW",
    " PPPWWPR RWPPP  ",
    "   PPP WR PPWW  ",
    " WPPWWPW WRWPPW ",
    "  WWWRWPWPWW    ",
    " PWPPWPWWPPWW P ",
    " WP  P PWP PPW  ",
    "WP  P   W    PW ",
    "        W P   P ",
)

private val RawDataLarge0 = listOf(
    "                                ",
    "                     W       W  ",
    "  W       W          W       W  ",
    "   W  W  W   PPP WWP  W     W   ",
    "    P  W   WWWPPPWWPP W W  P    ",
    "     W P  WPWWW W  WPPW W WW    ",
    "      W   WWPW WPPP WWWW W      ",
    "    WW   WWWWWWWWWPWWWWWW W     ",
    "       PPPW  WWWPWWPWW PW    PW ",
    "      WPPPWWWWWPWW WW WWPW   W  ",
    "P    WW WPWWWRWWRWWWPWWWPWWW    ",
    " WP PW WWWRRWWWWRPWWWPWPW WPW   ",
    "  W WW WWWWRRWRWRWWRRPWW WPPW   ",
    " W   WW WPWRWRRRRRRWRWWWW WW    ",
    "  W WPWWPPWWRWRWWRWRPWWWWW WW   ",
    "    PP PWPWWRRWP WRWWWW W WPP   ",
    "      WWPWRRRW WPW RW PWWW PP   ",
    "     WWWWWWWRRWRPRRRWPPPWWWP  W ",
    "     WW WWWWRWRRPRWWRWW WWPP   W",
    "    PW WWPWRWWWRWRPWWWWWPPPP    ",
    "    PPWPPPWWWWPRRWWWWPWWW P W   ",
    "  W  PPPPWWWWPPWRWWWWWPWW WW    ",
    "   W    PW WPPWWWW WW PW WP     ",
    "        WW WWW WWWW  PPWWPP     ",
    "      WP WW W PPWWWWPPWW P      ",
    "     P W WWWWWPP  PPP W W WW    ",
    "    W      PPPP         W  P    ",
    "   W     W        W  W     WP   ",
    "  P        W       PWP      WP  ",
    "  W                W W       W  ",
    "                                ",
    "                                ",
)

private val RawDataLarge1 = listOf(
    "W                               ",
    "PW   W             PPWW       W ",
    " PW   P     WPPP PWWWWPP    WP  ",
    "  PP    WW WWWP PWWW  WPP  WP   ",
    "   PP  WPWWWRWWW WW WP WPP  P   ",
    "   P  WWPPRPWWWWWWWWWWP WP      ",
    "      WPWWWWPWWWWWWPPWW WP WW   ",
    "    PPWWWWWWWWWPPWWWWPWWWPPWWP  ",
    "    PWWWW WPPWWWWPWWWWWWPPWWPPP ",
    "     WW  WWWWWRWWWWRWWWWPWW WPP ",
    "   WWW WWWRWWWRRWWRRWRWPWWWW WP ",
    "  PWWWWWWWRRRWRWRRRWRWWWWPWW WPW",
    "  WPWW WWWR RRWPRRWRWWWWWPW WPPP",
    "   PW PWWWWRWWR RWRWWWRWPW WPPW ",
    "   PP PWWWWRRRRWPRRRWRWWWWWWP   ",
    " W WWW PWRRRRRWWW WRWWWWWWPPWW  ",
    " WWWWWWWWWWRRP WWRRRRWWWWPWWWPP ",
    "WWWRPWWWWRWRWWRPWWR WRRRWWWWWWP ",
    "WWWWPWWWRWWRRRR RRWRWWWWWWW WWPW",
    "PWWPWRWWWWRRWRRWRRRRWWWWWPWW WWP",
    "PPPPWWPPWRRPPWRRRWWRRWWPPWWWW W ",
    " PP WWWWWWWWWWRWWWWWWRWWWPWW WPW",
    "     WWWWWWWPWWWWWWPWWWWWWWWW WP",
    "    WW WW WWWWWWW PP WWWWWWWPWPW",
    "    WWW  PPWWWPWWW  WWWP WW  PWW",
    "    WWWWWWPPWWW WWWWPWWPP  WPPW ",
    "    PWWWWP WWWW WWWPPPWWPPPPPW  ",
    "  W  PWPP  PPPWW PPP WRWWPPW    ",
    "   W  PP      WWW   WWWPP    W  ",
    "  P P     WP   WWWWWWWPPW  P P  ",
    " W  P       W   WWPPWPPP    P W ",
    "W          W     PPP P         P",
)