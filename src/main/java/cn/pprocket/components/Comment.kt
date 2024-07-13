package cn.pprocket.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import cn.pprocket.items.Comment
import cn.pprocket.pages.getImagePath
import cn.pprocket.pages.urlToFileName
import com.lt.load_the_image.rememberImagePainter
import kotlinx.coroutines.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Comment(comment: Comment,navController: NavHostController) {
    val subComments = rememberSaveable { mutableStateListOf<Comment>() }
    LaunchedEffect(Unit) {
        comment.subComments.forEach { subComments.add(it) }
        subComments.distinctBy { it.commentId }

    }
    Box(modifier = Modifier.animateContentSize(animationSpec = tween(500))) {
        Column(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(12.dp)


        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberImagePainter(comment.userAvatar),
                    contentDescription = "User Avatar",
                    modifier = Modifier.size(40.dp).clip(shape = MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
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
            }
            Card(modifier = Modifier.padding(top = 12.dp)) {
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
                subComments.forEach {
                    val str = buildAnnotatedString {
                        withStyle(style = SpanStyle(Color(0xff004b96))) {
                            pushStringAnnotation("send", it.userId)
                            append(it.userName)
                            pop()
                        }
                        append(" 回复 ")
                        val post = GlobalState.map[it.postId]
                        if (post!!.userId == comment.userId) {
                            withStyle(style = SpanStyle(Color.Green)) {
                                pushStringAnnotation("to", it.userId)
                                append(it.replyName)
                                pop()
                            }
                        } else {
                            pushStringAnnotation("to", it.userId)
                            append(it.replyName)
                            pop()
                        }
                        append("：")
                        append(it.content)
                    }
                    ClickableText(str, modifier = Modifier.padding(12.dp), onClick = { offset ->
                        str.getStringAnnotations(start = offset, end = offset)
                            .firstOrNull()?.let { annotation ->
                                when (annotation.tag) {
                                    "send" -> {
                                        var user = HeyClient.getUser(annotation.item)
                                        GlobalState.users[annotation.item] = user
                                        navController.navigate("user/${annotation.item}")
                                    }

                                    "to" -> println("Clicked on receiver: ${annotation.item}")
                                    else -> println("Clicked on: ${annotation.item}")
                                }
                                // 在这里处理点击事件，例如打开 URL 或导航到其他屏幕
                            }
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
                                }
                            },
                        color = MaterialTheme.colorScheme.primary,

                        )
                }
            }

        }
    }
}
