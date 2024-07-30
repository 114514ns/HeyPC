package cn.pprocket.components

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
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
import cn.pprocket.HeyClient
import cn.pprocket.Logger
import cn.pprocket.pages.getImageDir
import cn.pprocket.pages.urlToFileName
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.awt.Desktop
import java.io.File
import java.net.URL

@Composable
fun ContextImage(scope: CoroutineScope, img: String) {
    var showSticker by remember { mutableStateOf(false) }
    val url = transformImage(img)
    val logger = Logger("cn.pprocket.components.ContextImage")
    if (showSticker) {
        StickerDialog({ showSticker = false }, img)
    }
    ContextMenuArea(
        items = {
            listOf(
                ContextMenuItem("添加表情包") {
                    showSticker = true
                }
            )
        }
    ) {
        Box {
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
                            val fileName = urlToFileName(url)
                            val file = File(getImageDir(), fileName)
                            val request = Request.Builder().url(url).build()
                            HeyClient.cosClient.newCall(request).execute().use { response ->
                                if (response.isSuccessful) {
                                    withContext(Dispatchers.IO) {
                                        response.body?.byteStream()?.copyTo(file.outputStream())
                                    }
                                    Desktop.getDesktop().open(file)
                                }
                            }
                        }
                    }
                    .fillMaxSize(),
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop
                )
            )
        }
    }
}

fun transformImage(string: String): String {
    if (GlobalState.config.isOriginImage) {
        val url = URL(string)
        val processedPath = url.protocol + "://" + url.host + url.path
        val ext = processedPath.split(".").last()

        var v0 = processedPath.replace("/thumb.${ext}", "")
        return "${v0}.${ext}"

    } else {
        return string
    }
}