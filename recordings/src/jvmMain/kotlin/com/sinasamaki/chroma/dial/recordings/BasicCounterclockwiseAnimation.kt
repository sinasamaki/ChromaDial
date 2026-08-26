package com.sinasamaki.chroma.dial.recordings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.drawArc

// Counterclockwise: degree animates 0 → 270 → 0 on a clockwise=false dial.
// Note: for CCW dials, state.degree returns a NEGATIVE value (-degreeState), so arc math
// uses state.absoluteDegree for the thumb position and (-state.degree) for the sweep magnitude.
@OptIn(ExperimentalComposeUiApi::class)
class BasicCounterclockwiseAnimation : AnimationDefinition() {
    override val name = "basics_counterclockwise"
    override val durationMs = 4000L
    override val width = 400
    override val height = 400

    private var degree by mutableFloatStateOf(0f)

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        // Hold 5% → sweep to 270° over 45% → hold 15% → sweep back 30% → hold 5%
        degree = when {
            progress < 0.05f -> 0f
            progress < 0.50f -> easeInOut((progress - 0.05f) / 0.45f) * 270f
            progress < 0.65f -> 270f
            progress < 0.95f -> easeInOut(1f - (progress - 0.65f) / 0.30f) * 270f
            else -> 0f
        }
    }

    override val Content: @Composable () -> Unit = {
        Box(
            Modifier.fillMaxSize().background(Neutral950).padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Dial(
                degree = degree,
                onDegreeChange = { degree = it },
                startDegrees = 225f,
                sweepDegrees = 270f,
                clockwise = false,
                modifier = Modifier.fillMaxSize(),
                thumb = { _ ->
                    Box(
                        Modifier
                            .size(28.dp)
                            .background(Lime500, CircleShape)
                            .border(2.dp, Lime300, CircleShape)
                    )
                },
                track = { state ->
                    Box(Modifier.fillMaxSize().drawBehind {
                        val r = state.radius - 14.dp.toPx()
                        val magnitude = state.degreeRange.endInclusive - state.degreeRange.start  // 270f
                        // state.degree is negative for CCW dials, so activeSize = -state.degree
                        val activeSize = -state.degree

                        // Background arc: full CCW span (drawn as CW from the CCW-end back to start)
                        drawArc(
                            color = Zinc700,
                            startAngle = state.startDegrees - magnitude,  // 225 - 270 = -45
                            sweepAngle = magnitude,
                            radius = r,
                            strokeWidth = 8.dp,
                            strokeCap = StrokeCap.Round,
                        )

                        // Active arc: from absoluteDegree (thumb position) CW to startDegrees
                        if (activeSize > 0f) {
                            drawArc(
                                color = Lime500,
                                startAngle = state.absoluteDegree,  // thumb's screen angle
                                sweepAngle = activeSize,            // grows as degree increases
                                radius = r,
                                strokeWidth = 8.dp,
                                strokeCap = StrokeCap.Round,
                            )
                        }
                    })
                },
            )
        }
    }
}
