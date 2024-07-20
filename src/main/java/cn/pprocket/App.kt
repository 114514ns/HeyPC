
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
import cn.pprocket.HeyClient
import cn.pprocket.pages.RootPage
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.lt.load_the_image.LoadTheImageManager
import com.lt.load_the_image.cache.ImageLruMemoryCache
import org.example.cn.pprocket.utils.app.AppSignGenerator
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

fun main() = application {
    GlobalState.config = loadConfig()
    saveTask()
    System.setProperty("skiko.directx.gpu.priority","discrete")
    //saveTask()
    gcTask()
    val multipy = 1.3f

    Window(
        title = "迎面走来的你让我蠢蠢欲动",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = (multipy*450).dp, height = (multipy*1050).dp,

        )
    ) {
        App()
    }
}
fun loadConfig() : Config {
    val file = File("config.json")
    if (!file.exists()) {
        file.createNewFile()
        return Config()
    }
    val content = file.readText()
    return Gson().fromJson(content,Config::class.java)

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
        while(true) {
            System.gc()
            Thread.sleep(2000)
        }
    }.start()
}
