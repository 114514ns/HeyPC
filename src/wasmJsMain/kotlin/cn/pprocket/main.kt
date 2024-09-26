package cn.pprocket

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.GenericFontFamily
import androidx.compose.ui.window.CanvasBasedWindow
import cn.pprocket.pages.RootPage
import com.materialkolor.DynamicMaterialTheme
import fetchFeedsTask
import fetchMeTask
import fetchTopicTask
import kotlinx.coroutines.launch
import loadConfig

@OptIn(ExperimentalComposeUiApi::class)

fun main() {





    CanvasBasedWindow("Multiplatform App") {
        HeyClient.domain = "http://127.0.0.1"
        var color by remember { mutableStateOf(Color(99, 160, 2)) }
        val scope = rememberCoroutineScope()
        scope.launch {
            GlobalState.config = loadConfig()
            HeyClient.cookie = GlobalState.config.cookies
            fetchTopicTask()
            fetchFeedsTask()
            fetchMeTask()
        }
        DynamicMaterialTheme(color, useDarkTheme = false) {
            Box(modifier = Modifier.fillMaxSize()) {
                RootPage(onChangeState = { state ->
                    if (state.type == "color") {
                        color = state.value as Color
                    }
                })
            }

        }
    }
}
