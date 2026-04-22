package com.schednd.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

// --- SHAPES ESTÁNDAR ---
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

/**
 * VerticalSquircleShape: Mantiene la curvatura orgánica de un Squircle
 * pero permite dimensiones asimétricas y desbordes verticales.
 */
class VerticalSquircleShape(
    private val cornerRadius: Dp,
    private val extraHeightTop: Dp = 0.dp,
    private val extraHeightBottom: Dp = 0.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        // El radio se basa en la dimensión menor (normalmente el ancho)
        val r = with(density) { cornerRadius.toPx() }
            .coerceAtMost(minOf(size.width, size.height) / 2f)

        val topEx = with(density) { extraHeightTop.toPx() }
        val botEx = with(density) { extraHeightBottom.toPx() }

        val k = 0.5519f
        val w = size.width
        val h = size.height

        val path = Path().apply {
            // Superior izquierda (con desborde)
            moveTo(r, -topEx)
            lineTo(w - r, -topEx)
            // Esquina superior derecha
            cubicTo(w - r + r * k, -topEx, w, r - r * k - topEx, w, r - topEx)

            // Lateral derecho recto
            lineTo(w, h - r + botEx)
            // Esquina inferior derecha
            cubicTo(w, h - r + r * k + botEx, w - r + r * k, h + botEx, w - r, h + botEx)

            // Línea inferior
            lineTo(r, h + botEx)
            // Esquina inferior izquierda
            cubicTo(r - r * k, h + botEx, 0f, h - r + r * k + botEx, 0f, h - r + botEx)

            // Lateral izquierdo recto
            lineTo(0f, r - topEx)
            // Esquina superior izquierda
            cubicTo(0f, r - r * k - topEx, r - r * k, -topEx, r, -topEx)
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * SquircleShape: Superelipse estándar.
 */
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

// --- INSTANCIAS ---
val GridHeaderShape = VerticalSquircleShape(cornerRadius = 12.dp, extraHeightTop = 15.dp)
val GridFooterShape = VerticalSquircleShape(cornerRadius = 12.dp, extraHeightBottom = 15.dp)
val SquircleCellShape = SquircleShape(12.dp)
val SquircleHeaderShape = SquircleShape(14.dp)
val CalendarCellShape = SquircleShape(14.dp)
val SquircleMiniShape = SquircleShape(4.dp)

// --- PREVIEW ---

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SquircleShapesPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .padding(30.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text("Vertical Squircle Shapes", fontWeight = FontWeight.Bold)

            // Preview del Vertical Squircle puro (estilo cápsula Squircle)
            ShapeShowcase(
                label = "Vertical Squircle Puro (Cápsula)",
                width = 24.dp,
                height = 30.dp,
                shape = VerticalSquircleShape(10.dp)
            )

            // Preview del desborde para el Grid
            ShapeShowcase(
                label = "Grid Header (Desborde superior)",
                width = 48.dp,
                height = 48.dp,
                shape = GridHeaderShape
            )

            HorizontalDivider()

            Text("Shapes Cuadrados", fontWeight = FontWeight.Bold)

            ShapeShowcase("Squircle Estándar", 44.dp, 44.dp, SquircleCellShape)
            ShapeShowcase("Mini Heatmap (4dp)", 16.dp, 16.dp, SquircleMiniShape)
        }
    }
}

@Composable
private fun ShapeShowcase(
    label: String,
    width: Dp,
    height: Dp,
    shape: Shape
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = width, height = height)
                .clip(shape)
                .background(Color.Black)
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}