package cn.pprocket.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import coil3.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


@Composable
fun AccountDialog(onDismissRequest: () -> Unit) {
    var url by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        url = HeyClient.genQRCode()
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            while (true) {
                if (HeyClient.checkLogin(url)) {
                    GlobalState.config.cookies = HeyClient.cookie
                    GlobalState.config.isLogin = true
                    GlobalState.config.user = HeyClient.user
                    GlobalState.users[HeyClient.user.userId] = HeyClient.user
                    break
                }
                delay(200)
            }
            withContext(Dispatchers.Default) {
                onDismissRequest()
            }
        }
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {

        Card(
            modifier = Modifier.fillMaxWidth().height(450.dp).padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                //verticalArrangement = Arrangement.Center
            ) {

                Text("请使用小黑盒app扫描二维码", modifier = Modifier.padding(top = 15.dp))
                //Image(painter = rememberImagePainter(qrCodeImage!!.toBitmap()), "")
                AsyncImage("https://api.cl2wm.cn/api/qrcode/code?text=${url}&mhid=sELPDFnok80gPHovKdI",null)

                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}
