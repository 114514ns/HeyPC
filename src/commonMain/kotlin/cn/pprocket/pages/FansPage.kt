package cn.pprocket.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.HeyClient.getFollowers
import cn.pprocket.HeyClient.getFollowings
import cn.pprocket.items.User
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun FansPage(userId: String, navHostController: NavHostController) {
    val user = GlobalState.users[userId]
    user!!.userId = userId
    val scope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            var tabIndex by rememberSaveable { mutableStateOf(if (GlobalState.isFlowing) 0 else 1) }
            val tabs = listOf("关注", "粉丝")
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
            when (tabIndex) {
                0 -> {
                    UserList({ page -> user.getFollowers(page) }, navHostController, scope, user)

                }

                1 -> {
                    UserList({ page -> user.getFollowings(page) }, navHostController, scope, user)
                }
            }
        }
        FloatingActionButton(
            onClick = {
                navHostController.popBackStack();
            },
            content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "发表评论") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp)
        )
    }

}

@Composable
fun UserList(  fetch:suspend  (Int) -> List<User>, navHostController: NavHostController, scope: CoroutineScope, user: User) {
    var page by rememberSaveable { mutableStateOf(1) }
    val list = rememberSaveable { mutableStateListOf<User>() }
    val state = rememberLazyListState()
    LaunchedEffect(Unit) {
        list.clear()
        list.addAll(fetch(page))
    }
    LaunchedEffect(state) {

        snapshotFlow { state.layoutInfo.visibleItemsInfo.lastOrNull()?.index }.collectLatest { lastIndex ->
            if (lastIndex != null && lastIndex >= list.size - 5) {
                // 在后台线程执行网络请求

                if (list.size > if (GlobalState.isFlowing) user.followings else user.followers) {
                    val new = fetch(++page)
                    list += new
                }

            }
        }
    }
    LazyColumn(state = state) {
        items(list.size) { index ->
            Row(modifier = Modifier.fillMaxWidth().height(70.dp).clickable {
                scope.launch {
                    GlobalState.users[list[index].userId] = HeyClient.getUser(list[index].userId)
                    navHostController.navigate("user/${list[index].userId}")
                }

            }) {
                AsyncImage(list[index].avatar,"",Modifier.clip(RoundedCornerShape(12.dp)).padding(20.dp))
                Text(text = list[index].userName, modifier = Modifier.padding(top = 10.dp))
            }
        }
    }
}
