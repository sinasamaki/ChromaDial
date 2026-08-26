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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.drawArc

// Mapped value: valueRange=0f..100f with the current integer value shown large in the center.
// Demonstrates how mappedValue relates to the dial's position.
@OptIn(ExperimentalComposeUiApi::class)
class BasicMappedValueAnimation : AnimationDefinition() {
    override val name = "basics_mapped_value"
    override val durationMs = 4000L
    override val width = 400
    override val height = 400

    private val arcClockStart = 225.0
    private val arcClockEnd = 225.0 + 270.0

    private val pressFrame = 20
    private val peakFrame get() = (frameCount * 0.50f).toInt()
    private val holdEnd get() = (frameCount * 0.65f).toInt()
    private val releaseFrame get() = frameCount - 20

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        when {
            frameIndex < pressFrame -> return
            frameIndex == pressFrame -> scene.sendPointerEventOnArc(PointerEventType.Press, arcClockStart, frameTimeMs)
            frameIndex == releaseFrame -> scene.sendPointerEventOnArc(PointerEventType.Release, arcClockStart, frameTimeMs)
            frameIndex <= peakFrame -> {
                val t = easeInOut((frameIndex - pressFrame).toFloat() / (peakFrame - pressFrame))
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockStart + 270.0 * t, frameTimeMs)
            }
            frameIndex <= holdEnd -> {
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockEnd, frameTimeMs)
            }
            else -> {
                val t = easeInOut((frameIndex - holdEnd).toFloat() / (releaseFrame - holdEnd))
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockEnd - 270.0 * t, frameTimeMs)
            }
        }
    }

    override val Content: @Composable () -> Unit = {
        var degree by remember { mutableStateOf(0f) }
        Box(
            Modifier.fillMaxSize().background(Neutral950).padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Dial(
                degree = degree,
                onDegreeChange = { degree = it },
                startDegrees = 225f,
                sweepDegrees = 270f,
                valueRange = 0f..100f,
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
                        drawArc(
                            color = Zinc700,
                            startAngle = state.startDegrees,
                            sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start,
                            radius = r,
                            strokeWidth = 8.dp,
                            strokeCap = StrokeCap.Round,
                        )
                        drawArc(
                            color = Lime500,
                            startAngle = state.startDegrees,
                            sweepAngle = state.degree,
                            radius = r,
                            strokeWidth = 8.dp,
                            strokeCap = StrokeCap.Round,
                        )
                    }) {
                        Text(
                            text = "${state.mappedValue.toInt()}",
                            modifier = Modifier.align(Alignment.Center),
                            color = Lime300,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
            )
        }
    }
}
