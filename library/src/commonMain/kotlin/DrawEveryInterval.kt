package com.sinasamaki.chroma.dial

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

/**
 * Data class containing information about each interval position on the dial.
 *
 * @property index The index of this interval (0-based)
 * @property position The pixel position of this interval on the dial path
 * @property rotationAngle The tangent angle in degrees at this position (useful for rotating
 *   content to align with the arc direction)
 * @property intervalDegree The degree value at this interval, in the 0..sweepDegrees space
 * @property inActiveRange Whether this interval is within the active/selected range
 * @property progress The normalized progress (0-1) of this interval within the total range
 */
public data class IntervalData(
    val index: Int,
    val position: Offset,
    val rotationAngle: Float,
    val intervalDegree: Float,
    val inActiveRange: Boolean,
    val progress: Float,
)

/**
 * Shared helper that builds a list of [IntervalData] for positions along an arc.
 *
 * @param startDegrees Visual start of the arc in degrees (0° = 12 o'clock)
 * @param sweepDegrees Total arc sweep in degrees
 * @param center Center of the arc in pixels
 * @param radius Arc radius in pixels
 * @param spacing Degree spacing between adjacent interval points
 * @param currentDegree Current degree in the 0..[sweepDegrees] space for computing
 *   [IntervalData.inActiveRange]; null means all intervals are inactive
 */
internal fun buildIntervalData(
    startDegrees: Float,
    sweepDegrees: Float,
    center: Offset,
    radius: Float,
    spacing: Float,
    currentDegree: Float? = null,
): List<IntervalData> {
    val path = Path().apply {
        addArc(
            oval = Rect(
                center = center,
                radius = radius,
            ),
            startAngleDegrees = startDegrees - 90f,
            sweepAngleDegrees = sweepDegrees,
        )
    }
    val measure = PathMeasure().apply {
        setPath(path, false)
    }

    val totalIntervals = if (spacing > 0f) (sweepDegrees / spacing).toInt() + 1 else 1

    val result = mutableListOf<IntervalData>()
    for (i in 0 until totalIntervals) {
        val intervalDegree = if (spacing > 0f) {
            (i * spacing).coerceAtMost(sweepDegrees)
        } else {
            0f
        }

        val progress = if (sweepDegrees > 0) intervalDegree / sweepDegrees else 0f

        val distance = progress * measure.length
        val pos = measure.getPosition(distance)
        val tangent = measure.getTangent(distance)
        val rotationAngle = atan2(tangent.y, tangent.x) * 180f / PI.toFloat()

        val inActiveRange = if (currentDegree != null) {
            intervalDegree <= currentDegree
        } else {
            false
        }

        result.add(
            IntervalData(
                index = i,
                position = pos,
                rotationAngle = rotationAngle,
                intervalDegree = intervalDegree,
                inActiveRange = inActiveRange,
                progress = progress,
            )
        )
    }
    return result
}

/**
 * Draws content at regular [spacing]-degree intervals along the [dialState]'s arc.
 *
 * @param dialState The dial state to derive arc geometry from.
 * @param spacing Degree spacing between adjacent draw positions. Note: this controls the visual
 *   drawing cadence and is independent of the dial's snap interval ([DialState.interval]).
 * @param center Center of the arc in pixels. Defaults to the [DrawScope]'s center.
 * @param onDraw Called for each interval with its [IntervalData].
 */
public fun DrawScope.drawEveryInterval(
    dialState: DialState,
    spacing: Float,
    center: Offset = this.center,
    onDraw: DrawScope.(IntervalData) -> Unit,
) {
    val overshoot = dialState.overshootDegrees
    val sweepDegrees = dialState.degreeRange.endInclusive - dialState.degreeRange.start
    val items = buildIntervalData(
        startDegrees = dialState.startDegrees + minOf(0f, overshoot),
        sweepDegrees = sweepDegrees + abs(overshoot),
        center = center,
        radius = dialState.radius,
        spacing = spacing,
        currentDegree = dialState.degree + maxOf(0f, overshoot),
    )
    for (item in items) {
        onDraw(item)
    }
}

/**
 * Draws content at regular [spacing]-degree intervals along an arc.
 *
 * @param startDegrees Visual start of the arc in degrees (0° = 12 o'clock). Defaults to 0.
 * @param sweepDegrees Total arc sweep in degrees.
 * @param radius Arc radius in pixels.
 * @param spacing Degree spacing between adjacent draw positions.
 * @param center Center of the arc in pixels. Defaults to the [DrawScope]'s center.
 * @param currentDegree Current degree in the 0..[sweepDegrees] space for determining
 *   [IntervalData.inActiveRange].
 * @param onDraw Called for each interval with its [IntervalData].
 */
public fun DrawScope.drawEveryInterval(
    startDegrees: Float = 0f,
    sweepDegrees: Float,
    radius: Float,
    spacing: Float,
    center: Offset = this.center,
    currentDegree: Float? = null,
    onDraw: DrawScope.(IntervalData) -> Unit,
) {
    val items = buildIntervalData(
        startDegrees = startDegrees,
        sweepDegrees = sweepDegrees,
        center = center,
        radius = radius,
        spacing = spacing,
        currentDegree = currentDegree,
    )
    for (item in items) {
        onDraw(item)
    }
}
