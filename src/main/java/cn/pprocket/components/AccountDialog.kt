package cn.pprocket.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.lt.load_the_image.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skiko.toBitmap
import java.awt.MultipleGradientPaint.CycleMethod.NO_CYCLE
import qrcode.QRCode
import qrcode.color.Colors
import qrcode.color.Colors.css
import java.awt.Color
import java.awt.RadialGradientPaint
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Composable
fun AccountDialog(onDismissRequest: () -> Unit) {
    var qrCodeImage by remember { mutableStateOf<BufferedImage?>(null) }
    var url by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        url = HeyClient.genQRCode()
        qrCodeImage = generateQRCodeImage(url, 300, 300)
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            while (true) {
                if (HeyClient.checkLogin(url)) {
                    GlobalState.config.cookies = HeyClient.cookie
                    GlobalState.config.isLogin = true
                    GlobalState.config.user = HeyClient.user
                    break
                }
                Thread.sleep(500)
            }
            withContext(Dispatchers.Default) {
                onDismissRequest()
            }
        }
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        val color = MaterialTheme.colorScheme.primary
        val code = QRCode.ofSquares()
            .withColor(Colors.rgba(99,160, 2, 255))
            .build(url)

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
                if (qrCodeImage != null) {
                    //Image(painter = rememberImagePainter(qrCodeImage!!.toBitmap()), "")
                    Image(painter = rememberImagePainter(code.renderToBytes()), "")
                }
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

fun generateQRCodeImage(text: String, width: Int, height: Int): BufferedImage {
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)

    // 创建带有透明背景的BufferedImage
    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val graphics = bufferedImage.createGraphics()

    // 设置透明背景
    graphics.color = Color(0, 0, 0, 0)
    graphics.fillRect(0, 0, width, height)

    // 绘制二维码
    graphics.color = Color.BLACK
    for (x in 0 until width) {
        for (y in 0 until height) {
            if (bitMatrix[x, y]) {
                graphics.fillRect(x, y, 1, 1)
            }
        }
    }

    graphics.dispose()
    return bufferedImage
}

fun BufferedImage.toImageBitmap(): ImageBitmap {
    val outputStream = ByteArrayOutputStream()
    ImageIO.write(this, "png", outputStream)
    val byteArray = outputStream.toByteArray()
    return androidx.compose.ui.res.loadImageBitmap(byteArray.inputStream())
}
