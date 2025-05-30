package cn.pprocket.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.pprocket.ui.PlatformU
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostCard(
    title: String,
    author: String,
    content: String,
    publishTime: String,
    likesCount: String,
    commentsCount: Int,
    onCardClick: () -> Unit,
    userAvatar: String,
    imgs: List<String>,
    modifier: Modifier,
    scope: CoroutineScope
) {

    Card(
        modifier = modifier.fillMaxWidth().padding(12.dp, vertical = 8.dp).clickable { onCardClick() }.background(MaterialTheme.colorScheme.surface),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title.ifEmpty { "这个b没有写标题" },
                style = PlatformU.getTypography().headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContextImage(
                    scope,
                    userAvatar,
                    modifier = modifier
                        .clip(CircleShape)
                        .size(64.dp)
                    //modifier = Modifier.size(72.dp).padding(6.dp).clip(CircleShape),
                )
                Text(
                    text = author,
                    style = PlatformU.getTypography().bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = PlatformU.getTypography().bodyMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = publishTime,
                    style = PlatformU.getTypography().bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            SelectableText(content)
            Spacer(modifier = Modifier.height(16.dp))
            FlowRow {
                imgs.forEach { img ->
                    ContextImage(
                        scope,
                        img,
                        modifier = Modifier.animateContentSize().size(150.dp)
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
                    text = likesCount, style = PlatformU.getTypography().bodySmall
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.List, contentDescription = "Comments", modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = commentsCount.toString(), style = PlatformU.getTypography().bodySmall
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

