package com.sinasamaki.chroma.dial.recordings

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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

// State overshoot: clear demonstration of the overshoot effect.
// Starts near the end of the arc, quick drag past the limit, spring plays back.
@OptIn(ExperimentalComposeUiApi::class)
class StateOvershootAnimation : AnimationDefinition() {
    override val name = "state_overshoot"
    override val durationMs = 4500L
    override val width = 400
    override val height = 400

    // Arc: startDegrees=225, sweepDegrees=275. Initial degree=200f (nearly full).
    private val dragStart = 225.0 + 200.0  // 425.0 — thumb's initial position
    private val dragEnd = 225.0 + 275.0 + 70.0  // 570.0 — 70° past the end

    private val pressFrame = 15
    private val releaseFrame = 75

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        when {
            frameIndex < pressFrame -> return
            frameIndex == pressFrame -> scene.sendPointerEventOnArc(PointerEventType.Press, dragStart, frameTimeMs)
            frameIndex == releaseFrame -> scene.sendPointerEventOnArc(PointerEventType.Release, dragEnd, frameTimeMs)
            frameIndex < releaseFrame -> {
                val t = easeInOut((frameIndex - pressFrame).toFloat() / (releaseFrame - pressFrame))
                scene.sendPointerEventOnArc(PointerEventType.Move, dragStart + (dragEnd - dragStart) * t, frameTimeMs)
            }
            // Remaining ~3.7s: spring animation plays out naturally
        }
    }

    override val Content: @Composable () -> Unit = {
        var degree by remember { mutableStateOf(200f) }
        Box(
            Modifier.fillMaxSize().background(Neutral950).padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Dial(
                degree = degree,
                onDegreeChange = { degree = it },
                startDegrees = 225f,
                sweepDegrees = 275f,
                overshootDecay = 0.35f,
                overshootAnimationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    dampingRatio = Spring.DampingRatioMediumBouncy,
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
