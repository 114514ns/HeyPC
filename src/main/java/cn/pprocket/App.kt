import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.pprocket.Config
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.Logger
import cn.pprocket.items.Topic
import cn.pprocket.pages.RootPage
import cn.pprocket.pages.getImageDir
import com.google.gson.Gson

import java.awt.Dimension
import java.awt.Toolkit
import java.io.File
import java.lang.management.ManagementFactory
import java.nio.file.Files
import java.nio.file.Paths



val logger = Logger(Object())

fun main() = application {
    cleanupTask()
    fetchTopicTask()
    var title by remember { mutableStateOf("迎面走来的你让我如此蠢蠢欲动") }
    GlobalState.config = loadConfig()
    HeyClient.cookie = GlobalState.config.cookies
    saveTask()
    System.setProperty("skiko.directx.gpu.priority", "discrete")
    val screenSize: Dimension = Toolkit.getDefaultToolkit().getScreenSize()
    gcTask()
    val width = screenSize.width
    val multipy = 1.3f * (width / 2560f)
    logger.info("Screen size :  ${screenSize.width} * ${screenSize.height}")
    logger.info("Real size ${multipy * 450}dp  * ${1050 * multipy}dp")

    Window(
        title = title,
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            width = (multipy * 450).dp, height = (multipy * 1050).dp,

            )
    ) {
        MaterialTheme() {
            Box(modifier = Modifier.fillMaxSize()) {
                RootPage(onChangeTitle = { newTitle ->
                    title = newTitle
                })
            }
        }

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

fun cleanupTask() {
    Thread {
        val dir = getImageDir()

        if (dir.exists()) {
            val start = System.currentTimeMillis()
            val files  = dir.listFiles()
            files.forEach {
                it.delete()
            }
            logger.info("清理缓存耗时  ${System.currentTimeMillis() - start}ms")
            logger.info("文件数量  ${files.size}")
        }


    }.start()

}

fun fetchTopicTask() {
    Thread {
        GlobalState.topicList = Topic.getTopics()
    }.start()
}
