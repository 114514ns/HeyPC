package cn.pprocket.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.pprocket.GlobalState
import cn.pprocket.ui.PlatformU

@Composable
fun SelectableText(text: String, modifier: Modifier = Modifier,style:TextStyle = TextStyle.Default) {
    SelectionContainer {
        var newText = text
        if (GlobalState.config.blockCube) {
            val pattern = "\\[cube_.*?]".toRegex()
            newText = text.replace(pattern,"")
        }
        Text(
            newText,
            modifier = Modifier.fillMaxSize().padding(8.dp),
            style = TextStyle(fontSize = 18.sp).plus(style).plus(TextStyle(fontFamily = PlatformU.getTypography().bodyLarge.fontFamily)),

            lineHeight = 25.sp,
        )
    }
}
