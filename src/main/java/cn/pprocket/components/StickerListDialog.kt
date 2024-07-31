package cn.pprocket.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cn.pprocket.sticker.Sticker
import cn.pprocket.sticker.StickerManager
import com.skydoves.landscapist.coil3.CoilImage
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StickerListDialog(onDismissRequest: () -> Unit, image: MutableList<Image>? = null) {
    var keyword by remember { mutableStateOf("默认") }
    val scope = rememberCoroutineScope()
    var exactly by remember { mutableStateOf(false) }
    var list = rememberSaveable { StickerManager.search(keyword, exactly) }
    println(list.size)
    AlertDialog(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "搜索表情",
                    modifier = Modifier.align(Alignment.TopStart)
                )

                Column(modifier = Modifier.align(Alignment.TopEnd)) {
                    Text("严格匹配")
                    Switch(
                        checked = exactly,
                        onCheckedChange = { newValue ->
                            exactly = newValue
                            list = StickerManager.search(keyword, newValue)
                        },

                        )
                }
            }
        },
        text = {

            Column(modifier = Modifier.animateContentSize()) {
                TextField(
                    keyword,
                    onValueChange = { value -> keyword = value;list = StickerManager.search(value, exactly) })
                LazyColumn {
                    items(list.size, { index -> list[index].filePath }) { index ->
                        StickerItem(
                            sticker = list[index],
                            modifier = Modifier.animateItemPlacement(),
                            onClick = { e ->
                                if (image != null) {
                                    image.add(e)
                                }
                            })
                    }
                }
            }

        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {

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


@Composable
fun StickerItem(sticker: Sticker, modifier: Modifier = Modifier, onClick: (Image) -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .then(modifier)
            .clickable {
                val image = ImageIO.read(File(sticker.filePath))
                onClick(image)

            },

        verticalAlignment = Alignment.CenterVertically
    ) {
        CoilImage(
            imageModel = { "file://${sticker.filePath.replace("\\", "/")}" },
            modifier = Modifier
                .size(64.dp) // Limit the size of the image
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = sticker.keyword,
            modifier = Modifier
                .weight(1f) // Allow text to take up remaining space
                .padding(start = 8.dp)
        )
    }
}
