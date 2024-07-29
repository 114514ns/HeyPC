package cn.pprocket.pages

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.Logger
import cn.pprocket.State
import cn.pprocket.components.Comment
import cn.pprocket.components.SelectableText
import cn.pprocket.items.Comment
import cn.pprocket.items.Tag
import cn.pprocket.toBufferedImage
import com.lt.load_the_image.rememberImagePainter
import com.lt.load_the_image.util.MD5
import com.skydoves.landscapist.coil3.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.log
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
@Preview
fun PostPage(
    navController: NavHostController,
    postId: String,
    snackbarHostState: SnackbarHostState,
    onChangeState: (State) -> Unit
) {
    var post = GlobalState.map[postId] ?: return
    var content by rememberSaveable { mutableStateOf(post.description) }
    val state = rememberScrollState()
    var comments by remember { mutableStateOf(mutableStateListOf<Comment>()) }

    val scope = rememberCoroutineScope()
    val logger = Logger("cn.pprocket.pages.PostPage")
    var showSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    var html = remember { mutableStateListOf<Tag>() }
    LaunchedEffect(Unit) {
        logger.info(" ${post.postId}  ${post.title}")

        withContext(Dispatchers.IO) {
            if (!post.isHTML) {
                var str = ""
                try {
                    str = post.fillContent()
                } catch (e: NullPointerException) {
                    logger.error("post.fillContent() error: $e")
                    logger.error("postId $postId  title ${post.title}")
                    throw e
                }
                content = str
            } else {
                html.addAll(post.renderHTML())
            }

        }
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            comments.addAll(HeyClient.getComments(postId, 1))
        }
        val state0 = State()
        state0.type = "title"
        state0.value = post.title
        onChangeState(state0)
    }

    var tabIndex by remember { mutableStateOf(0) }
    Spacer(modifier = Modifier.height(32.dp))
    Column(modifier = Modifier.padding(16.dp)) {
        /*
        val tabs = listOf("动态", "评论")
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) }, selected = tabIndex == index, onClick = {
                    tabIndex = index
                }, icon = {
                    when (index) {
                        0 -> Icon(imageVector = Icons.Default.Home, contentDescription = null)
                        1 -> Icon(imageVector = Icons.Default.Info, contentDescription = null)
                    }
                })
            }


        }

         */
        Box {
            when (tabIndex) {
                0 -> {
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(state).padding(top = 16.dp)) {
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

                        Card {

                            if (post.isHTML) {
                                html.forEach {
                                    when (it.tagType) {
                                        "text" -> {
                                            SelectableText(it.tagValue)
                                        }

                                        "image" -> {
                                            Image(
                                                painter = rememberImagePainter(it.tagValue),
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxWidth().padding(8.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .onClick {
                                                        Runtime.getRuntime()
                                                            .exec("cmd /c " + getImagePath(urlToFileName(it.tagValue)))
                                                    },
                                                contentScale = ContentScale.Fit,
                                            )
                                        }

                                        "title" -> {
                                            SelectableText(it.tagValue, style = MaterialTheme.typography.headlineMedium)
                                        }
                                    }
                                }
                            } else {

                                SelectableText(
                                    text = content
                                )


                            }

                        }

                        // 帖子图片
                        if (!post.isHTML) {
                            post.images.forEach {
                                CoilImage(
                                    imageModel = { it },
                                    modifier = Modifier.padding(8.dp).clip(RoundedCornerShape(12.dp))
                                )
                                /*
                                Image(
                                    painter = rememberImagePainter(it),
                                    contentDescription = "帖子图片",
                                    modifier = Modifier.padding(8.dp).clip(RoundedCornerShape(12.dp)).onClick {
                                        Runtime.getRuntime().exec("cmd /c " + getImagePath(urlToFileName(it)))
                                    },
                                    contentScale = ContentScale.Fit,
                                )

                                 */
                            }
                        }



                        FlowRow {
                            post.tags.forEach {
                                AssistChip(onClick = { }, label = { Text(it) }, modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }


                }

                1 -> {
                    var page by rememberSaveable { (mutableStateOf(1)) }
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.height(1800.dp),
                        flingBehavior = ScrollableDefaults.flingBehavior()
                    ) {
                        items(comments.size, key = { index -> comments[index].commentId }) { index ->
                            val comment = comments[index]
                            Comment(comment, navController, postId, onClick = {
                                showSheet = true

                            })
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
            }
            FloatingActionButton(
                onClick = {
                    tabIndex = if (tabIndex == 0) {
                        1
                    } else {
                        0
                    }
                },
                content = {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ) {
                                Text("${post.comments}")
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Autorenew, contentDescription = "发表评论")
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp, 160.dp)
            )
            FloatingActionButton(
                onClick = {
                    if (GlobalState.config.isLogin) {
                        showSheet = true
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "请先登录", actionLabel = "取消", duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                content = { Icon(Icons.Filled.Create, contentDescription = "发表评论") },
                modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp, 96.dp)
            )
            FloatingActionButton(
                onClick = {
                    val state0 = State()
                    state0.type = "title"
                    state0.value = "你干嘛 ~ 哎呦"
                    onChangeState(state0)
                    navController.popBackStack();
                },
                content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "发表评论") },
                modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp)
            )
        }
    }








    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }) {
            val scoll = rememberScrollState()
            val images = remember { mutableStateListOf<Image>() }
            Column(modifier = Modifier.fillMaxWidth().padding(32.dp, 8.dp).verticalScroll(scoll)) {
                var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = {
                        textFieldValue = it
                    },
                    label = { Text("按下Ctrl + Enter发送") },
                    modifier = Modifier.fillMaxWidth().onPreviewKeyEvent { keyEvent ->
                        if (keyEvent.type == KeyEventType.KeyDown && keyEvent.isCtrlPressed && keyEvent.key == Key.Enter) {
                            logger.info("发送: ${textFieldValue.text}")
                            showSheet = false
                            scope.launch {
                                val list = mutableListOf<String>()
                                val sentComment = Comment()
                                sentComment.content = textFieldValue.text
                                sentComment.userName = GlobalState.config.user.userName
                                sentComment.userAvatar = GlobalState.config.user.avatar
                                sentComment.userId = GlobalState.config.user.userId
                                sentComment.likes = 0;
                                sentComment.createdAt = "刚刚"
                                sentComment.images = emptyList()
                                sentComment.subComments = emptyList()
                                sentComment.commentId = Random(114514).nextInt().toString()
                                if (GlobalState.subCommentId == "-1") {
                                    Thread {
                                        val list = mutableListOf<String>()
                                        images.forEach {
                                            val url = HeyClient.uploadImage(it)
                                            list.add(url)
                                        }
                                        HeyClient.reply(post.postId, textFieldValue.text, images = list)
                                    }.start()
                                    comments.add(
                                        listState.firstVisibleItemIndex + if (comments.isEmpty()) 0 else 1,
                                        sentComment
                                    )
                                    logger.info("scoll.value: $scoll.value")
                                } else {
                                    Thread {

                                        images.forEach {
                                            val url = HeyClient.uploadImage(it)
                                            list.add(url)
                                        }
                                        HeyClient.reply(
                                            post.postId,
                                            textFieldValue.text,
                                            GlobalState.subCommentId,
                                            list
                                        )
                                    }.start()
                                }
                                GlobalState.subCommentId = "-1"
                                snackbarHostState.showSnackbar(
                                    message = "评论成功", actionLabel = "取消", duration = SnackbarDuration.Short
                                )
                            }
                            true
                        } else {
                            if (keyEvent.isCtrlPressed && keyEvent.key == Key.V && keyEvent.type == KeyEventType.KeyDown) {
                                val v0 = getImageFromClipboard()
                                if (getClipboardContents() != null) {
                                    val newText = textFieldValue.text + getClipboardContents()
                                    textFieldValue = TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length)
                                    )
                                } else if (v0 != null) {
                                    images.add(v0)
                                    logger.info("Image")
                                }
                                true
                            } else {
                                false
                            }
                        }
                    },
                )
                Row {
                    images.forEach {
                        val byteArray: ByteArray? = try {
                            val baos = ByteArrayOutputStream()
                            ImageIO.write(toBufferedImage(it), "jpg", baos) // "jpg" 可以根据实际图像类型调整
                            baos.toByteArray()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                        Image(
                            painter = rememberImagePainter(byteArray!!),
                            contentDescription = "图片",
                            modifier = Modifier.padding(8.dp).clip(RoundedCornerShape(8.dp)).onClick {
                                images.remove(it)
                            }.width(50.dp).height(50.dp),
                            contentScale = ContentScale.Fit,
                        )
                    }
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

private fun getClipboardContents(): String? {
    return try {
        Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor) as? String
    } catch (e: Exception) {

        null
    }
}

fun getImageFromClipboard(): Image? {
    val clipboard: Clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
    val cc = clipboard.getContents(null)
    if (cc == null) return null
    else if (cc.isDataFlavorSupported(DataFlavor.imageFlavor)) return cc.getTransferData(DataFlavor.imageFlavor) as Image
    return null
}
