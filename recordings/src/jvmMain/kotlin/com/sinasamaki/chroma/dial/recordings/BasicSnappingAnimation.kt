package com.sinasamaki.chroma.dial.recordings

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
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

// Step snapping: steps=6 on a 270° sweep (45° per step) — large, obvious jumps.
// Bouncy spring makes each snap clearly visible.
@OptIn(ExperimentalComposeUiApi::class)
class BasicSnappingAnimation : AnimationDefinition() {
    override val name = "basics_snapping"
    override val durationMs = 4500L
    override val width = 400
    override val height = 400

    private val arcClockStart = 225.0
    private val arcClockEnd = 225.0 + 270.0

    private val pressFrame = 20
    private val releaseFrame get() = frameCount - 20

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        when {
            frameIndex < pressFrame -> return
            frameIndex == pressFrame -> scene.sendPointerEventOnArc(PointerEventType.Press, arcClockStart, frameTimeMs)
            frameIndex == releaseFrame -> scene.sendPointerEventOnArc(PointerEventType.Release, arcClockEnd, frameTimeMs)
            else -> {
                val t = easeInOut((frameIndex - pressFrame).toFloat() / (releaseFrame - pressFrame))
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockStart + 270.0 * t, frameTimeMs)
            }
        }
    }

    override val Content: @Composable () -> Unit = {
        var degree by remember { mutableStateOf(0f) }
        val animatedDegree by animateFloatAsState(
            targetValue = degree,
            animationSpec = spring(
                stiffness = Spring.StiffnessMedium,
                dampingRatio = Spring.DampingRatioMediumBouncy,
            ),
        )
        Box(
            Modifier.fillMaxSize().background(Neutral950).padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Dial(
                degree = animatedDegree,
                onDegreeChange = { degree = it },
                startDegrees = 225f,
                sweepDegrees = 270f,
                steps = 6,
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
