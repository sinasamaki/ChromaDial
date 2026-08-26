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

// Shows a 275° arc dial (startDegrees=225, the standard ChromaDial default) being slowly dragged
// from start to end, emphasizing how the arc fills — illustrating arc configuration.
@OptIn(ExperimentalComposeUiApi::class)
class IntroArcConfigAnimation : AnimationDefinition() {
    override val name = "intro_arc_config"
    override val durationMs = 3000L
    override val width = 400
    override val height = 400

    private val arcClockStart = 225.0
    private val arcClockEnd = 225.0 + 275.0

    private val pressFrame = 15
    private val releaseFrame get() = frameCount - 10

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        when (frameIndex) {
            pressFrame -> scene.sendPointerEventOnArc(PointerEventType.Press, arcClockStart, frameTimeMs)
            releaseFrame -> scene.sendPointerEventOnArc(PointerEventType.Release, arcClockEnd, frameTimeMs)
            in (pressFrame + 1) until releaseFrame -> {
                val t = easeInOut((frameIndex - pressFrame).toFloat() / (releaseFrame - pressFrame))
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockStart + 275.0 * t, frameTimeMs)
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
                            .size(28.dp)
                            .background(Lime500, CircleShape)
                    )
                },
                track = { state ->
                    Box(Modifier.fillMaxSize().drawBehind {
                        val r = state.radius - 14.dp.toPx()
                        drawArc(
                            color = Lime500.copy(alpha = 0.15f),
                            startAngle = state.startDegrees,
                            sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start,
                            radius = r,
                            strokeWidth = 10.dp,
                            strokeCap = StrokeCap.Round,
                        )
                        drawArc(
                            color = Lime500,
                            startAngle = state.startDegrees,
                            sweepAngle = state.degree,
                            radius = r,
                            strokeWidth = 10.dp,
                            strokeCap = StrokeCap.Round,
                        )
                    })
                },
            )
        }
    }
}
