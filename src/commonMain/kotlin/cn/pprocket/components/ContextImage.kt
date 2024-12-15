package cn.pprocket.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import cn.pprocket.GlobalState
import cn.pprocket.Logger
import cn.pprocket.Platform
import cn.pprocket.ui.PlatformU
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ContextImage(scope: CoroutineScope, img: String, modifier: Modifier = Modifier,nav: NavController? = null) {
    var showSticker by remember { mutableStateOf(false) }
    val url = (img)
    val logger = Logger("cn.pprocket.components.ContextImage")
    if (showSticker) {
        //StickerDialog({ showSticker = false }, getOriginalImage(img))
    }
    LaunchedEffect(Unit) {

    }
    Box(modifier) {
        val headers = NetworkHeaders.Builder().set(
            "Referer", "https://tieba.baidu.com/"
        ).build()
        AsyncImage(

            ImageRequest.Builder(LocalPlatformContext.current).data(url).httpHeaders(headers).build(),
            null,
            modifier = Modifier.padding(8.dp).clip(RoundedCornerShape(12.dp)).graphicsLayer {
                //alpha = 0.8f // 降低亮度
            }.clickable(
                indication = null, // 禁用涟漪效果
                interactionSource = remember { MutableInteractionSource() }.also {
                    // 阻止交互状态变化
                    LaunchedEffect(it) {
                        it.interactions.collect { } // 消费所有交互事件，防止状态变化
                    }
                }
            ) {
                logger.info(url)
                nav?.navigate("image/${Base64.Default.encode(img.toByteArray())}")
            }.fillMaxSize().animateContentSize(),
            contentScale = ContentScale.Crop)

    }
}

fun transformImage(string: String): String {
    if (GlobalState.config.originImage) {
        return getOriginalImage(string)

    } else {
        return string
    }
}

fun getOriginalImage(string: String): String {

    if (string.contains("steamstatic")) {
        return string
    }
    // 解析 URL 协议
    val protocolEndIndex = string.indexOf("://")
    if (protocolEndIndex == -1) return string // 如果没有协议部分，返回原字符串
    val protocol = string.substring(0, protocolEndIndex)

    // 获取主机部分
    val remainingUrl = string.substring(protocolEndIndex + 3)
    val hostEndIndex = remainingUrl.indexOf("/")
    if (hostEndIndex == -1) return string // 如果没有路径部分，返回原字符串
    val host = remainingUrl.substring(0, hostEndIndex)

    // 获取路径部分
    val path = remainingUrl.substring(hostEndIndex)

    // 处理路径和扩展名
    val ext = path.split(".").last() // 获取扩展名
    var processedPath = path.replace("/thumb.$ext", "").replace("/format.$ext", "")

    // 拼接协议、主机和处理后的路径
    return "$protocol://$host$processedPath.$ext"
}

