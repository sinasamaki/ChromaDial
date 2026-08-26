package com.sinasamaki.chroma.dial.recordings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.DialColors

// Multi-ring display: 720° sweep, degree animated programmatically via class-level state
// so the second ring appears and the ring overflow animation is clearly visible.
@OptIn(ExperimentalComposeUiApi::class)
class IntroMultiRingAnimation : AnimationDefinition() {
    override val name = "intro_multi_ring"
    override val durationMs = 4000L
    override val width = 400
    override val height = 400

    private var degree by mutableFloatStateOf(0f)

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        // Animate 0 → 720 (two full rings), ease in/out
        degree = easeInOut(progress) * 720f
    }

    override val Content: @Composable () -> Unit = {
        Box(
            Modifier.fillMaxSize().background(Neutral950).padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Dial(
                degree = degree,
                onDegreeChange = { degree = it },
                modifier = Modifier.fillMaxSize(),
                startDegrees = 225f,
                sweepDegrees = 720f,
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
