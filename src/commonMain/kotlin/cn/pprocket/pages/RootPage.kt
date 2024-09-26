package cn.pprocket.pages

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import cn.pprocket.Platform
import cn.pprocket.State
import cn.pprocket.items.Topic
import cn.pprocket.ui.PlatformU
import kotlin.random.Random

@Composable
fun RootPage(onChangeState: (State) -> Unit) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    Box(modifier = Modifier.fillMaxSize()) {
        var showBottomBar by remember { mutableStateOf(true) }
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(navController)
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) {

            LaunchedEffect(navController) {
                navController.currentBackStackEntryFlow.collect { backStackEntry ->
                    showBottomBar =
                        backStackEntry.destination.route!! == "feeds" || backStackEntry.destination.route == "settings"
                }
            }
            NavHost(navController = navController, startDestination = "home") {
                navigation(
                    startDestination = "feeds",
                    route = "home",
                    enterTransition = {


                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )

                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )

                    },
                    popEnterTransition = {

                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )

                    },
                    popExitTransition = {

                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )

                    }
                ) {
                    composable("feeds/{topic}") { stack ->
                        val str = stack.arguments?.getString("topic")

                        if (str!!.contains("search")) {
                            FeedsPage(
                                navController,
                                snackbarHostState,
                                keyWord = str.replace("feeds/search", "")
                            )
                        } else {
                            val topic = Topic(str!!.split("|")[0], str.split("|")[1].toInt(), "")
                            FeedsPage(navController, snackbarHostState, topicArg = topic)
                        }

                    }
                    composable("feeds") {
                        FeedsPage(navController, snackbarHostState)
                    }
                    composable("settings") {
                        SettingsPage(navController, snackbarHostState, onChangeState)
                    }
                    composable(
                        "user/{userId}",
                    ) { stack ->
                        UserPage(navController, stack.arguments?.getString("userId") ?: "")
                    }
                    composable(
                        "fans/{userId}"
                    ) { stack ->
                        FansPage(stack.arguments?.getString("userId")!!, navController)


                    }
                }
                composable(
                    "post/{postId}"
                ) { stack ->
                    PostPage(
                        navController,
                        stack.arguments?.getString("postId") ?: "",
                        snackbarHostState,
                        onChangeState
                    )
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

    var selectedItem by rememberSaveable { mutableStateOf(0) }
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = null) },
                label = { Text(item.capitalize()) },
                selected = index == selectedItem,
                onClick = {
                    if (index == 1) {
                        val links = listOf(
                            "https://www.bilibili.com/video/BV1GJ411x7h7/",
                            "https://www.bilibili.com/video/BV1J4411v7g6/",
                            //"https://www.bilibili.com/video/BV1U1421974u/",
                            "https://www.bilibili.com/video/BV1Fo4y1K7F2/"
                        )
                        PlatformU.openImage((links[Random.nextInt(0, links.size)]))
                    } else {
                        selectedItem = index
                        navController.navigate(item)
                    }
                },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}