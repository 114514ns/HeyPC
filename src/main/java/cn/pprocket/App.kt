import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowExceptionHandler
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.pprocket.Config
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.Logger
import cn.pprocket.State
import cn.pprocket.items.Topic
import cn.pprocket.pages.RootPage
import cn.pprocket.pages.getImageDir
import cn.pprocket.sticker.StickerManager
import com.google.gson.Gson
import com.lt.load_the_image.rememberImagePainter
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.rememberDynamicColorScheme
import java.awt.Dimension
import java.awt.SystemColor.window
import java.awt.Toolkit
import java.io.File


val logger = Logger(Object())

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    LaunchedEffect(Unit) {
        GlobalState.config = loadConfig()
        HeyClient.cookie = GlobalState.config.cookies
        cleanupTask()
        fetchTopicTask()
        saveTask()
        fetchFeedsTask()
        gcTask()
        fetchMeTask()
        StickerManager

    }

    var title by remember { mutableStateOf("迎面走来的你让我如此蠢蠢欲动") }

    System.setProperty("skiko.directx.gpu.priority", "discrete")
    val screenSize: Dimension = Toolkit.getDefaultToolkit().getScreenSize()

    val width = screenSize.width
    val multipy = 1.3f * (width / 2560f)
    logger.info("Screen size :  ${screenSize.width} * ${screenSize.height}")
    logger.info("Real size ${multipy * 450}dp  * ${1050 * multipy}dp")
    val windowState = rememberWindowState(width = (multipy * 450).dp, height = (multipy * 1050).dp)
    val colorScheme = rememberDynamicColorScheme(Color(99, 160, 2), false)
    var color by remember { mutableStateOf(Color(99, 160, 2)) }
    HeyClient.debug = true

    Window(
        title = title,
        onCloseRequest = ::exitApplication,
        state = windowState,
    ) {
        DynamicMaterialTheme(color,useDarkTheme = false) {
            Box(modifier = Modifier.fillMaxSize()) {
                RootPage(onChangeState = { state ->
                    if (state.type == "title") {
                        title = state.value as String
                    }
                    if (state.type == "color") {
                        color = state.value as Color
                    }
                },windowState)
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
    var last = ""
    Thread {
        while (true) {

            val json = Gson().toJson(GlobalState.config)
            if (last == "") {
                last = json
            } else if (last != json) {
                last = json
                File("config.json").writeText(json)
                logger.info("写入配置文件")
            }
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
            val files = dir.listFiles()
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

fun fetchMeTask() {
    Thread {
        if (GlobalState.config.isLogin) {
            val userId = GlobalState.config.user.userId
            GlobalState.users[userId] = HeyClient.getUser(userId)
        }
    }.start()
}

fun fetchFeedsTask() {
    Thread {
        GlobalState.feeds = HeyClient.getPosts(Topic.RECOMMEND)
    }.start()
}
