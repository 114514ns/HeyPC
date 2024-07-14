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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun CacheDialog(
    onDismissRequest: () -> Unit, snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    Dialog(onDismissRequest = { onDismissRequest() }) {

        Card(
            modifier = Modifier.fillMaxWidth().height(375.dp).padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                Row {
                    TextItem("清除图片缓存", "已缓存521MB", onClick = {
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
