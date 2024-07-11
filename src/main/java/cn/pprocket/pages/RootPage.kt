package cn.pprocket.pages

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

import androidx.navigation.compose.rememberNavController
import cn.pprocket.items.Post

@Composable
fun RootPage() {
    val navController = rememberNavController()
    Box(modifier = Modifier.fillMaxSize()) {
        var showBottomBar by remember { mutableStateOf(true) }
        Scaffold(
            bottomBar = { if (showBottomBar) {
                BottomNavigationBar(navController)
            }}
        ) {

            LaunchedEffect(navController) {
                navController.currentBackStackEntryFlow.collect { backStackEntry ->
                    showBottomBar = backStackEntry.destination.route == "feeds"
                    println(backStackEntry.destination.route)
                }
            }
            NavHost(navController = navController, startDestination = "home") {
                navigation(
                    startDestination = "feeds",
                    route = "home"
                ) {
                    composable("feeds") {
                        FeedsPage(navController)
                    }
                }
                composable(
                    "post/{postId}",
                    //arguments = listOf(navArgument("postId") { type = NavType.StringType })
                    enterTransition = {
                        return@composable fadeIn(tween(1000))
                    }, exitTransition = {
                        return@composable fadeOut(tween(700))
                    }, popEnterTransition = {
                        return@composable slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                        )
                    }
                ) { stack ->
                    PostPage(navController, stack.arguments?.getString("postId") ?: "")
                }
            }
        }
    }
}
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    //var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("home", "profile", "settings")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Person,
        Icons.Default.Settings
    )

    var selectedItem by remember { mutableStateOf(0) }
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = null) },
                label = { Text(item.capitalize()) },
                selected = index == selectedItem,
                onClick = {
                    selectedItem = index

                },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
