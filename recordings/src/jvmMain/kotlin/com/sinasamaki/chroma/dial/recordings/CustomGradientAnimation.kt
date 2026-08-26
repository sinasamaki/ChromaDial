package com.sinasamaki.chroma.dial.recordings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.drawArc

// Custom track with a gradient active arc + a value readout in the center.
@OptIn(ExperimentalComposeUiApi::class)
class CustomGradientAnimation : AnimationDefinition() {
    override val name = "custom_gradient"
    override val durationMs = 4000L
    override val width = 400
    override val height = 400

    private val arcClockStart = 225.0

    private val pressFrame = 12
    private val peakFrame get() = frameCount / 2
    private val releaseFrame get() = frameCount - 12

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        val halfCount = (releaseFrame - pressFrame) / 2
        when {
            frameIndex < pressFrame -> return
            frameIndex == pressFrame -> scene.sendPointerEventOnArc(PointerEventType.Press, arcClockStart, frameTimeMs)
            frameIndex == releaseFrame -> scene.sendPointerEventOnArc(PointerEventType.Release, arcClockStart, frameTimeMs)
            frameIndex <= peakFrame -> {
                val t = easeInOut((frameIndex - pressFrame).toFloat() / halfCount)
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockStart + 275.0 * t, frameTimeMs)
            }
            else -> {
                val t = easeInOut((frameIndex - peakFrame).toFloat() / halfCount)
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockStart + 275.0 * (1f - t), frameTimeMs)
            }
        }
    }

    override val Content: @Composable () -> Unit = {
        var degree by remember { mutableStateOf(0f) }
        Box(
            Modifier.fillMaxSize().background(Neutral950).padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Dial(
                degree = degree,
                onDegreeChange = { degree = it },
                startDegrees = 225f,
                sweepDegrees = 275f,
                modifier = Modifier.fillMaxSize(),
                thumb = { _ ->
                    Box(
                        Modifier
                            .size(22.dp)
                            .background(Zinc200, CircleShape)
                            .border(3.dp, Teal400, CircleShape)
                    )
                },
                track = { state ->
                    Box(Modifier.fillMaxSize().drawBehind {
                        val r = state.radius - 11.dp.toPx()
                        drawArc(
                            color = Zinc800,
                            startAngle = state.startDegrees,
                            sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start,
                            radius = r,
                            strokeWidth = 16.dp,
                            strokeCap = StrokeCap.Round,
                        )
                        drawArc(
                            brush = Brush.linearGradient(listOf(Cyan400, Teal400, Lime300)),
                            startAngle = state.startDegrees,
                            sweepAngle = state.degree + state.overshootDegrees,
                            radius = r,
                            strokeWidth = 16.dp,
                            strokeCap = StrokeCap.Round,
                        )
                    }) {
                        Text(
                            text = "${(state.value * 100).toInt()}%",
                            color = Zinc200,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                },
            )
        }
    }
}
