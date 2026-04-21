package com.schednd.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

val CardShape = RoundedCornerShape(16.dp)
val ButtonShape = RoundedCornerShape(12.dp)
val TextFieldShape = RoundedCornerShape(12.dp)
val FullRoundShape = RoundedCornerShape(50)

class SquircleShape(private val cornerRadius: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val r = with(density) { cornerRadius.toPx() }
            .coerceAtMost(minOf(size.width, size.height) / 2f)
        val k = 0.5519f
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(r, 0f)
            lineTo(w - r, 0f)
            cubicTo(w - r + r * k, 0f, w, r - r * k, w, r)
            lineTo(w, h - r)
            cubicTo(w, h - r + r * k, w - r + r * k, h, w - r, h)
            lineTo(r, h)
            cubicTo(r - r * k, h, 0f, h - r + r * k, 0f, h - r)
            lineTo(0f, r)
            cubicTo(0f, r - r * k, r - r * k, 0f, r, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}

val SquircleCellShape = SquircleShape(12.dp)
val SquircleHeaderShape = SquircleShape(14.dp)
val CalendarCellShape = SquircleShape(14.dp)

