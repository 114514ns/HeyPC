package cn.pprocket.pages

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDuration.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.components.Comment
import cn.pprocket.components.SelectableText
import cn.pprocket.items.Comment
import com.lt.load_the_image.rememberImagePainter
import com.lt.load_the_image.util.MD5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
@Preview
fun PostPage(
    navController: NavHostController,
    postId: String,
    snackbarHostState: androidx.compose.material3.SnackbarHostState
) {
    val post = GlobalState.map[postId] ?: return
    var content by rememberSaveable { mutableStateOf(post.description) }
    val state = rememberScrollState()
    var comments by remember { mutableStateOf(emptyList<Comment>()) }
    var gridHeight by remember { mutableStateOf(0.dp) }
    var page = 1
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    LaunchedEffect(0) {
        var str = ""
        withContext(Dispatchers.IO) {
            str = post.fillContent()
        }
        content = str
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
                    Text(text = post.userName, style = MaterialTheme.typography.subtitle1)
                    Text(text = post.createAt, style = MaterialTheme.typography.caption)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 帖子内容
            androidx.compose.material3.Card {
                SelectableText(
                    text = content,
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
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

            // 评论区域
            Text(
                text = "评论",
                style = MaterialTheme.typography.h6
            )
            val listState = rememberLazyListState()
            LaunchedEffect(Unit) {
                comments = HeyClient.getComments(postId, 1)
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.height(1800.dp),
                flingBehavior = ScrollableDefaults.flingBehavior()
            ) {
                items(comments.size) { index ->
                    val comment = comments[index]
                    Comment(comment,navController)
                }
            }
            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .collectLatest { lastIndex ->
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
                                        message = "评论成功",
                                        actionLabel = "Action",
                                        duration = SnackbarDuration.Short
                                    )
                                    HeyClient.reply(post.postId,text)
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
    System.getProperty("user.home")
            + File.separator
            + "Pictures"
            + File.separator
            + "LoadTheImageCache"
            + File.separator, string
).absolutePath
