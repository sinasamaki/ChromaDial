package com.sinasamaki.chroma.dial.recordings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.DialColors

// Demonstrates MutableInteractionSource: a center badge flips between "Idle" and "Dragging"
// as the simulated pointer presses, sweeps, and releases.
@OptIn(ExperimentalComposeUiApi::class)
class InputInteractionAnimation : AnimationDefinition() {
    override val name = "input_interaction"
    override val durationMs = 3500L
    override val width = 400
    override val height = 400

    private val arcClockStart = 225.0

    private val pressFrame = 18
    private val peakFrame get() = frameCount / 2
    private val releaseFrame get() = frameCount - 18

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        val halfCount = (releaseFrame - pressFrame) / 2
        when {
            frameIndex < pressFrame -> return
            frameIndex == pressFrame -> scene.sendPointerEventOnArc(PointerEventType.Press, arcClockStart, frameTimeMs)
            frameIndex == releaseFrame -> scene.sendPointerEventOnArc(PointerEventType.Release, arcClockStart, frameTimeMs)
            frameIndex <= peakFrame -> {
                val t = easeInOut((frameIndex - pressFrame).toFloat() / halfCount)
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockStart + 275.0 * t, frameTimeMs)
            }
            else -> {
                val t = easeInOut((frameIndex - peakFrame).toFloat() / halfCount)
                scene.sendPointerEventOnArc(PointerEventType.Move, arcClockStart + 275.0 * (1f - t), frameTimeMs)
            }
        }
    }

    override val Content: @Composable () -> Unit = {
        var degree by remember { mutableStateOf(0f) }
        val interaction = remember { MutableInteractionSource() }
        val isDragged by interaction.collectIsDraggedAsState()

        val badgeColor by animateColorAsState(if (isDragged) Lime500 else Zinc800)
        val labelColor by animateColorAsState(if (isDragged) Zinc950 else Zinc400)

        Box(
            Modifier.fillMaxSize().background(Neutral950).padding(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Dial(
                degree = degree,
                onDegreeChange = { degree = it },
                startDegrees = 225f,
                sweepDegrees = 275f,
                modifier = Modifier.fillMaxSize(),
                interactionSource = interaction,
                colors = DialColors.default(
                    inactiveTrackColor = Zinc800,
                    activeTrackColor = Lime500,
                    thumbColor = Zinc950,
                    thumbStrokeColor = Lime400,
                ),
            )

            Box(
                Modifier
                    .background(badgeColor, RoundedCornerShape(50))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = if (isDragged) "Dragging" else "Idle",
                    color = labelColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
