package cn.pprocket.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.items.Comment
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UserComment(comment: Comment,modifier: Modifier,navController: NavHostController) {
    //val post = comment.extraPost
    val scope = rememberCoroutineScope()
    Card(modifier =modifier.fillMaxWidth().clickable {

        scope.launch {
            withContext(Dispatchers.Default) {

                GlobalState.map[comment.postId] = HeyClient.getPost(comment.postId)
            }
        }

        navController.navigate("post/${comment.postId}")
    }) {
        Row {
            KamelImage(asyncPainterResource(comment.userAvatar),"", modifier = Modifier.size(64.dp).clip(CircleShape).padding(12.dp))
            Column() {
                Text(comment.userName,modifier = Modifier.padding(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(comment.createdAt)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(comment.content,modifier = Modifier.padding(12.dp))
        Row {
            Text(comment.postTitle,modifier = Modifier.padding(12.dp),color = Color(0xff8c9196))
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
