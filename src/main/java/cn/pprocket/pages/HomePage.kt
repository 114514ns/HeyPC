package cn.pprocket.pages

import App
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
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.components.PostCard
import cn.pprocket.items.Post
import cn.pprocket.items.Topic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomePage(navController: NavHostController) {
    var posts by remember { mutableStateOf(mutableListOf<Post>()) }
    LaunchedEffect(Unit) {
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
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
            //.then(navigationBarsPadding) // 填充导航栏之外剩余的空间
        ) {

            val windowSize = rememberWindowState()

            val columns = if (windowSize.placement == WindowPlacement.Fullscreen) 2 else 1
            LazyVerticalGrid(columns = GridCells.Fixed(columns)) {

                items(

                    posts.size,
                    key = { index -> Random().nextInt() }
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
                        onCardClick = {
                            GlobalState.map[post.postId] = post
                            navController.navigate("post/${post.postId}")
                        },
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

    var selectedItem by remember { mutableStateOf(0) }
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = null) },
                label = { Text(item.capitalize()) },
                selected = index == selectedItem,
                onClick = {
                    selectedItem = index

                },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
