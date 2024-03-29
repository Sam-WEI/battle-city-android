package com.samwdev.battlecity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.samwdev.battlecity.core.SoundPlayer
import com.samwdev.battlecity.ui.component.BattleCityApp
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var soundPlayer: SoundPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        soundPlayer = SoundPlayer.INSTANCE
        lifecycleScope.launch {
            soundPlayer.init(this@MainActivity)
        }
        setContent {
            BattleCityApp()
        }
    }

    override fun onResume() {
        super.onResume()
        soundPlayer.resume()
    }

    override fun onPause() {
        super.onPause()
        soundPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer.release()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, window.decorView).let {
                it.hide(WindowInsetsCompat.Type.systemBars())
                it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BattleCityTheme {

    }
}