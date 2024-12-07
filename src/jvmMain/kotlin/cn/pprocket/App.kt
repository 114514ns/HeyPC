package cn.pprocket

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import client
import cn.pprocket.items.Topic
import cn.pprocket.pages.RootPage
import cn.pprocket.ui.PlatformU
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.rememberDynamicColorScheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.awt.Dimension
import java.awt.Toolkit


val logger = Logger("App")

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    LaunchedEffect(Unit) {
        GlobalState.config = loadConfig()
        saveTask()
        client.cookie = GlobalState.config.cookies
        fetchTopicTask()
        fetchMeTask()
    }

    var title by remember { mutableStateOf("迎面走来的你让我如此蠢蠢欲动") }


    PlatformU.mTypography = MaterialTheme.typography
    val screenSize: Dimension = Toolkit.getDefaultToolkit().getScreenSize()

    val width = screenSize.width
    val multipy = 1.3f * (width / 2560f)
    logger.info("Screen size :  ${screenSize.width} * ${screenSize.height}")
    logger.info("Real size ${multipy * 450}dp  * ${1050 * multipy}dp")
    val windowState = rememberWindowState(width = (multipy * 450).dp, height = (multipy * 1050).dp)
    val colorScheme = rememberDynamicColorScheme(Color(99, 160, 2), false)
    var color by remember { mutableStateOf(Color(99, 160, 2)) }

    Window(
        title = title,
        onCloseRequest = ::exitApplication,
        state = windowState,
    ) {
        DynamicMaterialTheme(color, useDarkTheme = false) {
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

fun loadConfig(): Config {
    val file = cn.pprocket.File("config.json")
    if (!PlatformU.containFile("config.json")) {
        PlatformU.createFile("config.json")
        return Config()
    }

    val content = file.read()
    return Json.decodeFromString<Config>(content)

}

fun saveTask() {
    Thread {
        while (true) {
            File("config.json").write(Json.encodeToString(GlobalState.config))
            Thread.sleep(1000)
        }
    }.start()
}

suspend fun fetchTopicTask() {
    GlobalState.topicList = client.fetchTopics()
}

suspend fun fetchMeTask() {
    if (GlobalState.config.isLogin) {
        val userId = GlobalState.config.user.userId
        GlobalState.users[userId]= client.getUser (userId)
    }
}

