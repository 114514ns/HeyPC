package cn.pprocket.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController

@Composable
fun RootPage() {
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomePage(navController)
            }
            composable(
                "post/{postId}",
                //arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { stack ->
                PostPage(navController, stack.arguments?.getString("postId") ?: "")
            }
        }
    }
}
