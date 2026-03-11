package com.sinasamaki.chroma.dial

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Places composable content at regular [spacing]-degree intervals along the dial arc,
 * using [state] to derive geometry.
 *
 * @param state The dial state to derive arc geometry from.
 * @param modifier Modifier for the container.
 * @param spacing Degree spacing between adjacent interval positions.
 * @param padding Inset from the arc radius.
 * @param currentDegree Current degree in the 0..sweepDegrees space for determining
 *   [IntervalData.inActiveRange].
 * @param onIntervalContent Composable content for each interval.
 */
@Composable
public fun DialInterval(
    state: DialState,
    modifier: Modifier = Modifier,
    spacing: Float,
    padding: Dp = 0.dp,
    currentDegree: Float? = null,
    onIntervalContent: @Composable (IntervalData) -> Unit,
) {
    val overshoot = state.overshootDegrees
    val sweepDegrees = state.degreeRange.endInclusive - state.degreeRange.start
    DialIntervalImpl(
        modifier = modifier,
        startDegrees = state.startDegrees + minOf(0f, overshoot),
        sweepDegrees = sweepDegrees + abs(overshoot),
        radius = state.radius,
        spacing = spacing,
        padding = padding,
        currentDegree = currentDegree,
        onIntervalContent = onIntervalContent,
    )
}

/**
 * Places composable content at regular [spacing]-degree intervals along an arc.
 *
 * @param modifier Modifier for the container.
 * @param startDegrees Visual start of the arc in degrees (0° = 12 o'clock). Defaults to 0.
 * @param sweepDegrees Total arc sweep in degrees.
 * @param radius Arc radius in pixels, or null to use the layout width.
 * @param spacing Degree spacing between adjacent interval positions.
 * @param padding Inset from the arc radius.
 * @param currentDegree Current degree in the 0..[sweepDegrees] space for determining
 *   [IntervalData.inActiveRange].
 * @param onIntervalContent Composable content for each interval.
 */
@Composable
public fun DialInterval(
    modifier: Modifier = Modifier,
    startDegrees: Float = 0f,
    sweepDegrees: Float,
    radius: Float? = null,
    spacing: Float,
    padding: Dp = 0.dp,
    currentDegree: Float? = null,
    onIntervalContent: @Composable (IntervalData) -> Unit,
) {
    DialIntervalImpl(
        modifier = modifier,
        startDegrees = startDegrees,
        sweepDegrees = sweepDegrees,
        radius = radius,
        spacing = spacing,
        padding = padding,
        currentDegree = currentDegree,
        onIntervalContent = onIntervalContent,
    )
}

@Composable
private fun DialIntervalImpl(
    modifier: Modifier,
    startDegrees: Float,
    sweepDegrees: Float,
    radius: Float? = null,
    spacing: Float,
    padding: Dp,
    currentDegree: Float?,
    onIntervalContent: @Composable (IntervalData) -> Unit,
) {
    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier) {
        val layoutWidth = constraints.maxWidth.toFloat()
        val radiusPx = radius ?: (layoutWidth / 2f)
        val paddingPx = with(density) { padding.toPx() }

        val items = remember(startDegrees, sweepDegrees, radiusPx, spacing, paddingPx, currentDegree) {
            buildIntervalData(
                startDegrees = startDegrees,
                sweepDegrees = sweepDegrees,
                radius = radiusPx,
                spacing = spacing,
                paddingPx = paddingPx,
                currentDegree = currentDegree,
            )
        }

        for (item in items) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = item.rotationAngle + 90f
                    }
                    .align(Alignment.CenterStart)
                    .width(with(density) { constraints.maxWidth.toDp() })
            ) {
                onIntervalContent(item)
            }
        }
    }
}
