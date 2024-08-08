package cn.pprocket.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import cn.pprocket.Logger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchArea(navController: NavHostController) {
    var text by rememberSaveable { mutableStateOf("") }
    val result = mutableStateListOf<String>()
    val logger = Logger("cn.pprocket.components.SearchArea")
    LaunchedEffect(text) {

        if (text.isNotEmpty() && !text.contains("'")) {
            logger.info(text)
            result.clear()
            result.addAll(HeyClient.searchSuggestion(text))
        }
    }
    SearchBar(
        text,
        onQueryChange = { t -> text = t },
        active = true,
        onSearch = {},
        onActiveChange = {},
        modifier = Modifier.padding(12.dp).fillMaxHeight(1f).onKeyEvent { event ->
            if (event.key == Key.Enter) {
                navController.navigate("feeds/search${text}")
                true
            } else {
                false
            }
        }
    ) {
        Column {
            result.forEach {
                Text(text = it, modifier = Modifier.padding(8.dp).fillMaxWidth().clickable {
                    navController.navigate("feeds/search${it}")
                })
            }
        }
    }
}
