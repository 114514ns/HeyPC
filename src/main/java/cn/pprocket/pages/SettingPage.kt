package cn.pprocket.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import com.lt.load_the_image.rememberImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun SettingsPage(navController: NavHostController) {

    MaterialTheme {
        Card(
            modifier = Modifier.fillMaxSize(),

            ) {
            Box {
                Image(
                    rememberImagePainter(
                        "https://cdn.max-c.com/app/heybox/icon_83.5@3x.png?imageMogr2/thumbnail/!100p/format/jpg"
                    ), "", modifier = Modifier.size(96.dp).align(Alignment.TopEnd).padding(top = 16.dp, end = 16.dp).clip(CircleShape)
                )
                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 96.dp)
                ) {

                    SettingItem(icon = Icons.Filled.Palette,
                        title = "缓存",
                        subtitle = "调整和清除缓存",
                        onClick = {  }
                    )
                    SettingItem(icon = Icons.Filled.Palette,
                        title = "屏蔽",
                        subtitle = "管理黑名单和屏蔽词",
                        onClick = {  }
                    )
                    SettingItem(icon = Icons.Filled.Palette,
                        title = "关于",
                        subtitle = "项目地址",
                        onClick = {  }
                    )

                }
            }
        }
    }

}

@Composable
fun SettingItem(
    icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit

) {
    Card(modifier = Modifier.padding(12.dp).clip(RoundedCornerShape(24.dp)).clickable { }) {
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
            }
        }
    }

}
