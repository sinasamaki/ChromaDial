package com.sinasamaki.chroma.dial.recordings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.dp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.drawArc

// 275° arc, drag from start → end → back in a smooth loop
@OptIn(ExperimentalComposeUiApi::class)
class HeroAnimation : AnimationDefinition() {
    override val name = "hero"
    override val durationMs = 4000L
    override val width = 400
    override val height = 400

    // startDegrees=225 → 0° on arc = clock angle 225 (7:30 position)
    // sweep=275° → end is at clock angle 225+275=500 = 140° (4:40 position)
    // Clock angles: arc start = 225, arc end = 225+275=500 mod 360 = 140
    // But sendPointerEventOnArc uses clock convention where 0=top(12), 90=right(3)
    // ChromaDial startDegrees=225 means arc starts at 225° screen → clock angle 225
    private val arcClockStart = 225.0
    private val arcClockEnd = 225.0 + 275.0  // = 500 → wraps but pointer follows arc

    private val pressFrame = 8
    private val peakFrame get() = frameCount / 2
    private val releaseFrame get() = frameCount - 8

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        val halfCount = (releaseFrame - pressFrame) / 2
        val t = when {
            frameIndex < pressFrame -> return
            frameIndex == pressFrame -> {
                scene.sendPointerEventOnArc(PointerEventType.Press, arcClockStart, frameTimeMs)
                return
            }
            frameIndex == releaseFrame -> {
                scene.sendPointerEventOnArc(PointerEventType.Release, arcClockStart, frameTimeMs)
                return
            }
            frameIndex <= peakFrame -> {
                easeInOut((frameIndex - pressFrame).toFloat() / halfCount)
            }
            else -> {
                1f - easeInOut((frameIndex - peakFrame).toFloat() / halfCount)
            }
        }
        val angle = arcClockStart + 275.0 * t
        scene.sendPointerEventOnArc(PointerEventType.Move, angle, frameTimeMs)
    }

    override val Content: @Composable () -> Unit = {
        var degree by remember { mutableStateOf(0f) }
        Box(
            Modifier.fillMaxSize().background(Neutral950).padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            val interaction = remember { MutableInteractionSource() }
            val isDragged by interaction.collectIsDraggedAsState()
            val dragScale by animateFloatAsState(if (isDragged) 1.35f else 1f)

            Dial(
                degree = degree,
                onDegreeChange = { degree = it },
                startDegrees = 225f,
                sweepDegrees = 275f,
                modifier = Modifier.fillMaxSize(),
                interactionSource = interaction,
                thumb = { _ ->
                    Box(
                        Modifier
                            .size(36.dp)
                            .background(Lime500.copy(alpha = 0.35f), CircleShape)
                            .border(2.dp, Lime500, CircleShape)
                            .drawBehind {
                                scale(dragScale) {
                                    drawCircle(color = Lime500.copy(alpha = 0.25f))
                                }
                            }
                    )
                },
                track = { state ->
                    Box(Modifier.fillMaxSize().drawBehind {
                        val trackRadius = state.radius - 18.dp.toPx()
                        drawArc(
                            color = Lime500.copy(alpha = 0.15f),
                            startAngle = state.startDegrees,
                            sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start,
                            radius = trackRadius,
                            strokeWidth = 8.dp,
                            strokeCap = StrokeCap.Round,
                        )
                        drawArc(
                            color = Lime500,
                            startAngle = state.startDegrees,
                            sweepAngle = state.degree + state.overshootDegrees,
                            radius = trackRadius,
                            strokeWidth = 8.dp,
                            strokeCap = StrokeCap.Round,
                        )
                    })
                },
            )
        }
    }
}
