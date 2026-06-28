package com.sinasamaki.chroma.dial.recordings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.drawArc

// Invisible thumb: fills the entire area (whole component draggable) with a thick custom arc track.
// The track is the only visual indicator of position.
@OptIn(ExperimentalComposeUiApi::class)
class CustomInvisibleThumbAnimation : AnimationDefinition() {
    override val name = "custom_invisible_thumb"
    override val durationMs = 4000L
    override val width = 400
    override val height = 400

    private val arcClockStart = 225.0
    private val arcClockEnd = 225.0 + 275.0

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
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockStart + 275.0 * t, frameTimeMs)
            }
            frameIndex <= holdEnd -> {
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockEnd, frameTimeMs)
            }
            else -> {
                val t = easeInOut((frameIndex - holdEnd).toFloat() / (releaseFrame - holdEnd))
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockEnd - 275.0 * t, frameTimeMs)
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
                sweepDegrees = 275f,
                modifier = Modifier.fillMaxSize(),
                thumb = { _ ->
                    Box(Modifier.fillMaxSize())
                },
                track = { state ->
                    Box(Modifier.fillMaxSize().drawBehind {
                        val sweep = state.degreeRange.endInclusive - state.degreeRange.start
                        drawArc(
                            color = Zinc800,
                            startAngle = state.startDegrees,
                            sweepAngle = sweep,
                            radius = state.radius,
                            strokeWidth = 20.dp,
                            strokeCap = StrokeCap.Round,
                        )
                        drawArc(
                            color = Lime500,
                            startAngle = state.startDegrees,
                            sweepAngle = state.degree,
                            radius = state.radius,
                            strokeWidth = 20.dp,
                            strokeCap = StrokeCap.Round,
                        )
                    })
                },
            )
        }
    }
}
