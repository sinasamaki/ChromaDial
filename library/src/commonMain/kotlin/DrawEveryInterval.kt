package com.sinasamaki.chroma.dial

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

/**
 * Data class containing information about each interval position on the dial.
 *
 * @property index The index of this interval (0-based)
 * @property position The absolute pixel position of this interval on the dial path. With
 *   [IntervalOrientation.PositionAndRotate] or [IntervalOrientation.PositionOnly] the drawing origin
 *   is already moved here, so draw relative to [Offset.Zero] and ignore this value; it is only needed
 *   with [IntervalOrientation.None].
 * @property rotationAngle The tangent angle in degrees at this position. With
 *   [IntervalOrientation.PositionAndRotate] the canvas is already rotated by this amount (so `+y`
 *   points radially inward); with [IntervalOrientation.None] use it to rotate content yourself to
 *   align with the arc direction.
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
 * Controls how [drawEveryInterval] and [DialInterval] transform the canvas / layout at each interval
 * position before emitting content.
 */
public enum class IntervalOrientation {
    /**
     * The origin is moved to the interval's position and rotated by its tangent angle, so `+y`
     * points radially inward. Draw/place content relative to the origin ([Offset.Zero]); content
     * follows the curve of the arc. This is the default.
     */
    PositionAndRotate,

    /**
     * The origin is moved to the interval's position, but the axes stay screen-aligned (no
     * rotation). Draw/place content relative to the origin ([Offset.Zero]); content stays upright
     * regardless of where it sits on the arc — ideal for readable labels.
     */
    PositionOnly,

    /**
     * No transform is applied. Position content yourself using [IntervalData.position] and, if
     * needed, [IntervalData.rotationAngle] — for example when computing your own polar geometry from
     * [IntervalData.intervalDegree].
     */
    None,
}

/**
 * Shared helper that builds a list of [IntervalData] for positions along an arc.
 *
 * @param startDegrees Visual start of the arc in degrees (0° = 12 o'clock)
 * @param sweepDegrees Total arc sweep in degrees
 * @param center Center of the arc in pixels
 * @param radius Arc radius in pixels
 * @param interval Degree spacing between adjacent interval points
 * @param currentDegree Current degree in the 0..[sweepDegrees] space for computing
 *   [IntervalData.inActiveRange]; null means all intervals are inactive
 */
internal fun buildIntervalData(
    startDegrees: Float,
    sweepDegrees: Float,
    center: Offset,
    radius: Float,
    interval: Float,
    currentDegree: Float? = null,
): List<IntervalData> {
    val absSweep = abs(sweepDegrees)
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

    val totalIntervals = if (interval > 0f) (absSweep / interval).toInt() + 1 else 1

    val result = mutableListOf<IntervalData>()
    for (i in 0 until totalIntervals) {
        val intervalDegree = if (interval > 0f) {
            (i * interval).coerceAtMost(absSweep)
        } else {
            0f
        }

        val progress = if (absSweep > 0) intervalDegree / absSweep else 0f

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
 * Draws content at regular [interval]-degree intervals along an arc.
 *
 * @param interval Degree spacing between adjacent draw positions.
 * @param startDegrees Visual start of the arc in degrees (0° = 12 o'clock). Defaults to 0.
 * @param sweepDegrees Total arc sweep in degrees. Defaults to a full 360° circle.
 * @param center Center of the arc in pixels. Defaults to the [DrawScope]'s center.
 * @param radius Arc radius in pixels. Defaults to the largest circle that fits, i.e. the smaller of
 *   [center]'s x/y.
 * @param currentDegree Current degree in the 0..[sweepDegrees] space for determining
 *   [IntervalData.inActiveRange].
 * @param orientation How the canvas is transformed at each interval before [onDraw] runs. Defaults
 *   to [IntervalOrientation.PositionAndRotate]. See [IntervalOrientation] for the alternatives
 *   ([IntervalOrientation.PositionOnly] keeps content upright; [IntervalOrientation.None] leaves the
 *   [DrawScope] untransformed).
 * @param onDraw Called for each interval with its [IntervalData].
 */
public fun DrawScope.drawEveryInterval(
    interval: Float,
    startDegrees: Float = 0f,
    sweepDegrees: Float = 360f,
    center: Offset = this.center,
    radius: Float = minOf(center.x, center.y),
    currentDegree: Float? = null,
    orientation: IntervalOrientation = IntervalOrientation.PositionAndRotate,
    onDraw: DrawScope.(IntervalData) -> Unit,
) {
    val items = buildIntervalData(
        startDegrees = startDegrees,
        sweepDegrees = sweepDegrees,
        center = center,
        radius = radius,
        interval = interval,
        currentDegree = currentDegree,
    )
    for (item in items) {
        when (orientation) {
            IntervalOrientation.PositionAndRotate ->
                translate(left = item.position.x, top = item.position.y) {
                    rotate(degrees = item.rotationAngle, pivot = Offset.Zero) {
                        onDraw(item)
                    }
                }

            IntervalOrientation.PositionOnly ->
                translate(left = item.position.x, top = item.position.y) {
                    onDraw(item)
                }

            IntervalOrientation.None ->
                onDraw(item)
        }
    }
}
