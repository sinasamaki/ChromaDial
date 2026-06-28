package com.sinasamaki.chroma.dial.recordings

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.dp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.drawArc
import com.sinasamaki.chroma.dial.drawEveryInterval

// Draw utilities: large dot tick marks via drawEveryInterval — active dots are bright Lime300,
// inactive are a clearly visible Zinc500. Slow sweep to ~60% then back so activation is legible.
@OptIn(ExperimentalComposeUiApi::class)
class BentoDrawUtilitiesAnimation : AnimationDefinition() {
    override val name = "bento_draw_utilities"
    override val durationMs = 5000L
    override val width = 400
    override val height = 400

    private val arcClockStart = 225.0
    // Only sweep to ~60% of the arc (165°) so dots don't crowd the frame
    private val arcClockMid = 225.0 + 165.0

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
                // Tiny thumb so it doesn't crowd the dots
                thumb = { _ ->
                    Box(
                        Modifier
                            .size(18.dp)
                            .background(Lime300, CircleShape)
                            .drawBehind {
                                drawCircle(color = Lime300.copy(alpha = 0.3f), radius = 13.dp.toPx())
                            }
                    )
                },
                track = { state ->
                    Box(Modifier.fillMaxSize().drawBehind {
                        val r = state.radius - 9.dp.toPx()  // thumb half = 9dp for 18dp thumb
                        val sweep = state.degreeRange.endInclusive - state.degreeRange.start

                        // Very thin background arc — just a guide line
                        drawArc(
                            color = Zinc800,
                            startAngle = state.startDegrees,
                            sweepAngle = sweep,
                            radius = r,
                            strokeWidth = 2.dp,
                            strokeCap = StrokeCap.Round,
                        )

                        // Large, clearly visible dot tick marks
                        drawEveryInterval(
                            startDegrees = state.startDegrees,
                            sweepDegrees = sweep,
                            radius = r,
                            interval = 25f,
                            currentDegree = state.degree,
                        ) { data ->
                            if (data.inActiveRange) {
                                // Active: bright large dot with a subtle glow ring
                                drawCircle(
                                    color = Lime400.copy(alpha = 0.25f),
                                    radius = 9.dp.toPx(),
                                    center = data.position,
                                )
                                drawCircle(
                                    color = Lime300,
                                    radius = 5.dp.toPx(),
                                    center = data.position,
                                )
                            } else {
                                // Inactive: visible but muted
                                drawCircle(
                                    color = Zinc500,
                                    radius = 4.dp.toPx(),
                                    center = data.position,
                                )
                            }
                        }
                    })
                },
            )
        }
    }
}
