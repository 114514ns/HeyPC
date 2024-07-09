
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.pprocket.HeyClient
import cn.pprocket.pages.HomePage
import cn.pprocket.pages.RootPage
import java.awt.Color
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.io.File
import kotlin.math.roundToInt

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
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 450.dp, height = 1050.dp,

        )
    ) {
        //setupFixedAspectRatio(window, aspectRatio)
        App()
    }
}
