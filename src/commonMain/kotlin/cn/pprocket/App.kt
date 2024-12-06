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

fun loadConfig(): Config {
    val file = File("config.json")
    if (!PlatformU.containFile("config.json")) {
        PlatformU.createFile("config.json")
        PlatformU.saveFile("config.json",Json.encodeToString(Config()))
        return Config()
    }
    val content = file.read()
    return Config()

}
var client = TClient

suspend fun fetchTopicTask() {
    GlobalState.topicList = client.fetchTopics()
}

suspend fun fetchMeTask() {
    if (GlobalState.config.isLogin) {
        val userId = GlobalState.config.user.userId
        GlobalState.users[userId]= HeyClient.getUser (userId)
    }
}
fun saveConfigTask() {

}

