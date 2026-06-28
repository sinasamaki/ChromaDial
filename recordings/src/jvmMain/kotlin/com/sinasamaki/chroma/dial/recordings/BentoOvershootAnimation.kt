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

// Overshoot: quick drag from mid-point to 60° past end, release, spring-back plays.
// Starting at mid-point (not 0) means we spend more screen time on the overshoot effect.
@OptIn(ExperimentalComposeUiApi::class)
class BentoOvershootAnimation : AnimationDefinition() {
    override val name = "bento_overshoot"
    override val durationMs = 4000L
    override val width = 400
    override val height = 400

    // Arc: startDegrees=225, sweepDegrees=275. End = clock 500°.
    // We start from mid-arc (clock 340°) and drag to 60° past end (clock 560°).
    private val dragStart = 340.0  // ~50% into arc
    private val dragEnd = 225.0 + 275.0 + 60.0  // 560 = past end by 60°

    private val pressFrame = 20
    private val releaseFrame = 80  // quick drag in ~1s → leaves ~2.7s for spring

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        when {
            frameIndex < pressFrame -> return
            frameIndex == pressFrame -> scene.sendPointerEventOnArc(PointerEventType.Press, dragStart, frameTimeMs)
            frameIndex == releaseFrame -> scene.sendPointerEventOnArc(PointerEventType.Release, dragEnd, frameTimeMs)
            frameIndex < releaseFrame -> {
                val t = easeInOut((frameIndex - pressFrame).toFloat() / (releaseFrame - pressFrame))
                scene.sendPointerEventOnArc(PointerEventType.Move, dragStart + (dragEnd - dragStart) * t, frameTimeMs)
            }
            // Remaining 2.7s: spring animation plays out naturally
        }
    }

    override val Content: @Composable () -> Unit = {
        var degree by remember { mutableStateOf(115f) }  // start at ~42% so mid-arc is visible from frame 0
        Box(
            Modifier.fillMaxSize().background(Neutral950).padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Dial(
                degree = degree,
                onDegreeChange = { degree = it },
                startDegrees = 225f,
                sweepDegrees = 275f,
                overshootDecay = 0.38f,
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
