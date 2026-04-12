package com.sinasamaki.chroma.dial

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * Creates a filled tube-shaped path that follows a circular arc.
 *
 * @param center Center of the arc circle.
 * @param radius Radius to the midline of the tube.
 * @param startAngleDegrees Starting angle of the arc in degrees, where 0° = 12 o'clock (top).
 * @param sweepAngleDegrees Sweep of the arc in degrees.
 * @param tubeRadius Half the tube thickness (distance from midline to edge).
 * @param cornerRadius Rounding of the end caps. Defaults to full capsule ends.
 *   Corners map as: topStart = start-outer, topEnd = end-outer,
 *   bottomEnd = end-inner, bottomStart = start-inner.
 * @param density Density used to resolve [cornerRadius] corner sizes.
 * @param steps Number of sample points along the arc for the tube outline.
 */
public fun createTubePath(
    center: Offset,
    radius: Float,
    startAngleDegrees: Float,
    sweepAngleDegrees: Float,
    tubeRadius: Float,
    cornerRadius: RoundedCornerShape = RoundedCornerShape(percent = 50),
    density: Density,
    steps: Int = 64,
): Path {
    val source = Path().apply {
        addArc(
            oval = Rect(center, radius),
            startAngleDegrees = startAngleDegrees - 90f,
            sweepAngleDegrees = sweepAngleDegrees,
        )
    }
    val size = Size(tubeRadius * 2, tubeRadius * 2)
    val startOuterCr = cornerRadius.topStart.toPx(size, density).coerceIn(0f, tubeRadius)
    val endOuterCr = cornerRadius.topEnd.toPx(size, density).coerceIn(0f, tubeRadius)
    val endInnerCr = cornerRadius.bottomEnd.toPx(size, density).coerceIn(0f, tubeRadius)
    val startInnerCr = cornerRadius.bottomStart.toPx(size, density).coerceIn(0f, tubeRadius)
    return expandPathCubic(source, tubeRadius, endOuterCr, endInnerCr, startInnerCr, startOuterCr, steps)
}

/**
 * Creates and draws a tube-shaped path in this [DrawScope].
 *
 * All [createTubePath] parameters are accepted. [center] defaults to [DrawScope.center] and
 * [radius] defaults to half the minimum dimension minus [tubeRadius] (outer edge flush with bounds).
 * [density] is provided by the [DrawScope] itself.
 *
 * The remaining parameters mirror [DrawScope.drawPath].
 */
public fun DrawScope.drawTubePath(
    startAngleDegrees: Float,
    sweepAngleDegrees: Float,
    tubeRadius: Float,
    center: Offset = this.center,
    radius: Float = size.minDimension / 2f - tubeRadius,
    cornerRadius: RoundedCornerShape = RoundedCornerShape(percent = 50),
    steps: Int = 64,
    color: Color,
    alpha: Float = 1f,
    style: DrawStyle = Fill,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DrawScope.DefaultBlendMode,
) {
    drawPath(
        path = createTubePath(center, radius, startAngleDegrees, sweepAngleDegrees, tubeRadius, cornerRadius, this, steps),
        color = color,
        alpha = alpha,
        style = style,
        colorFilter = colorFilter,
        blendMode = blendMode,
    )
}

/**
 * Creates and draws a tube-shaped path in this [DrawScope] using a [Brush].
 *
 * @see drawTubePath
 */
public fun DrawScope.drawTubePath(
    startAngleDegrees: Float,
    sweepAngleDegrees: Float,
    tubeRadius: Float,
    center: Offset = this.center,
    radius: Float = size.minDimension / 2f - tubeRadius,
    cornerRadius: RoundedCornerShape = RoundedCornerShape(percent = 50),
    steps: Int = 64,
    brush: Brush,
    alpha: Float = 1f,
    style: DrawStyle = Fill,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DrawScope.DefaultBlendMode,
) {
    drawPath(
        path = createTubePath(center, radius, startAngleDegrees, sweepAngleDegrees, tubeRadius, cornerRadius, this, steps),
        brush = brush,
        alpha = alpha,
        style = style,
        colorFilter = colorFilter,
        blendMode = blendMode,
    )
}

/**
 * A [Shape] that produces a tube-shaped outline following a circular arc.
 *
 * [center] and [radius] may be left at their defaults ([Float.NaN]) to have them derived from
 * the layout size in [createOutline]: center becomes the size center and radius becomes
 * `minDimension / 2 - tubeRadius` (outer edge flush with bounds). [density] is supplied by the
 * framework when [createOutline] is called.
 */
