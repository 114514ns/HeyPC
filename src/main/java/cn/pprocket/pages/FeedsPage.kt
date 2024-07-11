package cn.pprocket.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Text
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.components.PostCard
import cn.pprocket.items.Post
import cn.pprocket.items.Topic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import java.nio.charset.Charset
import java.util.*

@Composable
fun FeedsPage(navController : NavHostController) {
    var posts by rememberSaveable { mutableStateOf(mutableListOf<Post>()) }
    var topic by remember { mutableStateOf(Topic.LOVE) }
    var selected by remember { mutableStateOf(0) }
    var lastSelected by remember { mutableStateOf(0) }
    val topics = listOf(Topic.LOVE, Topic.WORK, Topic.SCHOOL, Topic.HARDWARE, Topic.DAILY)
    val listState = rememberLazyGridState()

    Column {
        LaunchedEffect(topic) {
            if (posts.isEmpty() || selected != lastSelected) {
                withContext(Dispatchers.IO) {
                    val fetch = HeyClient.getPosts(topics[selected])
                    val newList = mutableListOf<Post>()
                    fetch.forEach { newList.add(it) }
                    //posts.forEach { newList.add(it) }
                    posts = newList


                }
                listState.scrollToItem(0, 0)
            }
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(topics.size, key = { index -> topics[index].name }) { index ->
                val theTopic = topics[index]
                FilterChip(
                    onClick = {
                        lastSelected = selected
                        selected = index
                        topic = theTopic
                    },
                    label = {
                        Text(fixEncoding(theTopic.name))
                    },
                    selected = index == selected,

                    )
            }
        }
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collectLatest { lastIndex ->
                    if (lastIndex != null && lastIndex >= posts.size - 3) {
                        // 在后台线程执行网络请求
                        withContext(Dispatchers.IO) {
                            val new = HeyClient.getPosts(topic)
                            posts.addAll(new)
                        }
                    }
                }
        }
        LazyVerticalGrid(columns = GridCells.Fixed(1), state = listState) {

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
fun fixEncoding(str: String): String {
    val bytes = str.toByteArray(Charset.forName("GBK"))
    return String(bytes, Charsets.UTF_8)
}
