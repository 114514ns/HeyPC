package cn.pprocket.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.pprocket.HeyClient
import cn.pprocket.pages.getStickerDir
import cn.pprocket.sticker.Sticker
import cn.pprocket.sticker.StickerManager
import com.skydoves.landscapist.coil3.CoilImage
import kotlinx.coroutines.launch
import okhttp3.Request
import java.io.File
import java.security.MessageDigest

@Composable
fun StickerDialog(onDismissRequest : (() -> Unit),image:String) {
    var keyword by remember { mutableStateOf("默认") }
    val scope = rememberCoroutineScope()
    AlertDialog(
        title = {
            Text(text = "添加收藏")
        },
        text = {

            Column {
                CoilImage(imageModel = {image}, modifier = Modifier.padding(16.dp))
                OutlinedTextField(keyword, onValueChange = { text -> keyword = text}, modifier = Modifier.fillMaxWidth())
            }

        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        val bytes = HeyClient.cosClient.newCall(Request.Builder().get().url(image).build()).execute().body!!.bytes()
                        val file = File(getStickerDir(), MD5(image) +"." +  image.split(".").last())
                        file.createNewFile()
                        file.writeBytes(bytes)
                        val sticker = Sticker(keyword,file.absolutePath)
                        StickerManager.add(sticker)
                    }

                    onDismissRequest()
                },
                enabled = keyword.isNotBlank()
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("取消")
            }
        }
    )
}

@OptIn(ExperimentalStdlibApi::class)
fun MD5(text: String):String {
    val instance =  MessageDigest.getInstance("MD5")
    instance.update(text.toByteArray())
    return instance.digest().toHexString()
}
