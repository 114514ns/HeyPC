package cn.pprocket

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import cn.pprocket.pages.RootPage
import cn.pprocket.ui.PlatformU
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.materialkolor.DynamicMaterialTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
        val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)

        val context = this.baseContext
        val metrics = context.resources.displayMetrics
        val densityDpi = metrics.densityDpi
        val screenWidthDp = metrics.widthPixels / densityDpi.toFloat() * 160
        PlatformU.setFullscreen(screenWidthDp >= 600)
        PlatformU.mContext = context
        WindowCompat.setDecorFitsSystemWindows(window, true)


        setContent {
            var color by remember { mutableStateOf(Color(99, 160, 2)) }
            DynamicMaterialTheme(color, useDarkTheme = false) {
                DisposableEffect(Unit) { // from https://juejin.cn/post/7331714933388492812
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT,
                        ) {false},
                        navigationBarStyle = SystemBarStyle.auto(
                            lightScrim,
                            darkScrim,
                        ) { false},
                    )
                    onDispose {}
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    RootPage(onChangeState = { state ->
                        if (state.type == "title") {
                            title = state.value as String
                        }
                        if (state.type == "color") {
                            color = state.value as Color
                        }
                    })
                }

            }
        }

    }
}