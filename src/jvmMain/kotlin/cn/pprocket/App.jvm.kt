package cn.pprocket

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Maximize
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import client
import cn.pprocket.pages.RootPage
import cn.pprocket.ui.PlatformU
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.rememberDynamicColorScheme
import dev.datlag.kcef.KCEF
import fetchMeTask
import fetchTopicTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import loadConfig
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
    var color by remember { mutableStateOf(Color(99, 160, 2)) }

    PlatformU.initFirebase()
    LaunchedEffect(windowState.placement) {
        PlatformU.isFull = (windowState.placement != WindowPlacement.Floating)
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            KCEF.init(builder = {
                installDir(java.io.File("kcef-bundle"))
                progress {
                    onDownloading {
                        logger.info("onDownloading")
                    }
                    onInitialized {
                        logger.info("onInitialized")
                    }
                }
                download {
                    custom("https://tjdownload.pan.wo.cn/openapi/download?fid=aPJUh_kCRwRH8BHQ1PIHBo4rbzGTdfIaUPLjiPYSLvMI7D7hhbJk7YIYt1%2BUZwX3hEenZqSYIMFbMLG4kKhfltrBNpnQ==")
                }

                settings {
                    cachePath = java.io.File("cache").absolutePath
                }
            }, onError = {
                it?.printStackTrace()
            }, onRestartRequired = {
                logger.info("onRestartRequired")
            })
        }
    }
    Window(
        title = title,
        state = windowState,
        onCloseRequest = ::exitApplication,
        undecorated = true,
    ) {
        var offsetX by remember { mutableStateOf(0) }
        var offsetY by remember { mutableStateOf(0) }
        Column {
            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color.DarkGray)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += (dragAmount.x).toInt()
                            offsetY += (dragAmount.y).toInt()
                            window.setLocation(
                                window.x + offsetX,
                                window.y + offsetY
                            )
                        }
                    }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { windowState.isMinimized = true }) {
                    Icon(Icons.Default.Minimize, contentDescription = "Minimize", tint = Color.White)
                }



                IconButton(onClick = {
                    if (windowState.placement == WindowPlacement.Floating) {
                        windowState.placement = WindowPlacement.Maximized
                    } else {
                        windowState.placement = WindowPlacement.Floating
                    }
                }) {
                    Icon(Icons.Default.Adb, contentDescription = "Maximize", tint = Color.White)
                }





                // 关闭按钮
                IconButton(onClick = ::exitApplication) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }



            }
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
}



fun saveTask() {
    Thread {
        while (true) {
            File("config.json").write(Json.encodeToString(GlobalState.config))
            Thread.sleep(1000)
        }
    }.start()
}



