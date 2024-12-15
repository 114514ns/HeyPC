package cn.pprocket.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import cn.pprocket.GlobalState
import cn.pprocket.State
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch

@Composable
fun SettingDialog(
    onDismissRequest: () -> Unit,
    onChangeState: (State) -> Unit
) {
    val scope = rememberCoroutineScope()
    var showCube by remember { mutableStateOf(GlobalState.config.blockCube) }
    var showTab by remember { mutableStateOf(GlobalState.config.showTab) }
    var showExpend by remember { mutableStateOf(false) }
    var originImage by remember { mutableStateOf(GlobalState.config.originImage) }
    val dropdownMenuOffset = remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }
    val map = mutableMapOf(
        "Green" to Color(99, 160, 2),
        "Blue" to Color(2, 136, 209),
        "Purple" to Color(123, 31, 162),
        "Yellow" to Color(251, 192, 45),
        "Sky" to Color(2,136,209),
        "Black" to Color(38,50,56)
    )
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.fillMaxWidth().height(375.dp).padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                SettingsSwitch(
                    state = showCube,
                    title = { Text(text = "屏蔽表情") },
                    //subtitle = { Text(text = "Setting subtitle") },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    enabled = true,
                    onCheckedChange = { newState: Boolean ->
                        GlobalState.config.blockCube = newState;showCube = newState
                    },
                )
                SettingsSwitch(
                    state = showTab,
                    title = { Text(text = "显示帖子页面的Tab栏") },
                    //subtitle = { Text(text = "Setting subtitle") },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    enabled = true,
                    onCheckedChange = { newState: Boolean ->
                        GlobalState.config.showTab = newState;showTab = newState
                    },
                )
                SettingsSwitch(
                    state = originImage,
                    title = { Text(text = "帖子图片始终显示原图") },
                    //subtitle = { Text(text = "Setting subtitle") },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    enabled = true,
                    onCheckedChange = { newState: Boolean ->
                        GlobalState.config.originImage = newState;originImage = newState
                    },
                )
                SettingsMenuLink(
                    { Text("自定义主题色") },
                    onClick = { showExpend = true },
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        // 获取 TextField 的位置和大小
                        val position = coordinates.positionInRoot()
                        val size = coordinates.size.toSize()

                        // 计算 DropdownMenu 的偏移量
                        val offset = DpOffset(position.x.dp, position.y.dp)
                        // 更新 DropdownMenu 的偏移量
                        dropdownMenuOffset.value = offset
                    })
                Box {
                    if (showExpend) {
                        DropdownMenu(true, { showExpend = false }) {
                            for (item in map) {
                                DropdownMenuItem(text = { Text(item.key) }, onClick = {
                                    val state = State("color", item.value)
                                    onChangeState(state)
                                    showExpend = false
                                })
                            }
                        }
                    }
                }

            }

        }
    }
}



