
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.pprocket.HeyClient
import cn.pprocket.pages.RootPage
import java.io.File

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            RootPage()
        }
    }
}

fun main() = application {
    HeyClient.scriptContent = File("extra.js").readText()
    val multipy = 1.3f
    Thread {
        while(true) {
            System.gc()
            Thread.sleep(5000)
        }
    }.start()
    Window(
        title = "迎面走来的你让我蠢蠢欲动",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = (multipy*450).dp, height = (multipy*1050).dp,

        )
    ) {
        //setupFixedAspectRatio(window, aspectRatio)
        App()
    }
}
