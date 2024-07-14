package cn.pprocket.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient.fetchPosts
import cn.pprocket.HeyClient.getPosts
import cn.pprocket.components.PostCard
import cn.pprocket.items.Post
import cn.pprocket.items.User
import com.lt.load_the_image.rememberImagePainter

@Composable
fun UserPage(navController: NavHostController, userId: String) {
    val user: User = GlobalState.users[userId]!!
    Box {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(modifier = Modifier.fillMaxHeight(0.08f)) {
                Image(rememberImagePainter(user.avatar), "")
                Row {
                    Card {
                        Text(user.userName, modifier = Modifier.padding(16.dp))
                        Text(user.signature, modifier = Modifier.padding(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Card {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(user.followings.toString(), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("关注", textAlign = TextAlign.Center, color = Color.Gray)
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(user.followers.toString(), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "粉丝", textAlign = TextAlign.Center, color = Color.Gray)
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            user.location.toString().replace("IP:", ""),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("归属地", textAlign = TextAlign.Center, color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            var tabIndex by rememberSaveable { mutableStateOf(0) }
            val tabs = listOf("动态", "评论")
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        icon = {
                            when (index) {
                                0 -> Icon(imageVector = Icons.Default.Home, contentDescription = null)
                                1 -> Icon(imageVector = Icons.Default.Info, contentDescription = null)
                                2 -> Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                            }
                        }
                    )
                }
            }
            var page = 0
            val postList = remember { mutableListOf<Post>() }
            LaunchedEffect(Unit) {
                postList.clear()
                val fetch = user.fetchPosts(page++)
                fetch.forEach { postList.add(it) }
            }
            when (tabIndex) {
                0 -> {
                    LaunchedEffect(userId) {
                        postList.clear()
                        val fetch = user.fetchPosts(page++)
                        fetch.forEach { postList.add(it) }
                    }
                    LazyColumn(state = rememberLazyListState()) {
                        items(postList.size,key = {index -> postList[index].postId}) { index ->
                            val post = postList[index]
                            PostCard(
                                title = post.title,
                                content = post.content,
                                author = post.userName,
                                publishTime = post.createAt,
                                likesCount = post.likes,
                                commentsCount = post.comments,
                                imgs = post.images,
                                userAvatar = post.userAvatar,
                                onCardClick = {
                                    GlobalState.map[post.postId] = post
                                    navController.navigate("post/${post.postId}")
                                }
                            )
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { navController.popBackStack() },
            content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "发表评论") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp)
        )

    }


}
