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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.unit.dp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.DialColors
import com.sinasamaki.chroma.dial.DialLayout
import kotlin.math.cos
import kotlin.math.sin

// True semi-circle: startDegrees=270, sweepDegrees=180, with DialLayout.height(fraction = 1f,
// center = (0.5, 1)) so the radius fills the height and the flat edge sits on the bottom.
// The dial center is on the bottom edge, so pointer events use a custom sender.
@OptIn(ExperimentalComposeUiApi::class)
class BasicSemiCircleAnimation : AnimationDefinition() {
    override val name = "basics_semi_circle"
    override val durationMs = 3000L
    override val width = 480
    override val height = 252

    // density is 2f (see runFrameLoop); 24.dp bottom padding = 48px of room for the thumb.
    private val bottomPadPx = 48f
    private val dialHeightPx get() = height - bottomPadPx          // radius = fraction(1) * height
    private val dialCenterX get() = width / 2f
    private val dialCenterY get() = dialHeightPx                   // center.y fraction = 1.0
    private val pointerRadius get() = dialHeightPx - 24f           // radius minus thumb half (24dp/2 px)

    // startDegrees=270 (left/9 o'clock), sweep 180° → ends at clock 90 (right/3 o'clock)
    private val arcClockStart = 270.0

    private val pressFrame = 10
    private val peakFrame get() = frameCount / 2
    private val releaseFrame get() = frameCount - 10

    private fun ImageComposeScene.sendAtArc(type: PointerEventType, clockDeg: Double, time: Long) {
        val rad = Math.toRadians(clockDeg - 90)
        sendPointerEvent(
            eventType = type,
            position = Offset(
                (dialCenterX + pointerRadius * cos(rad)).toFloat(),
                (dialCenterY + pointerRadius * sin(rad)).toFloat(),
            ),
            timeMillis = time,
            type = PointerType.Touch,
        )
    }

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        val halfCount = (releaseFrame - pressFrame) / 2
        when {
            frameIndex < pressFrame -> return
            frameIndex == pressFrame -> scene.sendAtArc(PointerEventType.Press, arcClockStart, frameTimeMs)
            frameIndex == releaseFrame -> scene.sendAtArc(PointerEventType.Release, arcClockStart, frameTimeMs)
            frameIndex <= peakFrame -> {
                val t = easeInOut((frameIndex - pressFrame).toFloat() / halfCount)
                scene.sendAtArc(PointerEventType.Move, arcClockStart + 180.0 * t, frameTimeMs)
            }
            else -> {
                val t = easeInOut((frameIndex - peakFrame).toFloat() / halfCount)
                scene.sendAtArc(PointerEventType.Move, arcClockStart + 180.0 * (1f - t), frameTimeMs)
            }
        }
    }

    override val Content: @Composable () -> Unit = {
        var degree by remember { mutableStateOf(0f) }
        Box(
            Modifier.fillMaxSize()
                .background(Neutral950)
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Dial(
                degree = degree,
                onDegreeChange = { degree = it },
                startDegrees = 270f,
                sweepDegrees = 180f,
                layout = DialLayout.height(
                    fraction = 1f,
                    center = Offset(.5f, 1f),
                ),
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
