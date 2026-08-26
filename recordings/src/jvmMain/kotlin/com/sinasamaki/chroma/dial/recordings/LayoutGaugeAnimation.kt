package com.sinasamaki.chroma.dial.recordings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.DialLayout
import com.sinasamaki.chroma.dial.RadiusMode
import com.sinasamaki.chroma.dial.drawArc
import kotlin.math.cos
import kotlin.math.sin

// Bottom-anchored gauge using DialLayout(center = (0.5, 1), radiusFraction = 0.9, HEIGHT).
// The dial center sits on the bottom edge, so pointer events use a custom sender.
@OptIn(ExperimentalComposeUiApi::class)
class LayoutGaugeAnimation : AnimationDefinition() {
    override val name = "layout_gauge"
    override val durationMs = 3500L
    override val width = 480
    override val height = 240

    // startDegrees=270 → arc starts at clock 270 (left); sweeps 180° clockwise to clock 90 (right)
    private val arcClockStart = 270.0
    private val pointerRadius = height * 0.9f - 16f  // radius(0.9*height) minus thumb half

    private val pressFrame = 14
    private val peakFrame get() = frameCount / 2
    private val releaseFrame get() = frameCount - 14

    private fun ImageComposeScene.sendAtGauge(type: PointerEventType, clockDeg: Double, time: Long) {
        val cx = constraints.maxWidth / 2f
        val cy = constraints.maxHeight.toFloat()       // center.y fraction = 1.0 → bottom edge
        val rad = Math.toRadians(clockDeg - 90)
        sendPointerEvent(
            eventType = type,
            position = Offset((cx + pointerRadius * cos(rad)).toFloat(), (cy + pointerRadius * sin(rad)).toFloat()),
            timeMillis = time,
            type = PointerType.Touch,
        )
    }

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        val halfCount = (releaseFrame - pressFrame) / 2
        when {
            frameIndex < pressFrame -> return
            frameIndex == pressFrame -> scene.sendAtGauge(PointerEventType.Press, arcClockStart, frameTimeMs)
            frameIndex == releaseFrame -> scene.sendAtGauge(PointerEventType.Release, arcClockStart, frameTimeMs)
            frameIndex <= peakFrame -> {
                val t = easeInOut((frameIndex - pressFrame).toFloat() / halfCount)
                scene.sendAtGauge(PointerEventType.Move, arcClockStart + 180.0 * t, frameTimeMs)
            }
            else -> {
                val t = easeInOut((frameIndex - peakFrame).toFloat() / halfCount)
                scene.sendAtGauge(PointerEventType.Move, arcClockStart + 180.0 * (1f - t), frameTimeMs)
            }
        }
    }

    override val Content: @Composable () -> Unit = {
        var degree by remember { mutableStateOf(0f) }
        Box(
            Modifier.fillMaxSize().background(Neutral950),
            contentAlignment = Alignment.Center,
        ) {
            Dial(
                degree = degree,
                onDegreeChange = { degree = it },
                startDegrees = 270f,
                sweepDegrees = 180f,
                layout = DialLayout(
                    radiusMode = RadiusMode.HEIGHT,
                    radiusFraction = 0.9f,
                    center = Offset(0.5f, 1f),
                ),
                modifier = Modifier.fillMaxSize(),
                thumb = { _ ->
                    Box(
                        Modifier
                            .size(16.dp)
                            .background(Lime300, CircleShape)
                    )
                },
                track = { state ->
                    Box(Modifier.fillMaxSize().drawBehind {
                        val r = state.radius - 8.dp.toPx()
                        drawArc(
                            color = Zinc800,
                            startAngle = state.startDegrees,
                            sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start,
                            radius = r,
                            center = state.center,
                            strokeWidth = 12.dp,
                            strokeCap = StrokeCap.Round,
                        )
                        drawArc(
                            color = Lime500,
                            startAngle = state.startDegrees,
                            sweepAngle = state.degree + state.overshootDegrees,
                            radius = r,
                            center = state.center,
                            strokeWidth = 12.dp,
                            strokeCap = StrokeCap.Round,
                        )
                    }) {
                        Text(
                            text = "${(state.value * 100).toInt()}",
                            color = Lime300,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
                        )
                    }
                },
            )
        }
    }
}
