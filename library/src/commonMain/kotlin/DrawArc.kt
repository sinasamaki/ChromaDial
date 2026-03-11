package com.sinasamaki.chroma.dial

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws a stroke arc on the dial, with 0° at 12 o'clock.
 *
 * The [radius] defines the outer edge of the stroke — the arc is automatically
 * inset by half the [strokeWidth] so the stroke stays fully within the given radius.
 *
 * @param color Arc color.
 * @param startAngle Start angle in degrees, where 0° is 12 o'clock.
 * @param sweepAngle Sweep angle in degrees, clockwise.
 * @param radius Outer radius of the arc stroke in pixels.
 * @param strokeWidth Stroke width in dp. Defaults to 8.dp.
 * @param strokeCap Stroke cap style. Defaults to [StrokeCap.Round].
 */
public fun DrawScope.drawArc(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    radius: Float,
    strokeWidth: Dp = 8.dp,
    strokeCap: StrokeCap = StrokeCap.Round,
) {
    val strokePx = strokeWidth.toPx()
    val arcRadius = radius - strokePx / 2f
    drawArc(
        color = color,
        startAngle = startAngle - 90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
        size = Size(arcRadius * 2f, arcRadius * 2f),
        style = Stroke(width = strokePx, cap = strokeCap),
    )
}

/**
 * Draws a stroke arc on the dial using a [Brush], with 0° at 12 o'clock.
 *
 * The [radius] defines the outer edge of the stroke — the arc is automatically
 * inset by half the [strokeWidth] so the stroke stays fully within the given radius.
 *
 * @param brush Arc brush (gradient or shader).
 * @param startAngle Start angle in degrees, where 0° is 12 o'clock.
 * @param sweepAngle Sweep angle in degrees, clockwise.
 * @param radius Outer radius of the arc stroke in pixels.
 * @param strokeWidth Stroke width in dp. Defaults to 8.dp.
 * @param strokeCap Stroke cap style. Defaults to [StrokeCap.Round].
 */
public fun DrawScope.drawArc(
    brush: Brush,
    startAngle: Float,
    sweepAngle: Float,
    radius: Float,
    strokeWidth: Dp = 8.dp,
    strokeCap: StrokeCap = StrokeCap.Round,
) {
    val strokePx = strokeWidth.toPx()
    val arcRadius = radius - strokePx / 2f
    drawArc(
        brush = brush,
        startAngle = startAngle - 90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
        size = Size(arcRadius * 2f, arcRadius * 2f),
        style = Stroke(width = strokePx, cap = strokeCap),
    )
}
