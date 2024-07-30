package cn.pprocket.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.pprocket.items.Game
import com.lt.load_the_image.rememberImagePainter

@Composable
fun GameCard(
    onFollowClick: () -> Unit,
    game: Game
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Image(
            painter = rememberImagePainter(game.screenshots[0]),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = game.name, fontSize = 16.sp, color = Color.Black)
            Text(text = game.description, fontSize = 14.sp, color = Color.Gray)
            Row {
                Text(text = game.price.toString(), fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.width(4.dp))
                //Text(text = game., fontSize = 14.sp, color = Color.Red)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}
