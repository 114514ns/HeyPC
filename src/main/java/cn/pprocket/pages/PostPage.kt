package cn.pprocket.pages

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share

import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.components.Comment
import cn.pprocket.items.Comment
import com.lt.load_the_image.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@Composable
@Preview
fun PostPage(navController: NavHostController, postId: String) {
    val post = GlobalState.map[postId] ?: return
    var content by rememberSaveable { mutableStateOf(post.description) }
    val state = rememberScrollState()
    var comments by remember { mutableStateOf(emptyList<Comment>()) }
    var gridHeight by remember { mutableStateOf(0.dp) }
    var page = 1
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
            Row(verticalAlignment = Alignment.CenterVertically) {
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
            Text(
                text = content,
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 帖子图片
            post.images.forEach {
                Image(
                    painter = rememberImagePainter(it),
                    contentDescription = "帖子图片",
                    modifier = Modifier.fillMaxSize().padding(8.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit

                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 评论区域
            Text(
                text = "评论",
                style = MaterialTheme.typography.h6
            )
            val gridState = rememberLazyGridState()
            LaunchedEffect(Unit) {
                comments = HeyClient.getComments(postId,1)
            }
            LazyVerticalGrid(columns = GridCells.Fixed(1), state = gridState, modifier = Modifier.height(1800.dp)) {
                items(comments.size) { index ->
                    val comment = comments[index]
                    Comment(comment)
                }
            }
            LaunchedEffect(gridState) {
                snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
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
            onClick = { navController.popBackStack() },
            content = { Icon(Icons.Default.Share, contentDescription = "发表评论") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )


    }

}

