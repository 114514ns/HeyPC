package cn.pprocket.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.components.*
import com.lt.load_the_image.rememberImagePainter

@OptIn(ExperimentalFoundationApi::class)
@Composable

fun SettingsPage(navController: NavHostController, snackbarHostState: SnackbarHostState) {

    var showAbout by remember { mutableStateOf(false) }
    var showAccount by remember { mutableStateOf(false) }
    var showDebug by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    MaterialTheme {
        Card(
            modifier = Modifier.fillMaxSize(),

            ) {
            Box {
                Image(
                    rememberImagePainter(
                        GlobalState.config.user.avatar
                            ?: "https://cdn.max-c.com/app/heybox/icon_83.5@3x.png?imageMogr2/thumbnail/!100p/format/jpg"
                    ),
                    "",
                    modifier = Modifier.size(96.dp).align(Alignment.TopEnd).padding(top = 16.dp, end = 16.dp)
                        .clip(CircleShape).onClick { showAccount = true }
                )
                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 96.dp)
                ) {

                    SettingItem(icon = Icons.Filled.Palette,
                        title = "屏蔽",
                        subtitle = "管理黑名单和屏蔽词",
                        onClick = {

                        }
                    )
                    SettingItem(icon = Icons.Filled.Palette,
                        title = "关于",
                        subtitle = "项目地址",
                        onClick = {
                            showAbout = true
                        }
                    )
                    SettingItem(icon = Icons.Filled.Palette,
                        title = "调试",
                        subtitle = "开发者选项",
                        onClick = {
                            showDebug = true
                        }
                    )
                    SettingItem(icon = Icons.Filled.Palette,
                        title = "设置",
                        subtitle = "一般设置",
                        onClick = {
                            showSettings = true
                        }
                    )

                }
            }
            if (showAbout) {
                AboutDialog(
                    onDismissRequest = { showAbout = false },
                    snackbarHostState = snackbarHostState
                )
            }
            if (showAccount) {
                AccountDialog(
                    onDismissRequest = { showAccount = false },
                )
            }
            if (showDebug) {
                DebugDialog(
                    onDismissRequest = { showDebug = false }, snackbarHostState,navController
                )
            }
            if (showSettings) {
                SettingDialog(
                    onDismissRequest = { showSettings = false },
                )
            }
        }
    }

}

@Composable
fun SettingItem(
    icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit

) {
    Card(modifier = Modifier.padding(12.dp).clip(RoundedCornerShape(24.dp)).clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.height(128.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // 确保 Row 内的内容垂直居中
        ) {
            Icon(
                imageVector = icon, // 使用你的 Icon 资源
                contentDescription = null, modifier = Modifier.padding(start = 16.dp) // 给图标添加左侧内边距
            )
            Spacer(modifier = Modifier.width(16.dp)) // 添加图标和文本之间的间距
            Column(
                modifier = Modifier.weight(1f) // 分配剩余空间给 Column
            ) {
                Text(title)
                Spacer(modifier = Modifier.height(8.dp)) // 更改为高度间距，以分隔标题和副标题
                Text(subtitle)
                MaterialTheme.colorScheme
            }
        }
    }

}

@Composable

fun TextItem(title: String, subtitle: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable { onClick() }) {
        Text(title, modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(subtitle, modifier = Modifier.padding(8.dp), style = TextStyle(color = Color(0xff8c9196)))
    }
}

