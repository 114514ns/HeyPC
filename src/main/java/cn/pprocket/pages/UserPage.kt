package cn.pprocket.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.items.User
import com.lt.load_the_image.rememberImagePainter

@Composable
fun UserPage(navController: NavHostController, userId: String) {
    val user : User = GlobalState.users[userId]!!
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxHeight(0.3f)) {
           Image(rememberImagePainter(user.avatar), "", modifier = Modifier.fillMaxSize())
            Row {
                Text(user.userName, modifier = Modifier.padding(16.dp))
                Text(user.signature, modifier = Modifier.padding(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Column {
                Text(user.followings.toString())
                Spacer(modifier = Modifier.height(16.dp))
                Text("关注")
            }
            Column {
                Text(user.followers.toString())
                Spacer(modifier = Modifier.height(16.dp))
                Text("粉丝")
            }
            Column {
                Text(user.location.toString())
                Spacer(modifier = Modifier.height(16.dp))
                Text("归属地")
            }
        }
    }
}
