package cn.pprocket.components

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.State
import cn.pprocket.items.Comment
import cn.pprocket.items.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@Composable
fun PostComment(navController: NavHostController,post: Post,onChangeState: (State) -> Unit,onRequireComment : () -> Unit,modifier: Modifier = Modifier) {
    var page by rememberSaveable { (mutableStateOf(1)) }
    val listState = rememberLazyListState()
    var comments by remember { mutableStateOf(mutableStateListOf<Comment>()) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            comments.addAll(HeyClient.getComments(post.postId, 1))
        }
        val state0 = State("title", post.title)
        onChangeState(state0)
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.height(1800.dp).then(modifier),
        flingBehavior = ScrollableDefaults.flingBehavior()
    ) {
        items(comments.size, key = { index -> comments[index].commentId }) { index ->
            val comment = comments[index]
            if (GlobalState.config.isBlockCube) {
                val pattern = "\\[cube_.*?]".toRegex()
                val newText = comment.content.replace(pattern, "")
                if (newText.isNotEmpty()) {
                    Comment(comment, navController, post.postId, onClick = {
                        onRequireComment()

                    })
                }
            } else {
                Comment(comment, navController, post.postId, onClick = {
                    onRequireComment()

                })
            }


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
                    comments.distinctBy { it.commentId }
                }
            }
        }
    }
}
