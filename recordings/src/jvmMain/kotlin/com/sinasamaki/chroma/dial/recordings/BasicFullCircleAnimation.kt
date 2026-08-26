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

// Full circle 360° dial — drag around the whole circumference and back.
@OptIn(ExperimentalComposeUiApi::class)
class BasicFullCircleAnimation : AnimationDefinition() {
    override val name = "basics_full_circle"
    override val durationMs = 3000L
    override val width = 400
    override val height = 400

    // startDegrees=0 means arc starts at top (12 o'clock = clock angle 0)
    private val arcClockStart = 0.0

    private val pressFrame = 10
    private val peakFrame get() = frameCount / 2
    private val releaseFrame get() = frameCount - 10

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        val halfCount = (releaseFrame - pressFrame) / 2
        when {
            frameIndex < pressFrame -> return
            frameIndex == pressFrame -> scene.sendPointerEventOnArc(PointerEventType.Press, arcClockStart, frameTimeMs)
            frameIndex == releaseFrame -> scene.sendPointerEventOnArc(PointerEventType.Release, arcClockStart, frameTimeMs)
            frameIndex <= peakFrame -> {
                val t = easeInOut((frameIndex - pressFrame).toFloat() / halfCount)
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockStart + 360.0 * t, frameTimeMs)
            }
            else -> {
                val t = easeInOut((frameIndex - peakFrame).toFloat() / halfCount)
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockStart + 360.0 * (1f - t), frameTimeMs)
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
                startDegrees = 0f,
                sweepDegrees = 360f,
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
