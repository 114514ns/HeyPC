package cn.pprocket.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import client
import cn.pprocket.GlobalState
import cn.pprocket.Logger
import cn.pprocket.State
import cn.pprocket.items.Element
import cn.pprocket.items.Post
import coil3.compose.AsyncImage
import io.github.kdroidfilter.composemediaplayer.VideoPlayerState
import io.github.kdroidfilter.composemediaplayer.VideoPlayerSurface
import io.github.kdroidfilter.composemediaplayer.rememberVideoPlayerState
import kotlinx.coroutines.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostContent(
    navController: NavController,
    post: Post,
    onChangeState: (State) -> Unit,
    scope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val state = rememberScrollState()
    var content by rememberSaveable { mutableStateOf(post.description) }
    var html = remember { mutableStateListOf<Element>() }
    val logger = Logger("cn.pprocket.components.PostContent")
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            if (!post.isHTML) {
                var str = ""
                try {
                    str = client.fillContent(post)
                } catch (e: NullPointerException) {
                    logger.error("post.fillContent() error: $e")
                    logger.error("postId ${post.postId}  title ${post.title}")
                    throw e
                }
                content = str
            } else {
                html.addAll(client.renderHTML(post.postId))
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize().verticalScroll(state).padding(top = 16.dp).then(modifier)) {
        // 作者信息
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = {
            scope.launch {
                GlobalState.users[post.userId] = client.getUser(post.userId)
                navController.navigate("user/${post.userId}")
            }

        })) {
            AsyncImage(
                post.userAvatar, null, modifier = Modifier.size(40.dp).clip(CircleShape),
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
                            ContextImage(scope, it.tagValue,nav = navController)
                        }

                        "title" -> {
                            SelectableText(it.tagValue, style = MaterialTheme.typography.headlineMedium)
                        }

                        "ref" -> {
                            MarkdownQuote(it.tagValue)
                        }

                        "gameCard" -> {

                            //GameCard({},HeyClient.getGame(it.tagValue))
                        }
                        "video" -> {


                            val videoState = rememberVideoPlayerState()

                            var audioState = rememberVideoPlayerState()
                            var audioLink = ""
                            var videoLink = it.tagValue.split("|")[0]

                            /*
                            VideoPlayerSurface(
                                playerState = audioState,
                                modifier = Modifier.width(0.dp).height(0.dp)
                            )

                             */
                            VideoPlayerSurface(
                                playerState = videoState,
                                modifier = Modifier
                            )

                            if (it.tagValue.split("|").size == 2) {
                                audioLink = it.tagValue.split("|")[1]
                                audioState.openUri(audioLink)
                            }
                            logger.info("video ${videoLink}")

                            videoState.openUri(videoLink)




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

            FlowRow {
                post.images.forEach { imageUrl ->
                    ContextImage(scope, imageUrl, Modifier.animateContentSize())
                }
            }
        }

        FlowRow {
            post.tags.forEach {
                AssistChip(
                    onClick = {
                        val state = State("title", it.name)
                        onChangeState(state)
                        navController.navigate("feeds/${it.name}|${it.id}")
                    },
                    label = { Text(it.name) },
                    modifier = Modifier.padding(10.dp),
                    leadingIcon = {
                        AsyncImage(it.icon, null, Modifier.size(AssistChipDefaults.IconSize))
                    }
                )
            }
        }
    }
}
