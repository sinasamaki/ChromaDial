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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.dp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.drawArc
import com.sinasamaki.chroma.dial.drawEveryInterval

// Custom tick marks: radial line ticks via drawEveryInterval.
// Active ticks are Lime500, inactive are Zinc600.
@OptIn(ExperimentalComposeUiApi::class)
class CustomTickMarksAnimation : AnimationDefinition() {
    override val name = "custom_tick_marks"
    override val durationMs = 4000L
    override val width = 400
    override val height = 400

    private val arcClockStart = 225.0
    private val arcClockMid = 225.0 + 165.0  // ~60% of arc

    private val pressFrame = 25
    private val peakFrame get() = (frameCount * 0.45f).toInt()
    private val holdEnd get() = (frameCount * 0.65f).toInt()
    private val releaseFrame get() = frameCount - 25

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        when {
            frameIndex < pressFrame -> return
            frameIndex == pressFrame -> scene.sendPointerEventOnArc(PointerEventType.Press, arcClockStart, frameTimeMs)
            frameIndex == releaseFrame -> scene.sendPointerEventOnArc(PointerEventType.Release, arcClockStart, frameTimeMs)
            frameIndex <= peakFrame -> {
                val t = easeInOut((frameIndex - pressFrame).toFloat() / (peakFrame - pressFrame))
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockStart + 165.0 * t, frameTimeMs)
            }
            frameIndex <= holdEnd -> {
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockMid, frameTimeMs)
            }
            else -> {
                val t = easeInOut((frameIndex - holdEnd).toFloat() / (releaseFrame - holdEnd))
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockMid - 165.0 * t, frameTimeMs)
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
                    Box(
                        Modifier
                            .size(20.dp)
                            .background(Lime500, CircleShape)
                            .border(1.dp, Lime300, CircleShape)
                    )
                },
                track = { state ->
                    Box(Modifier.fillMaxSize().drawBehind {
                        val r = state.radius - 10.dp.toPx()
                        val sweep = state.degreeRange.endInclusive - state.degreeRange.start

                        drawArc(
                            color = Zinc800,
                            startAngle = state.startDegrees,
                            sweepAngle = sweep,
                            radius = r,
                            strokeWidth = 3.dp,
                            strokeCap = StrokeCap.Round,
                        )

                        drawEveryInterval(
                            startDegrees = state.startDegrees,
                            sweepDegrees = sweep,
                            radius = r,
                            interval = 20f,
                            currentDegree = state.degree,
                        ) { data ->
                            rotate(data.rotationAngle, pivot = data.position) {
                                drawLine(
                                    color = if (data.inActiveRange) Lime500 else Zinc600,
                                    start = data.position,
                                    end = data.position + Offset(0f, 14f),
                                    strokeWidth = 2.5.dp.toPx(),
                                    cap = StrokeCap.Round,
                                )
                            }
                        }
                    })
                },
            )
        }
    }
}
