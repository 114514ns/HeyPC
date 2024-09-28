package cn.pprocket.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cn.pprocket.GlobalState
import cn.pprocket.Logger
import cn.pprocket.Platform
import cn.pprocket.ui.PlatformU
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun ContextImage(scope: CoroutineScope, img: String, modifier: Modifier = Modifier) {
    var showSticker by remember { mutableStateOf(false) }
    val url = transformImage(img)
    val logger = Logger("cn.pprocket.components.ContextImage")
    if (showSticker) {
        //StickerDialog({ showSticker = false }, getOriginalImage(img))
    }
    LaunchedEffect(Unit) {

    }
    Box(modifier) {


        /*
        CoilImage(
            imageModel = {
                url
            },
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp))
                .graphicsLayer {
                    //alpha = 0.8f // 降低亮度
                }
                .clickable {
                    logger.info(url)
                    scope.launch {
                        Platform.openImage(url)
                    }
                }
                .fillMaxSize(),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop
            )
        )

         */
        /*
        KamelImage({ asyncPainterResource(url) }, null,
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp))
                .graphicsLayer {
                    //alpha = 0.8f // 降低亮度
                }
                .clickable {
                    logger.info(url)
                    scope.launch {
                        PlatformU.openImage(url)
                    }
                }
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

         */
        AsyncImage(url, null, modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .graphicsLayer {
                //alpha = 0.8f // 降低亮度
            }
            .clickable {
                logger.info(url)
                scope.launch {
                    PlatformU.openImage(url)
                }
            }
            .fillMaxSize(),
            contentScale = ContentScale.Crop)

        /*



       Image(
           rememberImagePainter(url), null,
           modifier = modifier
               .animateContentSize()
               .padding(8.dp)
               .clip(RoundedCornerShape(12.dp))
               .graphicsLayer {
                   //alpha = 0.8f // 降低亮度
               }
               .clickable {
                   logger.info(url)
                   scope.launch {
                       Desktop.getDesktop().open(file)
                   }
               }
               .fillMaxSize(),
           contentScale = ContentScale.Fit
       )

         */


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

