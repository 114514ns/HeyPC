package cn.pprocket.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.sharp.Favorite
import androidx.compose.material.icons.sharp.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.Logger
import cn.pprocket.items.Comment
import cn.pprocket.pages.getImagePath
import cn.pprocket.pages.urlToFileName
import com.lt.load_the_image.rememberImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Comment(comment: Comment, navController: NavHostController, postId: String, onClick: () -> Unit = {}) {
    val subComments = remember { mutableStateListOf<Comment>() }
    val logger = Logger("cn.pprocket.components.Comment")
    LaunchedEffect(Unit) {
        comment.subComments.forEach { subComments.add(it) }
        subComments.distinctBy { it.commentId }

    }
    Box(modifier = Modifier.onClick {

    }) {
        Column(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(12.dp)


        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberImagePainter(comment.userAvatar),
                    contentDescription = "User Avatar",
                    modifier = Modifier.size(40.dp).clip(shape = MaterialTheme.shapes.medium).clickable {
                        GlobalState.users[comment.userId] = HeyClient.getUser(comment.userId)
                        navController.navigate("user/${comment.userId}")
                    },
                    contentScale = ContentScale.Crop,

                    )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = comment.userName, fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = comment.createdAt,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Outlined.Favorite, "")
                Text(comment.likes.toString(), modifier = Modifier.padding(start = 6.dp))

            }
            Card(modifier = Modifier.padding(top = 12.dp).onClick {
                GlobalState.subCommentId = comment.commentId
                logger.info("Set subCommentId: ${GlobalState.subCommentId}")
                onClick()
            }) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SelectableText(text = comment.content)
                    Spacer(modifier = Modifier.height(8.dp))
                    comment.images.forEach {
                        Image(
                            painter = rememberImagePainter(it),
                            contentDescription = "Image",
                            modifier = Modifier.size(120.dp).padding(4.dp).onClick {
                                Runtime.getRuntime().exec("cmd /c " + getImagePath(urlToFileName(it)))
                            }.clip(RoundedCornerShape(8.dp))
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Column(modifier = Modifier.animateContentSize(animationSpec = tween(500))) {
                val post = GlobalState.map[postId]
                subComments.forEach {
                    val str = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                if (it.userName == comment.userName || it.userName == post!!.userName)
                                    Color(0xffC232E6)
                                else Color(0xff004b96)

                            )
                        ) {
                            pushStringAnnotation("send", it.userId)
                            append(it.userName)
                            pop()
                        }
                        append(" 回复 ")

                        if (post!!.userName == comment.userName) {
                            withStyle(style = SpanStyle(Color.Green)) {
                                pushStringAnnotation("to", it.replyId)
                                append(it.replyName)
                                pop()

                            }
                        } else {
                            pushStringAnnotation("to", it.replyId)
                            append(it.replyName)
                            pop()
                        }
                        append("：")
                        append(it.content)
                    }
                    ClickableText(
                        str,
                        modifier = Modifier.padding(12.dp).animateContentSize(animationSpec = tween(500)),
                        onClick = { offset ->
                            str.getStringAnnotations(start = offset, end = offset)
                                .firstOrNull()?.let { annotation ->
                                    when (annotation.tag) {
                                        "send" -> {
                                            var user = HeyClient.getUser(annotation.item)
                                            GlobalState.users[annotation.item] = user
                                            navController.navigate("user/${annotation.item}")
                                        }

                                        "to" -> {
                                            var user = HeyClient.getUser(annotation.item)
                                            GlobalState.users[annotation.item] = user
                                            navController.navigate("user/${annotation.item}")
                                        }

                                        else -> println("Clicked on: ${annotation.item}")
                                    }
                                    // 在这里处理点击事件，例如打开 URL 或导航到其他屏幕
                                }
                            GlobalState.subCommentId = it.commentId
                            logger.info("Set subCommentId: ${GlobalState.subCommentId}")
                            onClick()
                        })
                }
                if (comment.isHasMore) {

                    Text(
                        "加载更多",
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp).align(Alignment.CenterHorizontally)
                            .clickable {
                                CoroutineScope(Dispatchers.IO).launch() {
                                    subComments.clear()
                                    subComments.addAll(comment.fillSubComments())
                                    subComments.distinctBy { it.commentId }
                                }
                            },
                        color = MaterialTheme.colorScheme.primary,

                        )
                }
            }

        }

    }
}
