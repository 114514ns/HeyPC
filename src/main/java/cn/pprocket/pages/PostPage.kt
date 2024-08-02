package cn.pprocket.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.navigation.NavHostController
import cn.pprocket.*
import cn.pprocket.State
import cn.pprocket.components.*
import cn.pprocket.items.Comment
import com.lt.load_the_image.rememberImagePainter
import com.lt.load_the_image.util.MD5
import kotlinx.coroutines.launch
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class,
    ExperimentalAnimationApi::class
)
@Composable
@Preview
fun PostPage(
    navController: NavHostController,
    postId: String,
    snackbarHostState: SnackbarHostState,
    onChangeState: (State) -> Unit,
    windowState: WindowState
) {
    var post = GlobalState.map[postId] ?: return
    val scope = rememberCoroutineScope()
    val logger = Logger("cn.pprocket.pages.PostPage")
    var showSheet by remember { mutableStateOf(false) }
    var showSticker by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        logger.info(" ${post.postId}  ${post.title}")
    }
    var isFullScreen by remember { mutableStateOf(windowState.placement != WindowPlacement.Floating) }
    LaunchedEffect(windowState.placement) {
        if (windowState.placement == WindowPlacement.Fullscreen || windowState.placement == WindowPlacement.Maximized) {
            isFullScreen = true
        } else {
            isFullScreen = false
        }
    }


    var tabIndex by remember { mutableStateOf(0) }
    Spacer(modifier = Modifier.height(32.dp))
    Column(modifier = Modifier.padding(16.dp)) {
        if (GlobalState.config.isShowTab) {
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
        }
        Box {

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isFullScreen) {
                    PostContent(
                        navController,
                        post,
                        onChangeState,
                        scope,
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .animateContentSize()
                    )
                    PostComment(
                        navController,
                        post,
                        onChangeState,
                        {
                            showSheet = true
                        },
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .animateContentSize()
                    )
                } else {
                    Crossfade(targetState = tabIndex) { selectedTab ->
                        when (selectedTab) {
                            0 -> {
                                PostContent(navController, post, onChangeState, scope, Modifier.fillMaxSize())
                            }
                            1 -> {
                                PostComment(navController, post, onChangeState, {
                                    showSheet = true
                                }, Modifier.fillMaxSize())
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
                    val state0 = State("title", "你干嘛 ~ 哎呦")
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
                Icon(Icons.Default.Mood, "", modifier = Modifier.padding(bottom = 10.dp).clickable {
                    showSticker = true
                })
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
                                        HeyClient.reply(
                                            post.postId,
                                            textFieldValue.text,
                                            images = list
                                        )

                                        GlobalState.subCommentId = "-1"

                                    }.start()
                                    /*
                                    comments.add(
                                        listState.firstVisibleItemIndex + if (comments.isEmpty()) 0 else 1,
                                        sentComment
                                    )

                                     */
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
                                        GlobalState.subCommentId = "-1"
                                    }.start()
                                }

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
            if (showSticker) {
                StickerListDialog(onDismissRequest = { showSticker = false }, images)
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

fun getStickerDir() = File(
    System.getProperty("user.home") + File.separator + "Pictures" + File.separator + "HeyImage" + File.separator
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
