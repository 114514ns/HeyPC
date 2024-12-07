package cn.pprocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cn.pprocket.pages.RootPage
import com.materialkolor.DynamicMaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var color by remember { mutableStateOf(Color(99, 160, 2)) }
        setContent {
            DynamicMaterialTheme(color, useDarkTheme = false) {
                Box(modifier = Modifier.fillMaxSize()) {
                    RootPage(onChangeState = { state ->
                        if (state.type == "title") {
                            title = state.value as String
                        }
                        if (state.type == "color") {
                            color = state.value as Color
                        }
                    })
                }

            }
        }
    }
}