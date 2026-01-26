package com.sinasamaki.chroma.dial

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2

/**
 * Data class containing information about each interval position on the dial.
 *
 * @property index The index of this interval (0-based)
 * @property position The pixel position of this interval on the dial path
 * @property degree The tangent angle in degrees at this position (useful for rotation)
 * @property intervalDegree The actual degree value on the dial at this interval
 * @property inActiveRange Whether this interval is within the active/selected range
 * @property progress The normalized progress (0-1) of this interval within the total range
 */
public data class IntervalData(
    val index: Int,
    val position: Offset,
    val degree: Float,
    val intervalDegree: Float,
    val inActiveRange: Boolean,
    val progress: Float,
)

public fun DrawScope.drawEveryInterval(
    dialState: DialState,
    interval: Float,
    padding: Dp = 0.dp,
    onDraw: DrawScope.(IntervalData) -> Unit,
) {
    // Use absolute startDegrees for arc positioning, sweep from degreeRange
    val sweepDegrees = dialState.degreeRange.endInclusive - dialState.degreeRange.start
    val path = Path().apply {
        addArc(
            oval = Rect(
                offset = Offset(
                    padding.toPx(),
                    padding.toPx(),
                ),
                size = Size(
                    width = (dialState.radius * 2) - (padding * 2).toPx(),
                    height = (dialState.radius * 2) - (padding * 2).toPx(),
                )
            ),
            startAngleDegrees = dialState.startDegrees - 90f,
            sweepAngleDegrees = sweepDegrees,
        )
    }
    val measure = PathMeasure().apply {
        setPath(path, false)
    }

    // Calculate interval positions
    val range = dialState.degreeRange.endInclusive - dialState.degreeRange.start
    val totalIntervals = if (interval > 0f) (range / interval).toInt() + 1 else 1

    for (i in 0 until totalIntervals) {
        val intervalDegree = if (interval > 0f) {
            (dialState.degreeRange.start + (i * interval)).coerceAtMost(dialState.degreeRange.endInclusive)
        } else {
            dialState.degreeRange.start
        }

        val progress = if (range > 0) {
            (intervalDegree - dialState.degreeRange.start) / range
        } else {
            0f
        }

        val distance = progress * measure.length
        val pos = measure.getPosition(distance)
        val tangent = measure.getTangent(distance)
        val degrees = atan2(tangent.y, tangent.x) * 180f / PI.toFloat()

        val inActiveRange = intervalDegree <= dialState.degree

        onDraw(
            IntervalData(
                index = i,
                position = pos,
                degree = degrees,
                intervalDegree = intervalDegree,
                inActiveRange = inActiveRange,
                progress = progress,
            )
        )
    }
}

public fun DrawScope.drawEveryInterval(
    degreeRange: ClosedFloatingPointRange<Float>,
    radius: Float,
    interval: Float,
    padding: Dp = 0.dp,
    currentDegree: Float? = null,
    onDraw: DrawScope.(IntervalData) -> Unit,
) {
    val path = Path().apply {
        addArc(
            oval = Rect(
                offset = Offset(
                    padding.toPx(),
                    padding.toPx(),
                ),
                size = Size(
                    width = (radius * 2) - (padding * 2).toPx(),
                    height = (radius * 2) - (padding * 2).toPx(),
                )
            ),
            startAngleDegrees = degreeRange.start - 90f,
            sweepAngleDegrees = degreeRange.endInclusive - degreeRange.start,
        )
    }
    val measure = PathMeasure().apply {
        setPath(path, false)
    }

    // Calculate interval positions
    val range = degreeRange.endInclusive - degreeRange.start
    val totalIntervals = if (interval > 0f) (range / interval).toInt() + 1 else 1

    for (i in 0 until totalIntervals) {
        val intervalDegree = if (interval > 0f) {
            (degreeRange.start + (i * interval)).coerceAtMost(degreeRange.endInclusive)
        } else {
            degreeRange.start
        }

        val progress = if (range > 0) {
            (intervalDegree - degreeRange.start) / range
        } else {
            0f
        }

        val distance = progress * measure.length
        val pos = measure.getPosition(distance)
        val tangent = measure.getTangent(distance)
        val degrees = atan2(tangent.y, tangent.x) * 180f / PI.toFloat()

        val inActiveRange = if (currentDegree != null) {
            intervalDegree <= currentDegree
        } else {
            false
        }

        onDraw(
            IntervalData(
                index = i,
                position = pos,
                degree = degrees,
                intervalDegree = intervalDegree,
                inActiveRange = inActiveRange,
                progress = progress,
            )
        )
    }
}

@Composable
public fun DialInterval(
    modifier: Modifier = Modifier,
    degreeRange: ClosedFloatingPointRange<Float>,
    radius: Float? = null,
    interval: Float,
    padding: Dp = 0.dp,
    currentDegree: Float? = null,
    onIntervalContent: @Composable (IntervalData) -> Unit,
) {
    val density = LocalDensity.current

    // Calculate interval positions
    // Remember these to avoid recalculation when only currentDegree changes
    val range = remember(degreeRange) {
        degreeRange.endInclusive - degreeRange.start
    }
    val totalIntervals = remember(interval, range) {
        if (interval > 0f) (range / interval).toInt() + 1 else 1
    }

    BoxWithConstraints(
        modifier = modifier
    ) {
        val layoutWidth = constraints.maxWidth.toFloat()
        val layoutHeight = constraints.maxHeight.toFloat()

        // Use provided radius or default to half the width
        val radiusPx = radius ?: (layoutWidth / 2f)
        val paddingPx = with(density) { padding.toPx() }

        val path = remember(radiusPx, paddingPx, degreeRange) {
            Path().apply {
                addArc(
                    oval = Rect(
                        offset = Offset(
                            paddingPx,
                            paddingPx,
                        ),
                        size = Size(
                            width = (radiusPx * 2) - (paddingPx * 2),
                            height = (radiusPx * 2) - (paddingPx * 2),
                        )
                    ),
                    startAngleDegrees = degreeRange.start - 90f,
                    sweepAngleDegrees = degreeRange.endInclusive - degreeRange.start,
                )
            }
        }


        val measure = remember(path) {
            PathMeasure().apply {
                setPath(path, false)
            }
        }

        for (i in 0 until totalIntervals) {
            val intervalDegree = if (interval > 0f) {
                (degreeRange.start + (i * interval)).coerceAtMost(degreeRange.endInclusive)
            } else {
                degreeRange.start
            }

            val progress = if (range > 0) {
                (intervalDegree - degreeRange.start) / range
            } else {
                0f
            }

            val distance = progress * measure.length
            val pos = measure.getPosition(distance)
            val tangent = measure.getTangent(distance)
            val degrees = atan2(tangent.y, tangent.x) * 180f / PI.toFloat()

            val inActiveRange = if (currentDegree != null) {
                intervalDegree <= currentDegree
            } else {
                false
            }

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = degrees + 90f
                    }
                    .align(Alignment.CenterStart)
                    .width(with(density) { (constraints.maxWidth / 1f).toDp() })
            ) {
                onIntervalContent(
                    IntervalData(
                        index = i,
                        position = pos,
                        degree = degrees,
                        intervalDegree = intervalDegree,
                        inActiveRange = inActiveRange,
                        progress = progress,
                    )
                )
            }
        }
    }
}
