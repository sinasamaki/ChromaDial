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

    for (i in 0..steps) {
        val progress = i / steps.toFloat()
        val distance = progress * measure.length
        val pos = measure.getPosition(distance)
        val tangent = measure.getTangent(distance)
        val degrees = atan2(tangent.y, tangent.x) * 180f / PI.toFloat()

        val inActiveRange = progress <= dialState.value

        onDraw(pos, degrees, inActiveRange)
    }
}