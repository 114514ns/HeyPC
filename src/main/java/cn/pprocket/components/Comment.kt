package cn.pprocket.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.pprocket.items.Comment
import cn.pprocket.pages.getImagePath
import cn.pprocket.pages.urlToFileName
import com.lt.load_the_image.rememberImagePainter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Comment(comment: Comment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(comment.userAvatar),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = comment.userName,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = comment.createdAt,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = comment.content)
        Spacer(modifier = Modifier.height(8.dp))
        comment.images.forEach {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = "Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .padding(4.dp)
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
                    .onClick {
                        Runtime.getRuntime().exec("cmd /c " + getImagePath(urlToFileName(it)))
                    }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

    }
}
