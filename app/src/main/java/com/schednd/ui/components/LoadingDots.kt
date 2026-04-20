package com.schednd.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun LoadingDots(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    dotSize: androidx.compose.ui.unit.Dp = 6.dp,
    spacing: androidx.compose.ui.unit.Dp = 5.dp
) {
    val transition = rememberInfiniteTransition(label = "loadingDots")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1050, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loadingDotsPhase"
    )

    Row(modifier = modifier) {
        for (i in 0 until 3) {
            val distance = ((phase - i + 3f) % 3f)
            val a = when {
                distance < 1f -> 0.35f + (1f - distance) * 0.65f
                else -> 0.35f
            }
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer { alpha = a }
                    .clip(CircleShape)
                    .background(color)
            )
            if (i < 2) Spacer(Modifier.width(spacing))
        }
    }
}