public class TubeShape(
    private val startAngleDegrees: Float,
    private val sweepAngleDegrees: Float,
    private val tubeRadius: Float,
    private val center: Offset = Offset(Float.NaN, Float.NaN),
    private val radius: Float = Float.NaN,
    private val cornerRadius: RoundedCornerShape = RoundedCornerShape(percent = 50),
    private val steps: Int = 64,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val resolvedCenter = if (center.x.isNaN() || center.y.isNaN()) {
            Offset(size.width / 2f, size.height / 2f)
        } else {
            center
        }
        val resolvedRadius = if (radius.isNaN()) {
            size.minDimension / 2f - tubeRadius
        } else {
            radius
        }
        return Outline.Generic(
            createTubePath(
                center = resolvedCenter,
                radius = resolvedRadius,
                startAngleDegrees = startAngleDegrees,
                sweepAngleDegrees = sweepAngleDegrees,
                tubeRadius = tubeRadius,
                cornerRadius = cornerRadius,
                density = density,
                steps = steps,
            )
        )
    }
}

/**
 * Expands a path into a filled tube by offsetting along the normal at each sample point.
 * End caps are cubic Bézier quarter-circles, with optional flat sections controlled by
 * the four corner radii:
 *   - radius == [padding]  → full capsule at that corner
 *   - radius == 0          → flat butt cap at that corner
 */
private fun expandPathCubic(
    source: Path,
    padding: Float,
    endOuterCr: Float,
    endInnerCr: Float,
    startInnerCr: Float,
    startOuterCr: Float,
    steps: Int = 64,
): Path {
    val k = 0.5523f

    val measure = PathMeasure()
    measure.setPath(source, false)
    val length = measure.length
    if (length == 0f) return Path()

    val right = mutableListOf<Offset>()
    val left = mutableListOf<Offset>()

    for (i in 0..steps) {
        val distance = (i / steps.toFloat()) * length
        val pos = measure.getPosition(distance)
        val tan = measure.getTangent(distance)
        val perp = Offset(tan.y, -tan.x)
        right.add(pos + perp * padding)
        left.add(pos - perp * padding)
    }

    val startPos = measure.getPosition(0f)
    val startTan = measure.getTangent(0f)
    val startPerp = Offset(startTan.y, -startTan.x)

    val endPos = measure.getPosition(length)
    val endTan = measure.getTangent(length)
    val endPerp = Offset(endTan.y, -endTan.x)

    val endArc1End   = endPos + endTan * endOuterCr  + endPerp * (padding - endOuterCr)
    val endArc2Start = endPos + endTan * endInnerCr  - endPerp * (padding - endInnerCr)

    val startArc1End   = startPos - startTan * startInnerCr - startPerp * (padding - startInnerCr)
    val startArc2Start = startPos - startTan * startOuterCr + startPerp * (padding - startOuterCr)

    return Path().apply {
        moveTo(right.first().x, right.first().y)
        right.drop(1).forEach { lineTo(it.x, it.y) }

        val ec1 = right.last() + endTan * (endOuterCr * k)
        val ec2 = endArc1End + endPerp * (endOuterCr * k)
        cubicTo(ec1.x, ec1.y, ec2.x, ec2.y, endArc1End.x, endArc1End.y)
        lineTo(endArc2Start.x, endArc2Start.y)
        val ec3 = endArc2Start - endPerp * (endInnerCr * k)
        val ec4 = left.last() + endTan * (endInnerCr * k)
        cubicTo(ec3.x, ec3.y, ec4.x, ec4.y, left.last().x, left.last().y)

        left.reversed().drop(1).forEach { lineTo(it.x, it.y) }

        val sc1 = left.first() - startTan * (startInnerCr * k)
        val sc2 = startArc1End - startPerp * (startInnerCr * k)
        cubicTo(sc1.x, sc1.y, sc2.x, sc2.y, startArc1End.x, startArc1End.y)
        lineTo(startArc2Start.x, startArc2Start.y)
        val sc3 = startArc2Start + startPerp * (startOuterCr * k)
        val sc4 = right.first() - startTan * (startOuterCr * k)
        cubicTo(sc3.x, sc3.y, sc4.x, sc4.y, right.first().x, right.first().y)

        close()
    }
}
