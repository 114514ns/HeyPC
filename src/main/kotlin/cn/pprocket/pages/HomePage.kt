package cn.pprocket.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.pprocket.HeyClient
import cn.pprocket.components.PostCard
import cn.pprocket.items.Post
import cn.pprocket.items.Topic
import com.lt.load_the_image.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomePage(navController: NavHostController) {
    var posts by remember { mutableStateOf(mutableListOf<Post>()) }
    LaunchedEffect(navController) {
        withContext(Dispatchers.IO) {
            var fetch = HeyClient.getPosts(Topic.LOVE)
            val newList = mutableListOf<Post>()
            fetch.forEach { newList.add(it) }
            posts.forEach { newList.add(it) }
            posts = newList

        }
    }
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        val navigationBarsPadding = Modifier.navigationBarsPadding()
        Column (
                verticalArrangement = Arrangement.spacedBy(16.dp)
                //.then(navigationBarsPadding) // 填充导航栏之外剩余的空间
        ) {
            LazyVerticalGrid(columns = GridCells.Fixed(1)) {

                items (
                    posts.size,
                    key ={index -> Random().nextInt()}
                )
                    { index ->
                        val post = posts[index]
                        PostCard(
                            title = post.title,
                            author = post.userName,
                            content = post.description,
                            publishTime = post.createAt,
                            likesCount = post.likes,
                            commentsCount = post.comments,
                            sharesCount = 20,
                            onCardClick = {},
                            userAvatar = post.userAvatar,
                            imgs = post.images
                        )

                    }

            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    //var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("home", "profile", "settings")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Person,
        Icons.Default.Settings
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = null) },
                label = { Text(item.capitalize()) },
                selected = true,
                onClick = {
                    /*
                    selectedItem = index
                    navController.navigate(item) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }

                     */
                },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
