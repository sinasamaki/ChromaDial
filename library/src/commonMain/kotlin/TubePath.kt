package com.sinasamaki.chroma.dial

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure

/**
 * Creates a filled tube-shaped path that follows a circular arc.
 *
 * @param center Center of the arc circle.
 * @param radius Radius to the midline of the tube.
 * @param startAngleDegrees Starting angle of the arc in degrees.
 * @param sweepAngleDegrees Sweep of the arc in degrees.
 * @param tubeRadius Half the tube thickness (distance from midline to edge).
 * @param cornerRadius Rounding of the end caps. Equal to [tubeRadius] gives full capsule ends;
 *   0 gives flat butt caps.
 * @param steps Number of sample points along the arc for the tube outline.
 */
public fun createTubePath(
    center: Offset,
    radius: Float,
    startAngleDegrees: Float,
    sweepAngleDegrees: Float,
    tubeRadius: Float,
    cornerRadius: Float = tubeRadius,
    steps: Int = 64,
): Path {
    val source = Path().apply {
        addArc(
            oval = Rect(center, radius),
            startAngleDegrees = startAngleDegrees,
            sweepAngleDegrees = sweepAngleDegrees,
        )
    }
    return expandPathCubic(source, tubeRadius, cornerRadius, steps)
}

/**
 * Expands a path into a filled tube by offsetting along the normal at each sample point.
 * End caps are cubic Bézier quarter-circles, with optional flat sections controlled by
 * [cornerRadius]:
 *   - [cornerRadius] == [padding]  → full capsule
 *   - [cornerRadius] == 0          → flat butt caps
 */
private fun expandPathCubic(
    source: Path,
    padding: Float,
    cornerRadius: Float = padding,
    steps: Int = 64,
): Path {
    val cr = cornerRadius.coerceIn(0f, padding)
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

    val endArc1End = endPos + endTan * cr + endPerp * (padding - cr)
    val endArc2Start = endPos + endTan * cr - endPerp * (padding - cr)

    val startArc1End = startPos - startTan * cr - startPerp * (padding - cr)
    val startArc2Start = startPos - startTan * cr + startPerp * (padding - cr)

    return Path().apply {
        moveTo(right.first().x, right.first().y)
        right.drop(1).forEach { lineTo(it.x, it.y) }

        val ec1 = right.last() + endTan * (cr * k)
        val ec2 = endArc1End + endPerp * (cr * k)
        cubicTo(ec1.x, ec1.y, ec2.x, ec2.y, endArc1End.x, endArc1End.y)
        lineTo(endArc2Start.x, endArc2Start.y)
        val ec3 = endArc2Start - endPerp * (cr * k)
        val ec4 = left.last() + endTan * (cr * k)
        cubicTo(ec3.x, ec3.y, ec4.x, ec4.y, left.last().x, left.last().y)

        left.reversed().drop(1).forEach { lineTo(it.x, it.y) }

        val sc1 = left.first() - startTan * (cr * k)
        val sc2 = startArc1End - startPerp * (cr * k)
        cubicTo(sc1.x, sc1.y, sc2.x, sc2.y, startArc1End.x, startArc1End.y)
        lineTo(startArc2Start.x, startArc2Start.y)
        val sc3 = startArc2Start + startPerp * (cr * k)
        val sc4 = right.first() - startTan * (cr * k)
        cubicTo(sc3.x, sc3.y, sc4.x, sc4.y, right.first().x, right.first().y)

        close()
    }
}
