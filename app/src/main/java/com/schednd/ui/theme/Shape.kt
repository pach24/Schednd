package com.schednd.ui.theme

import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.draw.clip
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

val SquircleMiniShape = SquircleShape(7.dp)

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun SquircleShapesPreview() {
    androidx.compose.material3.MaterialTheme {
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            // Comparativa de los shapes definidos
            ShapeShowcase("Mini Shape (4dp) - Ideal Heatmap", 16.dp, SquircleMiniShape)
            ShapeShowcase("Cell Shape (12dp)", 44.dp, SquircleCellShape)
            ShapeShowcase("Header Shape (14dp)", 56.dp, SquircleHeaderShape)

            androidx.compose.material3.HorizontalDivider(modifier = androidx.compose.ui.Modifier.padding(vertical = 8.dp))

            // Ejemplo de cómo se vería la leyenda de GitHub con el MiniShape
            androidx.compose.material3.Text(
                "Ejemplo Leyenda GitHub (MiniShape)",
                style = androidx.compose.material3.MaterialTheme.typography.labelMedium
            )
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
            ) {
                repeat(7) { i ->
                    androidx.compose.foundation.layout.Box(
                        modifier = androidx.compose.ui.Modifier
                            .size(16.dp)
                            .clip(SquircleMiniShape) // Corregido: sin prefijo largo
                            .background(androidx.compose.ui.graphics.Color(0xFF216E39).copy(alpha = (i + 1) / 7f))
                    )
                }
            }
        }
    }
}

// Función auxiliar necesaria para que la preview funcione
@androidx.compose.runtime.Composable
private fun ShapeShowcase(
    label: String,
    size: androidx.compose.ui.unit.Dp,
    shape: androidx.compose.ui.graphics.Shape
) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .size(size)
                .clip(shape) // Corregido: sin prefijo largo
                .background(androidx.compose.ui.graphics.Color.Black)
        )
        androidx.compose.material3.Text(
            text = label,
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall
        )
    }
}

