package cn.pprocket.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ImagePage(imageUrl: String, onClose: () -> Unit) {
    var scale by remember { mutableStateOf(1f) } // 缩放
    var offset by remember { mutableStateOf(Offset(0f, 0f)) } // 平移
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    scale = (scale * zoom).coerceIn(1f, 5f)

                    offset = Offset(
                        x = offset.x + pan.x,
                        y = offset.y + pan.y
                    )
                }
            }
    ) {
        AsyncImage(
            model = Base64.decode(imageUrl.replace("1145141919810","")).decodeToString(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                )
                .clickable { onClose() },
            contentScale = ContentScale.Fit
        )

    }
}