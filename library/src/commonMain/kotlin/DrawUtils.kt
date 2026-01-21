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

public fun DrawScope.drawEveryStep(
    dialState: DialState,
    steps: Int,
    padding: Dp = 0.dp,
    onDraw: DrawScope.(Offset, Float, Boolean) -> Unit,
) {
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
            startAngleDegrees = dialState.degreeRange.start - 90f,
            sweepAngleDegrees = dialState.degreeRange.endInclusive - dialState.degreeRange.start,
        )
    }
    val measure = PathMeasure().apply {
        setPath(path, false)
    }

    // Calculate step positions matching Dial.kt's calculateSnappedValue
    val range = dialState.degreeRange.endInclusive - dialState.degreeRange.start
    val stepSize = if (steps > 0) range / (steps + 1) else range
    val totalSteps = if (steps > 0) steps + 2 else 1

    for (i in 0 until totalSteps) {
        val stepDegree = if (steps > 0) {
            dialState.degreeRange.start + (i * stepSize)
        } else {
            dialState.degreeRange.start
        }

        val progress = if (range > 0) {
            (stepDegree - dialState.degreeRange.start) / range
        } else {
            0f
        }

        val distance = progress * measure.length
        val pos = measure.getPosition(distance)
        val tangent = measure.getTangent(distance)
        val degrees = atan2(tangent.y, tangent.x) * 180f / PI.toFloat()

        val inActiveRange = stepDegree <= dialState.degree

        onDraw(pos, degrees, inActiveRange)
    }
}

public fun DrawScope.drawEveryStep(
    degreeRange: ClosedFloatingPointRange<Float>,
    radius: Float,
    steps: Int,
    padding: Dp = 0.dp,
    currentDegree: Float? = null,
    onDraw: DrawScope.(Offset, Float, Boolean) -> Unit,
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

    // Calculate step positions matching Dial.kt's calculateSnappedValue
    val range = degreeRange.endInclusive - degreeRange.start
    val stepSize = if (steps > 0) range / (steps + 1) else range
    val totalSteps = if (steps > 0) steps + 2 else 1

    for (i in 0 until totalSteps) {
        val stepDegree = if (steps > 0) {
            degreeRange.start + (i * stepSize)
        } else {
            degreeRange.start
        }

        val progress = if (range > 0) {
            (stepDegree - degreeRange.start) / range
        } else {
            0f
        }

        val distance = progress * measure.length
        val pos = measure.getPosition(distance)
        val tangent = measure.getTangent(distance)
        val degrees = atan2(tangent.y, tangent.x) * 180f / PI.toFloat()

        val inActiveRange = if (currentDegree != null) {
            stepDegree <= currentDegree
        } else {
            false
        }

        onDraw(pos, degrees, inActiveRange)
    }
}

@Composable
public fun StepBasedContent(
    modifier: Modifier = Modifier,
    degreeRange: ClosedFloatingPointRange<Float>,
    radius: Float? = null,
    steps: Int,
    padding: Dp = 0.dp,
    currentDegree: Float? = null,
    onStepContent: @Composable (Int, Offset, Float, Boolean) -> Unit,
) {
    val density = LocalDensity.current

    // Calculate step positions matching Dial.kt's calculateSnappedValue
    // Remember these to avoid recalculation when only currentDegree changes
    val range = remember(degreeRange) {
        degreeRange.endInclusive - degreeRange.start
    }
    val stepSize = remember(steps, range) {
        if (steps > 0) range / (steps + 1) else range
    }
    val totalSteps = remember(steps) {
        if (steps > 0) steps + 2 else 1
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

        for (i in 0 until totalSteps) {
            val stepDegree = if (steps > 0) {
                degreeRange.start + (i * stepSize)
            } else {
                degreeRange.start
            }

            val progress = if (range > 0) {
                (stepDegree - degreeRange.start) / range
            } else {
                0f
            }

            val distance = progress * measure.length
            val pos = measure.getPosition(distance)
            val tangent = measure.getTangent(distance)
            val degrees = atan2(tangent.y, tangent.x) * 180f / PI.toFloat()

            val inActiveRange = if (currentDegree != null) {
                stepDegree <= currentDegree
            } else {
                false
            }

            // Convert pixel position to bias (-1 to 1)
            // bias = (position / (size / 2)) - 1
            val biasX = (pos.x / (layoutWidth / 2f)) - 1f
            val biasY = (pos.y / (layoutHeight / 2f)) - 1f

//            Box(
//                modifier = Modifier
//                    .align(BiasAlignment(biasX, biasY))
//            ) {
//                onStepContent(pos, degrees, inActiveRange)
//            }

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = degrees + 90f
                    }
                    .align(Alignment.CenterStart)
                    .width(with(density) { (constraints.maxWidth / 1f).toDp() })
            ) {
                onStepContent(i, pos, degrees, inActiveRange)
            }
        }
    }
}