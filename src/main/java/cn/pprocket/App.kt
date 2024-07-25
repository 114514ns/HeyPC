import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.pprocket.Config
import cn.pprocket.GlobalState
import cn.pprocket.Logger
import cn.pprocket.pages.RootPage
import com.google.gson.Gson

import java.awt.Dimension
import java.awt.Toolkit
import java.io.File

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme() {
        Box(modifier = Modifier.fillMaxSize()) {
            RootPage()
        }
    }
}

val logger = Logger(Object())
fun main() = application {
    GlobalState.config = loadConfig()
    saveTask()
    System.setProperty("skiko.directx.gpu.priority", "discrete")
    val screenSize: Dimension = Toolkit.getDefaultToolkit().getScreenSize()
    //saveTask()
    gcTask()
    val width = screenSize.width
    val multipy = 1.3f * (width / 2560f)
    logger.info("Screen size :  ${screenSize.width} * ${screenSize.height}")
    logger.info("Real size ${multipy * 450}dp  * ${1050 * multipy}dp")


    Window(
        title = "迎面走来的你让我蠢蠢欲动",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            width = (multipy * 450).dp, height = (multipy * 1050).dp,

            )
    ) {
        App()


    }
}

fun loadConfig(): Config {
    val file = File("config.json")
    if (!file.exists()) {
        file.createNewFile()
        return Config()
    }
    val content = file.readText()
    return Gson().fromJson(content, Config::class.java)

}

fun saveTask() {
    Thread {
        while (true) {
            val json = Gson().toJson(GlobalState.config)
            File("config.json").writeText(json)
            Thread.sleep(1000)
        }
    }.start()
}

fun gcTask() {
    Thread {
        while (true) {
            System.gc()
            Thread.sleep(2000)
        }
    }.start()
}
