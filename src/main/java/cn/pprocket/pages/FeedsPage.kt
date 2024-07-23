package cn.pprocket.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.FilterChip
import androidx.compose.material3.SnackbarHostState
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedsPage(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    val posts = rememberSaveable { mutableStateListOf<Post>() }
    var topic by rememberSaveable { mutableStateOf(Topic.LOVE) }
    var selected by rememberSaveable { mutableStateOf(0) }
    var lastSelected by rememberSaveable { mutableStateOf(0) }
    val topics = listOf(Topic.LOVE,Topic.RECOMMEND,Topic.HOTS, Topic.WORK, Topic.SCHOOL, Topic.HARDWARE, Topic.DAILY)
    val listState = rememberLazyGridState()
    val scrollState = rememberLazyListState()
    var firstVisibleItemIndex by remember { mutableStateOf(0) }
    LaunchedEffect(topic) {
        println("${topic}  ${selected}   ${lastSelected}")
        if (posts.isEmpty() || selected != lastSelected) {
            //println("刷新")
            withContext(Dispatchers.IO) {
                val fetch = HeyClient.getPosts(topics[selected])
                val newList = mutableListOf<Post>()
                fetch.forEach { newList.add(it) }
                //posts.forEach { newList.add(it) }
                posts.clear()
                posts.addAll(newList)
            }
            listState.scrollToItem(0, 0)
        }
    }
    Column {
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
                        println("current ${selected} last ${lastSelected}")
                        topic = theTopic
                    },
                    label = {
                        //Text(fixEncoding(theTopic.name))
                        Text(theTopic.name)
                    },
                    selected = index == selected,

                    )
            }
        }

        LaunchedEffect(scrollState) {
            snapshotFlow { scrollState.firstVisibleItemIndex }
                .collect { index ->
                    firstVisibleItemIndex = index
                }
        }
        var firstVisibleItemIndex by remember { mutableStateOf(0) }
        val scale by animateFloatAsState(if (firstVisibleItemIndex > 0) 0.5f else 1.0f)
        LaunchedEffect(scrollState) {

            snapshotFlow { scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }.collectLatest { lastIndex ->
                if (lastIndex != null && lastIndex >= posts.size - 3) {
                    // 在后台线程执行网络请求
                    withContext(Dispatchers.IO) {
                        val new = HeyClient.getPosts(topic)
                        posts.addAll(new)
                        posts.forEach {
                            GlobalState.map[it.postId] = it
                        }
                    }

                }
            }
        }

        LazyColumn(
            state = scrollState,
            flingBehavior = ScrollableDefaults.flingBehavior(),
            modifier = Modifier.fillMaxSize()
        ) {

            items(

                posts.size, key = { index -> posts[index].postId }) { index ->
                val post = posts[index]
                var visible by remember { mutableStateOf(true) }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    PostCard(
                        title = post.title,
                        author = post.userName,
                        content = post.description,
                        publishTime = post.createAt,
                        likesCount = post.likes,
                        commentsCount = post.comments,
                        onCardClick = {
                            GlobalState.map[post.postId] = post
                            navController.navigate("post/${post.postId}")
                            lastSelected = selected
                        },
                        userAvatar = post.userAvatar,
                        imgs = post.images,
                        modifier = Modifier.animateItemPlacement(animationSpec = tween(500))
                    )
                }


            }
        }


    }
}

