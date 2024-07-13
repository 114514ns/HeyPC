package cn.pprocket.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun SettingsPage(navController: NavHostController) {
    MaterialTheme {
        Column {
            Text("屏蔽设置")
        }
    }

}
