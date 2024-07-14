package cn.pprocket.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import cn.pprocket.items.Comment
import com.lt.load_the_image.rememberImagePainter

@Composable
fun UserComment(comment: Comment,modifier: Modifier) {
    val post = comment.extraPost
    Card(modifier =modifier.fillMaxWidth()) {
        Row {
            Image(rememberImagePainter(comment.userAvatar),"", modifier = Modifier.size(64.dp).clip(CircleShape).padding(12.dp))
            Column() {
                Text(comment.userName)
                Spacer(modifier = Modifier.height(8.dp))
                Text(comment.createdAt)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(comment.content,modifier = Modifier.padding(12.dp))
        Row {
            Text(post.title,modifier = Modifier.padding(12.dp),color = Color(0xff8c9196))
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
