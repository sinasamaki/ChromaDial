package com.sinasamaki.chroma.dial.recordings

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.dp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.drawArc
import kotlin.math.absoluteValue

// Custom overshoot: thumb shrinks proportionally to overshootDegrees, giving tactile feedback.
// Quick drag past the end — thumb shrinks, then springs back as the dial snaps.
@OptIn(ExperimentalComposeUiApi::class)
class CustomOvershootAnimation : AnimationDefinition() {
    override val name = "custom_overshoot"
    override val durationMs = 4000L
    override val width = 400
    override val height = 400

    private val dragStart = 340.0  // ~50% into arc (startDegrees=225, so clock 340 = ~42% in)
    private val dragEnd = 225.0 + 275.0 + 60.0  // 60° past end

    private val pressFrame = 20
    private val releaseFrame = 80

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        when {
            frameIndex < pressFrame -> return
            frameIndex == pressFrame -> scene.sendPointerEventOnArc(PointerEventType.Press, dragStart, frameTimeMs)
            frameIndex == releaseFrame -> scene.sendPointerEventOnArc(PointerEventType.Release, dragEnd, frameTimeMs)
            frameIndex < releaseFrame -> {
                val t = easeInOut((frameIndex - pressFrame).toFloat() / (releaseFrame - pressFrame))
                scene.sendPointerEventOnArc(PointerEventType.Move, dragStart + (dragEnd - dragStart) * t, frameTimeMs)
            }
            // Remaining frames: spring animation plays out naturally
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
                thumb = { state ->
                    val scale = 1f - (state.overshootDegrees.absoluteValue / 90f).coerceIn(0f, 0.5f)
                    Box(
                        Modifier
                            .size(28.dp)
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                            .background(Lime500, CircleShape)
                            .border(2.dp, Lime300, CircleShape)
                    )
                },
                track = { state ->
                    Box(Modifier.fillMaxSize().drawBehind {
                        val r = state.radius - 14.dp.toPx()
                        drawArc(
                            color = Zinc700,
                            startAngle = state.startDegrees,
                            sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start,
                            radius = r,
                            strokeWidth = 8.dp,
                            strokeCap = StrokeCap.Round,
                        )
                        drawArc(
                            color = Lime500,
                            startAngle = state.startDegrees,
                            sweepAngle = state.degree,
                            radius = r,
                            strokeWidth = 8.dp,
                            strokeCap = StrokeCap.Round,
                        )
                    })
                },
            )
        }
    }
}
