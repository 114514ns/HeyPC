package cn.pprocket.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cn.pprocket.pages.TextItem
import cn.pprocket.pages.getImageDir
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun CacheDialog(
    onDismissRequest: () -> Unit, snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val dir = getImageDir()
    Dialog(onDismissRequest = { onDismissRequest() }) {

        Card(
            modifier = Modifier.fillMaxWidth().height(375.dp).padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                Row {
                    TextItem("清除图片缓存", formatBytes(dir.totalSpace), onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("on")
                            onDismissRequest()

                        }

                    })
                }
            }
        }
    }
}
fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"

    val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
    var value = bytes.toDouble()
    var index = 0

    while (value >= 1024 && index < units.size - 1) {
        value /= 1024
        index++
    }

    return String.format("%.2f %s", value, units[index])
}
