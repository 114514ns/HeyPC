package cn.pprocket.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import cn.pprocket.GlobalState
import cn.pprocket.HeyClient
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch


@Composable
fun DebugDialog(
    onDismissRequest: () -> Unit, snackbarHostState: SnackbarHostState, navHostController: NavHostController
) {
    val scope = rememberCoroutineScope()
    var postText by remember { mutableStateOf("") }
    var userText by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Key for the image inline content
        Card(
            modifier = Modifier.fillMaxWidth().height(375.dp).padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                Column {
                    OutlinedTextField(
                        value = postText,
                        singleLine = true,
                        onValueChange = { postText = it },
                        label = { Text("通过id打开帖子") },
                        modifier = Modifier.onKeyEvent { event ->
                            if (event.key == Key.Enter) {
                                scope.launch {
                                    val post = HeyClient.getPost(postText)
                                    GlobalState.map[post.postId] = post
                                    navHostController.navigate("post/${post.postId}")
                                }
                                true
                            } else {
                                false
                            }
                        }
                    )
                    OutlinedTextField(
                        value = userText,
                        singleLine = true,
                        onValueChange = { userText = it },
                        label = { Text("通过id打开用户主页") },
                        modifier = Modifier.onKeyEvent { event ->
                            if (event.key == Key.Enter) {
                                scope.launch {
                                    onDismissRequest()
                                    GlobalState.users[userText] = HeyClient.getUser(userText)
                                    navHostController.navigate("user/${userText}")
                                }
                                true
                            } else {
                                false
                            }
                        },
                    )
                    val dividerId = "inlineDividerId"
                    val text = buildAnnotatedString {
                        append(AnnotatedString("LinkedIn ", spanStyle = SpanStyle(Color.Blue)))

                        appendInlineContent(dividerId, "[divider]")

                        append(AnnotatedString(" Twitter ", spanStyle = SpanStyle(Color.Blue)))

                        appendInlineContent(dividerId, "[divider]")

                        append(AnnotatedString(" Portfolio", spanStyle = SpanStyle(Color.Blue)))
                    }
                    val inlineDividerContent = mapOf(
                        Pair(
                            // This tells the [CoreText] to replace the placeholder string "[divider]" by
                            // the composable given in the [InlineTextContent] object.
                            dividerId,
                            InlineTextContent(
                                // Placeholder tells text layout the expected size and vertical alignment of
                                // children composable.
                                Placeholder(
                                    width = 80.sp,
                                    height = 80.sp,
                                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                                )
                            ) {
                                KamelImage(asyncPainterResource("https://imgheybox1.max-c.com/bbs/2024/07/27/11708872c01f81a63af82191eacb245a/thumb.jpeg"), contentDescription = "")
                            }
                        )
                    )

                    BasicText(text = text, inlineContent = inlineDividerContent, style = TextStyle(fontSize = 17.sp))
                }


            }
        }
    }
}
