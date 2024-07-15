package cn.pprocket.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lt.load_the_image.rememberImagePainter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostCard(
    title: String,
    author: String,
    content: String,
    publishTime: String,
    likesCount: Int,
    commentsCount: Int,
    onCardClick: () -> Unit,
    userAvatar: String,
    imgs: List<String>,
    modifier: Modifier
) {
    ElevatedCard(
        onClick = onCardClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)


    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title.ifEmpty { "这个b没有写标题" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    rememberImagePainter(userAvatar),
                    "",
                    modifier = Modifier.size(48.dp).padding(6.dp).clip(CircleShape)
                )
                Text(
                    text = author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "•",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = publishTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            SelectableText(content)
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                imgs.forEach { img ->
                    Image(
                        painter = rememberImagePainter(img),
                        contentDescription = "",
                        modifier = Modifier.size(120.dp).padding(4.dp)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Likes",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = likesCount.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = "Comments",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = commentsCount.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun SelectableText(text: String, modifier: Modifier = Modifier) {
    // SelectionContainer allows text to be selectable
    SelectionContainer {

        /*
        BasicTextField(
            value = text,
            onValueChange = {},
            textStyle = TextStyle(fontSize = 18.sp),
            readOnly = true, // Make sure the text is read-only
            modifier = Modifier.fillMaxSize().padding(8.dp)
        )

         */
        Text(text,modifier = Modifier.fillMaxSize().padding(8.dp))
    }
}
