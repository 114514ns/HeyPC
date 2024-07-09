package cn.pprocket.pages

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
@Preview
fun RootPage() {
    val navController = rememberNavController()
    /*
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomePage()
        }
        composable("home") {

        }
    }

     */
    Box(modifier = Modifier.fillMaxSize()) {
        HomePage(navController)
    }
}

