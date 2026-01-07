package com.sinasamaki.chroma.dial

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2

fun DrawScope.drawEveryStep(
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

fun DrawScope.drawEveryStep(
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