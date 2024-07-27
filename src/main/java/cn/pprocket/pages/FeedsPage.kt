package cn.pprocket.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.Logger
import cn.pprocket.components.PostCard
import cn.pprocket.items.Post
import cn.pprocket.items.Topic
import com.lt.load_the_image.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.management.ManagementFactory
import kotlin.math.log


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FeedsPage(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    val posts = rememberSaveable { mutableStateListOf<Post>() }
    var topic by rememberSaveable { mutableStateOf(Topic.LOVE) }
    var selected by rememberSaveable { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    var topics = rememberSaveable {
        mutableStateListOf<Topic>(
            Topic.LOVE,
            Topic.RECOMMEND,
            Topic.HOTS,
            Topic.SCHOOL,
            Topic.HARDWARE,
            Topic.DAILY,
            Topic("更多", 114514, "")
        )
    }

    val listState = rememberLazyGridState()
    val scrollState = rememberLazyListState()
    val logger = Logger("cn.pprocket.pages.FeedsPage")
    var firstVisibleItemIndex by remember { mutableStateOf(0) }
    val topicScroll = rememberLazyListState()
    var showSheet by remember { mutableStateOf(false) }
    var refresh by rememberSaveable { mutableStateOf(true) }
    logger.info("selected ${selected}")
    LaunchedEffect(selected) {
        if (selected == topics.size - 1) {
            logger.info("last")
            showSheet = true
        } else if (topics[selected].id != 114514 && refresh) {
            refresh = false
            withContext(Dispatchers.IO) {
                logger.info("selected ${topics[selected]}")
                val fetch = HeyClient.getPosts(topics[selected])
                val newList = mutableListOf<Post>()
                fetch.forEach { newList.add(it) }
                posts.clear()
                posts.addAll(newList)

                if (!GlobalState.started) {
                    val bean = ManagementFactory.getRuntimeMXBean()
                    logger.info("启动耗时：${bean.uptime / 1000.0} s")
                    GlobalState.started = true
                }
                logger.info("切换topic  ${topic}")
            }
            listState.scrollToItem(0, 0)
        }
    }
    Column {
        LazyRow(
            state = topicScroll,
            modifier = Modifier.draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        scrollState.scrollBy(-delta)
                    }
                },
            ),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(topics.size, key = { index -> topics[index].name }) { index ->
                val theTopic = topics[index]
                FilterChip(
                    onClick = {
                        topic = theTopic
                        selected = index
                        refresh = true
                    },
                    label = {
                        //Text(fixEncoding(theTopic.name))
                        Text(theTopic.name)
                    },
                    selected = index == selected,

                    )
            }
        }
        HorizontalScrollbar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 12.dp),
            adapter = rememberScrollbarAdapter(topicScroll),
        )

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
                        },
                        userAvatar = post.userAvatar,
                        imgs = post.images,
                        modifier = Modifier.animateItemPlacement(animationSpec = tween(500))
                    )
                }


            }

        }


    }
    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }) {
            FlowRow(modifier = Modifier.padding(12.dp)) {
                GlobalState.topicList.forEach {
                    AssistChip(
                        onClick = {
                            val currentId = it.id
                            if (topics.any { it.id == currentId }) {
                                var pos = -1
                                topics.forEach {
                                    if (it.id == currentId) {
                                        pos = topics.indexOf(it)
                                    }
                                }
                                if (pos != -1) {
                                    selected = pos
                                    topic = topics[pos]
                                    showSheet = false
                                }
                            } else {
                                topics[topics.size - 2] = it
                                selected = topics.size - 2
                                topic = topics[topics.size - 2]
                            }
                            refresh = true
                            showSheet = false
                        },
                        label = { Text(it.name) },
                        leadingIcon = {
                            Image(
                                rememberImagePainter(it.icon),
                                contentDescription = "Localized description",
                                Modifier.size(AssistChipDefaults.IconSize)
                            )
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

