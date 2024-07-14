package cn.pprocket.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cn.pprocket.pages.TextItem
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.net.URI

@Composable
fun AboutDialog(
    onDismissRequest: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column {
                    TextItem("项目地址","点击跳转到项目仓库",onClick = {
                        Desktop.getDesktop().browse(URI("https://github.com/114514ns/HeyPC"))
                    })
                    Spacer(modifier = Modifier.width(16.dp))
                    TextItem("开源协议","GPL-V3",onClick = {})

                }
            }
        }
    }
}
