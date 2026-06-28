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
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.dp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.DialColors

// Arc shape: classic knob arc (startDegrees=225, sweepDegrees=270) dragged from start to end and back.
// The inactive track clearly shows the arc's full span.
@OptIn(ExperimentalComposeUiApi::class)
class BasicArcShapeAnimation : AnimationDefinition() {
    override val name = "basics_arc_shape"
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
                modifier = Modifier.fillMaxSize(),
                colors = DialColors.default(
                    activeTrackColor = Lime500,
                    thumbStrokeColor = Lime400,
                    activeTickColor = Lime300,
                    inactiveTrackColor = Zinc700,
                    inactiveTickColor = Zinc700,
                ),
            )
        }
    }
}
