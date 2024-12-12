import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

import cn.pprocket.*
import cn.pprocket.items.Topic
import cn.pprocket.ui.PlatformU
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


val logger = Logger("App")


var client = HeyClient




fun saveConfigTask() {

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

fun loadConfig(): Config {
    val file = cn.pprocket.File("config.json")
    if (!PlatformU.containFile("config.json")) {
        PlatformU.createFile("config.json")
        return Config()
    }

    val content = file.read()
    return Json.decodeFromString<Config>(content)

}