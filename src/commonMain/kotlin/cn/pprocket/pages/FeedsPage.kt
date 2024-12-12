package cn.pprocket.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import client
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.Logger
import cn.pprocket.components.PostCard
import cn.pprocket.components.SearchArea
import cn.pprocket.items.Post
import cn.pprocket.items.Topic
import cn.pprocket.ui.PlatformU
import coil3.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import logger
import kotlin.random.Random


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FeedsPage(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    topicArg: Topic? = null,
    keyWord: String? = null
) {




    val posts = if (PlatformU.getPlatform() == "Android") {

        rememberSaveable(
            saver = Saver(
                save = { Json.encodeToString(it.toList())},
                restore = { jsonString ->
                    val list = Json.decodeFromString<List<Post>>(jsonString) // 从 JSON 恢复为普通列表
                    list.toMutableStateList()
                }
            )
        ) { mutableStateListOf<Post>() }
    } else {
        rememberSaveable { mutableStateListOf() }
    }

    //var topic by remember {mutableStateOf(Topic("", 0, ""))}

    var topic by rememberSaveable(
        saver = Saver(
            save = { listOf(it.value.id, it.value.name, it.value.icon) }, // 保存为 List
            restore = { mutableStateOf(Topic(it[1] as String, it[0] as Int, it[2] as String)) } // 从 List 恢复
        )
    ) { mutableStateOf(Topic("", 0, "")) }


    var selected by rememberSaveable { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    /*
    val topics = rememberSaveable(
        saver = listSaver<MutableList<Topic>, Topic>(
            save = { it }, // 直接保存列表
            restore = { it.toMutableStateList() } // 恢复为 SnapshotStateList
        )
    ) {
        mutableStateListOf()
    }

     */

    val topics = remember { mutableStateListOf<Topic>() }
    LaunchedEffect(Unit) {


        val g = client.getDefaultTopics()
        topic = g[0]
        topics.addAll(g)

    }


    val listState = rememberLazyGridState()
    val scrollState = rememberLazyStaggeredGridState()
    val logger = Logger("cn.pprocket.pages.FeedsPage")
    var firstVisibleItemIndex by remember { mutableStateOf(0) }
    val topicScroll = rememberLazyListState()
    var showSheet by remember { mutableStateOf(false) }
    var refresh by rememberSaveable { mutableStateOf(true) }
    var cell by remember { mutableStateOf(1) }
    var showSearch by remember { mutableStateOf(false) }
    var searchPage by remember { mutableStateOf(1) }
    var scope = rememberCoroutineScope()
    LaunchedEffect(selected) {
        if (topics.size < 2) {
            return@LaunchedEffect
        }
        if (selected == topics.size - 1) {
            logger.info("last")
            showSheet = true
            logger.info("last0")
        } else if (topics[selected].id != 114514 && refresh) {
            refresh = false
            withContext(Dispatchers.Default) {
                coroutineScope.launch {
                    //scrollState.animateScrollToItem(0, 0)
                }
                logger.info("selected ${topics[selected]}")
                val fetch = if (keyWord == null) {
                    client.getPosts(topics[selected])
                } else {
                    client.searchPost(keyWord, searchPage++)
                }
                val newList = mutableListOf<Post>()
                fetch.forEach { newList.add(it) }
                posts.clear()
                posts.addAll(newList)


                logger.info("切换topic  ${topic}")
            }
            listState.scrollToItem(0, 0)
        }
    }
    LaunchedEffect(Unit) {

        if (topicArg != null) {
            logger.info("topic argument")
            if (topics.any { it.id == topicArg.id }) {
                selected = topics.indexOfLast { it.id == topicArg.id }
            } else {
                selected = topics.size - 2
                topics[topics.size - 2] = topicArg
            }
        }
        //GlobalState.users["36331242"] = HeyClient.getUser("36331242")
        //navController.navigate("user/36331242")
    }
    LaunchedEffect(Unit) {

        while (true) {
            cell = if (PlatformU.isFullScreen()) 2 else 1
            delay(40)
        }
    }
    Box {

        Column {
            if (topicArg == null && keyWord == null) {
                LazyRow(
                    state = topicScroll,
                    modifier = Modifier.draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            coroutineScope.launch {
                                scrollState.scrollBy(-delta)
                            }
                        },
                    ).padding(top = if (PlatformU.getPlatform() == "Android") 25.dp else 0.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(topics.distinctBy { it.name }.size, key = { index -> topics[index].name }) { index ->
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
            }


            LaunchedEffect(scrollState) {
                snapshotFlow { scrollState.firstVisibleItemIndex }
                    .collect { index ->
                        firstVisibleItemIndex = index
                    }
            }
            LaunchedEffect(scrollState) {

                snapshotFlow { scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }.collectLatest { lastIndex ->
                    if (lastIndex != null && lastIndex >= posts.size - 3) {
                        // 在后台线程执行网络请求
                        val new = if (keyWord == null) {
                            client.getPosts(topic)
                        } else {
                            client.searchPost(keyWord, searchPage++)
                        }
                        new.forEach {
                            if (it.postId != "" && !posts.contains(it)) {
                                posts.add(it)
                            }
                        }
                        posts.forEach {
                            GlobalState.map[it.postId] = it
                        }

                    }
                }
            }


            AnimatedVisibility(showSearch) {
                SearchArea(navController)
            }
            LazyVerticalStaggeredGrid(
                StaggeredGridCells.Fixed(cell),
                state = scrollState,
                modifier = Modifier.fillMaxHeight(1f)
            ) {
                items(

                    posts.size, key = { index -> posts[index].postId + Random.nextInt() }) { index ->
                    val post = posts[index]
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
                        modifier = Modifier.animateItemPlacement(
                            tween(durationMillis = 250)
                        ),
                        scope = coroutineScope
                    )


                }
            }
            /*

            LazyColumn(
                state = scrollState,
                flingBehavior = ScrollableDefaults.flingBehavior(),
                modifier = Modifier.fillMaxSize()
            ) {

                items(

                    posts.size, key = { index -> posts[index].postId }) { index ->
                    val post = posts[index]
                    var visible by remember { mutableStateOf(true) }




                }

            }

             */


        }
        if (topicArg != null || keyWord != null) {
            FloatingActionButton(
                onClick = {
                    navController.popBackStack()
                },
                content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "发表评论") },
                modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp)
            )
        }
        FloatingActionButton(
            onClick = {
                showSearch = !showSearch
            },
            content = { Icon(Icons.Filled.Search, contentDescription = "发表评论") },
            modifier = Modifier.align(Alignment.TopEnd).padding(32.dp, 96.dp)
        )
        FloatingActionButton(
            onClick = {
                scope.launch {
                    posts.clear()
                    val new = if (keyWord == null) {
                        client.getPosts(topic)
                    } else {
                        client.searchPost(keyWord, searchPage++)
                    }
                    posts.addAll(new)
                    posts.forEach {
                        GlobalState.map[it.postId] = it
                    }
                }

            },
            content = { Icon(Icons.Filled.Refresh, contentDescription = "发表评论") },
            modifier = Modifier.align(Alignment.TopEnd).padding(32.dp, 160.dp)
        )
    }


    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false;selected = 1 }) {
            val state = rememberScrollState()
            FlowRow(modifier = Modifier.padding(12.dp).scrollable(state, Orientation.Horizontal)) {
                GlobalState.topicList.forEach {
                    AssistChip(
                        onClick = {
                            coroutineScope.launch {
                                scrollState.animateScrollToItem(0)
                            }
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

                            AsyncImage(it.icon, null, Modifier.size(AssistChipDefaults.IconSize))
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

