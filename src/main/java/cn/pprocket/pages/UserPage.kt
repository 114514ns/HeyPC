package cn.pprocket.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.HeyClient.fetchComments
import cn.pprocket.HeyClient.fetchPosts
import cn.pprocket.HeyClient.getPosts
import cn.pprocket.components.PostCard
import cn.pprocket.components.SelectableText
import cn.pprocket.components.UserComment
import cn.pprocket.items.Comment
import cn.pprocket.items.Post
import cn.pprocket.items.User
import com.lt.load_the_image.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserPage(navController: NavHostController, userId: String) {
    val user: User = GlobalState.users[userId]!!
    Box {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Card(modifier = Modifier.fillMaxHeight(0.08f).fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Image(rememberImagePainter(user.avatar), "", modifier = Modifier.clip(CircleShape))
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        SelectableText(user.userName)
                        SelectableText(user.signature, modifier = Modifier.padding(16.dp))
                    }
                }

            }
            Spacer(modifier = Modifier.height(32.dp))
            Card {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(
                        modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(user.followings.toString(), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("关注", textAlign = TextAlign.Center, color = Color.Gray)
                    }
                    Column(
                        modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(user.followers.toString(), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "粉丝", textAlign = TextAlign.Center, color = Color.Gray)
                    }
                    Column(
                        modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            user.location.toString().replace("IP:", ""), textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("归属地", textAlign = TextAlign.Center, color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            var tabIndex by remember { mutableStateOf(0) }
            val tabs = listOf("动态", "评论")
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(title) }, selected = tabIndex == index, onClick = {
                        tabIndex = index
                        println("Index = $index")
                    }, icon = {
                        when (index) {
                            0 -> Icon(imageVector = Icons.Default.Home, contentDescription = null)
                            1 -> Icon(imageVector = Icons.Default.Info, contentDescription = null)
                        }
                    })
                }
            }
            var page = 0
            val postList = remember { mutableListOf<Post>() }
            var commentPage = 1
            val comments = remember { mutableListOf<Comment>() }
            LaunchedEffect(userId) {
                withContext(Dispatchers.IO) {
                    postList.clear()
                    val fetch = user.fetchPosts(page++)
                    fetch.forEach {
                        it.userAvatar = user.avatar
                        it.userName = user.userName
                        postList.add(it)
                    }
                }
            }
            LaunchedEffect(userId) {
                withContext(Dispatchers.IO) {
                    val fetch = user.fetchComments(commentPage++)
                    comments.clear()
                    comments.addAll(fetch)
                }
            }
            when (tabIndex) {
                0 -> {

                    LazyColumn(state = rememberLazyListState()) {
                        items(postList.size, key = { index -> postList[index].postId }) { index ->
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
                                },
                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }
                }

                1 -> {


                    LazyColumn {
                        println(comments.size)
                        items(comments.size, key = { index -> comments[index].commentId }) { index ->
                            val comment = comments[index]
                            UserComment(comment,modifier = Modifier.padding(16.dp).animateItemPlacement(
                            ))
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
