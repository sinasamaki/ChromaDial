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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2

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
