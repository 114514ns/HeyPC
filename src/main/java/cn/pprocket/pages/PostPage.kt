package cn.pprocket.pages

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.Logger
import cn.pprocket.components.Comment
import cn.pprocket.components.SelectableText
import cn.pprocket.items.Comment
import cn.pprocket.items.Post
import com.lt.load_the_image.rememberImagePainter
import com.lt.load_the_image.util.MD5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
@Preview
fun PostPage(
    navController: NavHostController, postId: String, snackbarHostState: androidx.compose.material3.SnackbarHostState
) {
    var post = GlobalState.map[postId] ?: return
    var content by rememberSaveable { mutableStateOf(post.description) }
    val state = rememberScrollState()
    var comments by remember { mutableStateOf(emptyList<Comment>()) }
    var gridHeight by remember { mutableStateOf(0.dp) }
    var page = 1
    val scope = rememberCoroutineScope()
    val logger = Logger("cn.pprocket.pages.PostPage")
    var showSheet by remember { mutableStateOf(false) }
    var ready by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        logger.info(" ${post.postId}  ${post.title}")
        withContext(Dispatchers.IO) {
            var str = ""
            try {
                str = post.fillContent()
            } catch (e:NullPointerException) {
                logger.error("post.fillContent() error: $e")
                logger.error("postId $postId  title ${post.title}")
                throw e
            }
            content = str
        }
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            comments = HeyClient.getComments(postId, 1)
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp).verticalScroll(state)) {

            Spacer(modifier = Modifier.height(8.dp))

            // 作者信息
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = {
                GlobalState.users[post.userId] = HeyClient.getUser(post.userId)
                navController.navigate("user/${post.userId}")
            })) {
                Image(
                    painter = rememberImagePainter(
                        post.userAvatar
                    ),
                    contentDescription = "作者头像",
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = post.userName)
                    Text(text = post.createAt)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 帖子内容
            Card {
                SelectableText(
                    text = content, modifier = Modifier.padding(16.dp).fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 帖子图片
            post.images.forEach {
                Image(
                    painter = rememberImagePainter(it),
                    contentDescription = "帖子图片",
                    modifier = Modifier.fillMaxSize().padding(8.dp).clip(RoundedCornerShape(12.dp)).onClick {
                        Runtime.getRuntime().exec("cmd /c " + getImagePath(urlToFileName(it)))
                    },
                    contentScale = ContentScale.Fit,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            /*
            Row {
                post.tags.forEach {
                    AssistChip(
                        onClick = { },
                        label = { Text(it) },
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

             */
            FlowRow {
                post.tags.forEach {
                    AssistChip(onClick = { }, label = { Text(it) }, modifier = Modifier.padding(10.dp)
                    )
                }
            }
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
            val listState = rememberLazyListState()

            LazyColumn(
                state = listState,
                modifier = Modifier.height(1800.dp),
                flingBehavior = ScrollableDefaults.flingBehavior()
            ) {
                items(comments.size, key = { index -> comments[index].commentId }) { index ->
                    val comment = comments[index]
                    Comment(comment, navController,postId)
                }
            }
            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }.collectLatest { lastIndex ->
                    // 如果最后一个可见的项目是列表中的最后一个项目，那么加载更多数据
                    if (lastIndex != null && lastIndex >= comments.size - 3) {
                        // 在后台线程执行网络请求
                        withContext(Dispatchers.IO) {
                            val new = HeyClient.getComments(post.postId, ++page)
                            comments += new
                        }
                    }
                }
            }

        }
        FloatingActionButton(
            onClick = { showSheet = true },
            content = { Icon(Icons.Filled.Create, contentDescription = "发表评论") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp, 96.dp)
        )
        FloatingActionButton(
            onClick = { navController.popBackStack() },
            content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "发表评论") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp)
        )
        if (showSheet) {
            ModalBottomSheet(onDismissRequest = { showSheet = false }) {
                val scoll = rememberScrollState()
                var text by remember { mutableStateOf("") }

                Column(modifier = Modifier.fillMaxWidth().padding(32.dp, 8.dp).verticalScroll(scoll)) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
                        },
                        label = { Text("按下Ctrl + Enter发送") },
                        modifier = Modifier.fillMaxWidth().onPreviewKeyEvent { keyEvent ->
                            if (keyEvent.type == KeyEventType.KeyDown && keyEvent.isCtrlPressed && keyEvent.key == Key.Enter) {
                                println("发送: $text")
                                showSheet = false
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "评论成功", actionLabel = "Action", duration = SnackbarDuration.Short
                                    )
                                    HeyClient.reply(post.postId, text)
                                }
                                true
                            } else {
                                false
                            }
                        },
                    )
                }


            }

        }
    }


}

fun urlToFileName(url: String): String {
    return MD5.GetMD5Code(url) + url.length + url.hashCode() + ".jpg"
}

fun getImagePath(string: String) = File(
    System.getProperty("user.home") + File.separator + "Pictures" + File.separator + "LoadTheImageCache" + File.separator,
    string
).absolutePath

fun getImageDir() = File(
    System.getProperty("user.home") + File.separator + "Pictures" + File.separator + "LoadTheImageCache" + File.separator
)
