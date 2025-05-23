package cn.pprocket.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.HeyClient.fillSubComments
import cn.pprocket.Logger
import cn.pprocket.items.Comment
import cn.pprocket.ui.PlatformU
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun Comment(comment: Comment, navController: NavHostController, postId: String, onClick: () -> Unit = {}) {
    val subComments = remember { mutableStateListOf<Comment>() }
    val logger = Logger("cn.pprocket.components.Comment")
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        comment.subComments.forEach { subComments.add(it) }
        subComments.distinctBy { it.commentId }

    }
    Box {
        Column(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(12.dp)


        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    comment.userAvatar, null,
                    modifier = Modifier.size(40.dp).clip(shape = MaterialTheme.shapes.medium).clickable {
                        scope.launch {
                            GlobalState.users[comment.userId] = HeyClient.getUser(comment.userId)
                            navController.navigate("user/${comment.userId}")
                        }
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
            Card(modifier = Modifier.padding(top = 12.dp).clickable {
                GlobalState.subCommentId = comment.commentId
                logger.info("Set subCommentId: ${GlobalState.subCommentId}")
                onClick()
            }) {

                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SelectableText(text = comment.content)
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow {
                        comment.images.forEach {
                            ContextImage(
                                scope,
                                it,
                                modifier = Modifier.size(120.dp).padding(4.dp).clip(RoundedCornerShape(8.dp)),navController)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Column(modifier = Modifier.animateContentSize(animationSpec = tween(500))) {
                val itemsState = remember { mutableStateListOf(*comment.subComments.toTypedArray()) }
                LaunchedEffect(comment.subComments) {
                    itemsState.clear()
                    itemsState.addAll(comment.subComments)
                }
                val post = GlobalState.map[postId]
                itemsState.forEach {
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
                        style = TextStyle(fontFamily = PlatformU.getTypography().bodyLarge.fontFamily),
                        modifier = Modifier.padding(12.dp).animateContentSize(animationSpec = tween(500)),
                        onClick = { offset ->
                            str.getStringAnnotations(start = offset, end = offset)
                                .firstOrNull()?.let { annotation ->
                                    when (annotation.tag) {
                                        "send" -> {
                                            scope.launch {
                                                var user = HeyClient.getUser(annotation.item)
                                                GlobalState.users[annotation.item] = user
                                                navController.navigate("user/${annotation.item}")
                                            }
                                        }

                                        "to" -> {
                                            scope.launch {
                                                var user = HeyClient.getUser(annotation.item)
                                                GlobalState.users[annotation.item] = user
                                                navController.navigate("user/${annotation.item}")
                                            }
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
                if (comment.hasMore) {

                    Text(
                        "加载更多",
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp).align(Alignment.CenterHorizontally)
                            .clickable {
                                CoroutineScope(Dispatchers.Default).launch() {
                                    /*
                                    itemsState.clear()
                                    itemsState.addAll(comment.fillSubComments())
                                    comment.fillSubComments()!!.forEach {
                                        itemsState.add(it)
                                    }
                                    itemsState.distinctBy { it.commentId }


                                     */
                                    comment.fillSubComments()
                                    val copy = itemsState.toList()
                                    comment.subComments.forEach { subComment ->
                                        var c = true
                                        copy.forEach {
                                            if (subComment.commentId == it.commentId) {
                                                c = false
                                            }

                                        }
                                        if (c) {
                                            itemsState.add(subComment)
                                        }
                                    }


                                }
                            },
                        color = MaterialTheme.colorScheme.primary,

                        )
                }
            }

        }

    }
}
