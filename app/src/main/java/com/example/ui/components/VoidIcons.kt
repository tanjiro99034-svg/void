package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.model.SupportedApp

@Composable
fun VoidLogoMark(
    modifier: Modifier = Modifier.size(36.dp),
    tint: Color = MaterialTheme.colorScheme.primary,
    secondaryTint: Color = MaterialTheme.colorScheme.onBackground,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.085f

        // Broken outer cyber ring
        drawArc(
            color = tint,
            startAngle = -55f,
            sweepAngle = 215f,
            useCenter = false,
            topLeft = Offset(stroke, stroke),
            size = Size(w - stroke * 2, h - stroke * 2),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        drawArc(
            color = secondaryTint.copy(alpha = 0.4f),
            startAngle = 180f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(stroke, stroke),
            size = Size(w - stroke * 2, h - stroke * 2),
            style = Stroke(width = stroke * 0.7f, cap = StrokeCap.Round),
        )

        // Geometric "V" void core
        val vPath = Path().apply {
            moveTo(w * 0.29f, h * 0.34f)
            lineTo(w * 0.50f, h * 0.72f)
            lineTo(w * 0.71f, h * 0.34f)
        }
        drawPath(
            path = vPath,
            color = tint,
            style = Stroke(
                width = stroke * 1.15f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}

@Composable
fun AppTargetEmblem(
    app: SupportedApp,
    isArmed: Boolean,
    modifier: Modifier = Modifier.size(28.dp),
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant
    val tint = if (isArmed) activeColor else inactiveColor

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.085f

        when (app) {
            SupportedApp.INSTAGRAM -> {
                val inset = stroke
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(inset, inset),
                    size = Size(w - inset * 2, h - inset * 2),
                    cornerRadius = CornerRadius(w * 0.26f, w * 0.26f),
                    style = Stroke(width = stroke),
                )
                drawCircle(
                    color = tint,
                    radius = w * 0.21f,
                    center = Offset(w * 0.5f, h * 0.5f),
                    style = Stroke(width = stroke),
                )
                drawCircle(
                    color = tint,
                    radius = w * 0.055f,
                    center = Offset(w * 0.73f, h * 0.27f),
                )
            }

            SupportedApp.YOUTUBE -> {
                val topInset = h * 0.16f
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(stroke, topInset),
                    size = Size(w - stroke * 2, h - topInset * 2),
                    cornerRadius = CornerRadius(w * 0.22f, w * 0.22f),
                    style = Stroke(width = stroke),
                )
                val playPath = Path().apply {
                    moveTo(w * 0.42f, h * 0.36f)
                    lineTo(w * 0.65f, h * 0.50f)
                    lineTo(w * 0.42f, h * 0.64f)
                    close()
                }
                drawPath(
                    path = playPath,
                    color = tint,
                    style = Stroke(
                        width = stroke * 0.85f,
                        join = StrokeJoin.Round,
                    ),
                )
            }

            SupportedApp.FACEBOOK -> {
                val inset = stroke
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(inset, inset),
                    size = Size(w - inset * 2, h - inset * 2),
                    cornerRadius = CornerRadius(w * 0.26f, w * 0.26f),
                    style = Stroke(width = stroke),
                )
                // Clean geometric "f" glyph
                val fPath = Path().apply {
                    moveTo(w * 0.54f, h * 0.88f)
                    lineTo(w * 0.54f, h * 0.40f)
                    quadraticTo(w * 0.54f, h * 0.28f, w * 0.66f, h * 0.28f)
                    lineTo(w * 0.70f, h * 0.28f)
                    moveTo(w * 0.42f, h * 0.52f)
                    lineTo(w * 0.67f, h * 0.52f)
                }
                drawPath(
                    path = fPath,
                    color = tint,
                    style = Stroke(
                        width = stroke * 1.1f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
        }
    }
}
