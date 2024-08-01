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
import kotlinx.coroutines.*
import okhttp3.Request
import java.awt.Desktop
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.File
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO




@Composable
fun ContextImage(scope: CoroutineScope, img: String,modifier: Modifier=Modifier) {
    var showSticker by remember { mutableStateOf(false) }
    val url = transformImage(img)
    val logger = Logger("cn.pprocket.components.ContextImage")
    var file = File(getImageDir(), urlToFileName(url))
    if (showSticker) {
        StickerDialog({ showSticker = false }, getOriginalImage(img))
    }
    LaunchedEffect(Unit) {
        val request = Request.Builder().url(url).build()
        withContext(Dispatchers.IO) {
            HeyClient.cosClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val arr = response.body?.bytes()
                    val stream = file.outputStream()
                    stream.write(arr)
                    stream.close()
                }
            }
        }
    }
    ContextMenuArea(
        items = {
            listOf(
                ContextMenuItem("添加表情包") {
                    showSticker = true
                },
                ContextMenuItem("复制图片") {

                    scope.launch {
                        withContext(Dispatchers.IO) {
                            val image: Image = ImageIO.read(file)
                            setClipboardImage(image)
                        }
                    }
                }
            )
        }
    ) {
        Box(modifier) {
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
                            Desktop.getDesktop().open(file)
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
        return getOriginalImage(string)

    } else {
        return string
    }
}
fun getOriginalImage(string: String):String {
    val url = URL(string)
    val processedPath = url.protocol + "://" + url.host + url.path
    val ext = processedPath.split(".").last()

    var v0 = processedPath.replace("/thumb.${ext}", "").replace("/format.${ext}","")
    return "${v0}.${ext}"
}
fun setClipboardImage(image: Image) {
    val trans: Transferable = object : Transferable {
        override fun getTransferDataFlavors(): Array<DataFlavor> {
            return arrayOf(DataFlavor.imageFlavor)
        }

        override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
            return DataFlavor.imageFlavor.equals(flavor)
        }

        @Throws(UnsupportedFlavorException::class, IOException::class)
        override fun getTransferData(flavor: DataFlavor): Any {
            if (isDataFlavorSupported(flavor)) return image
            throw UnsupportedFlavorException(flavor)
        }
    }
    Toolkit.getDefaultToolkit().systemClipboard.setContents(
        trans,
        null
    )
}
