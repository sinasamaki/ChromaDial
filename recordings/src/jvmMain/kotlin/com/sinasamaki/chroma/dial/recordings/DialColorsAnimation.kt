package com.sinasamaki.chroma.dial.recordings

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.DialColors

// Showcases DialColors: a 2×2 grid of the default dial styled with four different
// palettes (lime, blue, rose, amber). All dials share one programmatically animated
// degree so the point reads clearly — same component, different colors.
@OptIn(ExperimentalComposeUiApi::class)
class DialColorsAnimation : AnimationDefinition() {
    override val name = "dial_colors"
    override val durationMs = 4000L
    override val width = 1200
    override val height = 340

    private var animatedDegree by mutableFloatStateOf(0f)

    override fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {
        // Sweep up to full, hold, then sweep back.
        animatedDegree = when {
            progress < 0.4f -> easeInOut(progress / 0.4f) * 275f
            progress < 0.6f -> 275f
            else -> easeInOut(1f - (progress - 0.6f) / 0.4f) * 275f
        }
    }

    private data class Palette(
        val active: Color,
        val thumbStroke: Color,
        val activeTick: Color,
    )

    private val palettes = listOf(
        Palette(Lime500, Lime400, Lime300),
        Palette(Blue500, Blue400, Blue300),
        Palette(Rose500, Rose400, Rose300),
        Palette(Amber500, Amber400, Amber300),
    )

    @Composable
    private fun ColoredDial(palette: Palette, modifier: Modifier) {
        val degree by animateFloatAsState(
            targetValue = animatedDegree,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
        )
        Dial(
            degree = degree,
            onDegreeChange = { },
            startDegrees = 225f,
            sweepDegrees = 275f,
            interval = 25f,
            enabled = false,
            modifier = modifier,
            colors = DialColors.default(
                inactiveTrackColor = Zinc800,
                activeTrackColor = palette.active,
                thumbColor = Zinc950,
                thumbStrokeColor = palette.thumbStroke,
                inactiveTickColor = Zinc700,
                activeTickColor = palette.activeTick,
            ),
        )
    }

    override val Content: @Composable () -> Unit = {
        Row(
            Modifier.fillMaxSize().background(Neutral950).padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (palette in palettes) {
                Box(
                    Modifier.weight(1f).fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    ColoredDial(palette, Modifier.fillMaxSize())
                }
            }
        }
    }
}
