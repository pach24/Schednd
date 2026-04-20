package com.schednd.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.schednd.ui.theme.AvatarColors
import com.schednd.ui.theme.GolosFamily

@Composable
fun UserAvatar(
    name: String,
    model: Any?,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp
) {
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val bgColor = remember(name) { avatarColor(name) }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        if (model != null) {
            AsyncImage(
                model = model,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = initial,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = GolosFamily,
                fontSize = fontSize
            )
        }
    }
}

private fun avatarColor(name: String): Color {
    val index = name.fold(0) { acc, c -> acc + c.code } % AvatarColors.size
    return AvatarColors[index]
}
