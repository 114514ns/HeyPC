package cn.pprocket

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.platform.SystemFont
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.CanvasBasedWindow
import cn.pprocket.pages.RootPage
import cn.pprocket.ui.PlatformU
import com.materialkolor.DynamicMaterialTheme
import fetchFeedsTask
import fetchMeTask
import fetchTopicTask
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch
import loadConfig


@OptIn(ExperimentalComposeUiApi::class)

fun main() {


    CanvasBasedWindow("Multiplatform App") {
        //HeyClient.domain = "http://172.23.21.14"
        HeyClient.domain = "https://api.ikun.dev"

        var color by remember { mutableStateOf(Color(99, 160, 2)) }
        val scope = rememberCoroutineScope()
        scope.launch {
            GlobalState.config = loadConfig()
            HeyClient.cookie = GlobalState.config.cookies
            fetchTopicTask()
            //fetchFeedsTask()
            //fetchMeTask()
        }

        val origin = MaterialTheme.typography
        var fontState by remember { mutableStateOf(origin) }
        LaunchedEffect(Unit) {
            val bytes = HeyClient.ktorClient.get("https://r2.ikun.dev/LXGWWenKaiMono-Regular.ttf").readBytes()
            fontState = Typography().run {


                val fontFamily = androidx.compose.ui.text.platform.Font("Unicode", bytes).toFontFamily()
                copy(
                    displayLarge = displayLarge.copy(fontFamily = fontFamily),
                    displayMedium = displayMedium.copy(fontFamily = fontFamily),
                    displaySmall = displaySmall.copy(fontFamily = fontFamily),
                    headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
                    headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
                    headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
                    titleLarge = titleLarge.copy(fontFamily = fontFamily),
                    titleMedium = titleMedium.copy(fontFamily = fontFamily),
                    titleSmall = titleSmall.copy(fontFamily = fontFamily),
                    bodyLarge = bodyLarge.copy(fontFamily =  fontFamily),
                    bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
                    bodySmall = bodySmall.copy(fontFamily = fontFamily),
                    labelLarge = labelLarge.copy(fontFamily = fontFamily),
                    labelMedium = labelMedium.copy(fontFamily = fontFamily),
                    labelSmall = labelSmall.copy(fontFamily = fontFamily)
                )
            }
            PlatformU.typography = fontState
        }



        DynamicMaterialTheme(color, useDarkTheme = false, typography = fontState) {
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
