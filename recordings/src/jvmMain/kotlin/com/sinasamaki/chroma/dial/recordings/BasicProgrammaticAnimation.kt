package com.sinasamaki.chroma.dial.recordings

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.drawArc

// Programmatic animation: degree animates 0 → 275 → 0 purely via class-level state.
// Shows a "button" label in the center and the dial responding to programmatic control.
@OptIn(ExperimentalComposeUiApi::class)
class BasicProgrammaticAnimation : AnimationDefinition() {
    override val name = "basics_programmatic"
    override val durationMs = 3000L
    override val width = 400
    override val height = 400

    private var targetDegree by mutableFloatStateOf(0f)

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        // Phase 1 (0–40%): animate to 275°, Phase 2 (60–100%): animate back to 0
        targetDegree = when {
            progress < 0.4f -> easeInOut(progress / 0.4f) * 275f
            progress < 0.6f -> 275f
            else -> easeInOut(1f - (progress - 0.6f) / 0.4f) * 275f
        }
    }

    override val Content: @Composable () -> Unit = {
        var degree by mutableFloatStateOf(0f)
        degree = targetDegree

        val animatedDegree by animateFloatAsState(
            targetValue = degree,
            animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioLowBouncy),
        )

        Box(
            Modifier.fillMaxSize().background(Neutral950).padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Dial(
                degree = animatedDegree,
                onDegreeChange = { degree = it },
                startDegrees = 225f,
                sweepDegrees = 275f,
                modifier = Modifier.fillMaxSize(),
                thumb = { _ ->
                    Box(
                        Modifier
                            .size(28.dp)
                            .background(Lime500, CircleShape)
                            .border(2.dp, Lime300, CircleShape)
                    )
                },
                track = { state ->
                    Box(Modifier.fillMaxSize().drawBehind {
                        val r = state.radius - 14.dp.toPx()
                        drawArc(
                            color = Lime500.copy(alpha = 0.15f),
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
                    }) {
                        Box(
                            Modifier
                                .align(Alignment.Center)
                                .background(Zinc700, RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = "animateTo()",
                                color = Lime400,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                },
            )
        }
    }
}
