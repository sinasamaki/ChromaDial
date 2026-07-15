package com.sinasamaki.chroma.dial

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.ceil

private const val RING_ALPHA_DECAY = 0.4f
private const val RING_SCALE_INCREMENT = 0.15f

/**
 * Default style thumb for the Dial component.
 * A simple circle with stroke.
 */
@Composable
public fun DefaultDialThumb(state: DialState, colors: DialColors) {
    Box(
        Modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(16.dp)
                .drawBehind {
                    drawCircle(
                        color = colors.thumbStrokeColor,
                        style = Stroke(width = 4.dp.toPx())
                    )
                    drawCircle(
                        color = colors.thumbColor,
                        radius = size.minDimension / 2 - 2.dp.toPx()
                    )
                }
        )
    }
}

/**
 * Default track for the Dial component.
 * A simple arc track with active portion overlay and optional ticks.
 * Supports multi-ring display when sweep exceeds 360 degrees.
 */
@Composable
public fun DefaultDialTrack(state: DialState, colors: DialColors) {
    val trackWidth = 4.dp
    val sweepRange = state.degreeRange.endInclusive - state.degreeRange.start
    val totalSweep = state.degree - state.degreeRange.start

    val maxPossibleRings = maxOf(1, ceil(sweepRange / 360.0).toInt())
    val numActiveRings =
        maxOf(1, ceil(totalSweep.coerceAtLeast(0.001f) / 360.0).toInt())

    Box(Modifier.fillMaxSize()) {
        for (ringIndex in (maxPossibleRings - 1) downTo 0) {
            val ringStartSweep = ringIndex * 360f
            val ringMaxSweep = (sweepRange - ringStartSweep).coerceIn(0f, 360f)
            val ringSweep = (totalSweep - ringStartSweep).coerceIn(0f, ringMaxSweep)
            val isActiveRing = ringIndex < numActiveRings
            val ringsAbove = if (isActiveRing) (numActiveRings - 1 - ringIndex)
                .coerceAtLeast(0) else 0
            val targetScale = 1f + ringsAbove * RING_SCALE_INCREMENT
            val targetAlpha = (1f - ringsAbove * RING_ALPHA_DECAY).coerceAtLeast(0.01f)
            val isInnermostRing = ringIndex == numActiveRings - 1
            val targetStrokeMultiplier = if (isActiveRing) 1f else 0f

            key(ringIndex) {
                val scale by animateFloatAsState(
                    targetValue = targetScale,
                    animationSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                    )
                )
                val strokeMultiplier by animateFloatAsState(
                    targetValue = targetStrokeMultiplier,
                    animationSpec = spring(
                        stiffness = if (isActiveRing)
                            Spring.StiffnessLow
                        else
                            Spring.StiffnessHigh
                    )
                )
                val alpha by animateFloatAsState(
                    targetValue = targetAlpha,
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMedium,
                    )
                )

                if (strokeMultiplier > 0f || ringMaxSweep > 0f) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                // Scale rings around the dial's center (which may be off-center
                                // when a custom DialLayout.center is used) rather than the box center.
                                if (size.width > 0f && size.height > 0f) {
                                    transformOrigin = TransformOrigin(
                                        state.center.x / size.width,
                                        state.center.y / size.height,
                                    )
                                }
                            }
                            .drawBehind {
                                val effectiveStrokeWidth = trackWidth * strokeMultiplier
                                // Arc center radius: state.radius - 12dp. Library's drawArc insets
                                // by strokePx/2, so we pass center + strokePx/2 as outer radius.
                                val arcCenterRadius = state.radius - 12.dp.toPx()

                                if (ringMaxSweep > 0f) {
                                    drawArc(
                                        color = colors.inactiveTrackColor.copy(alpha = alpha),
                                        startAngle = state.startDegrees,
                                        sweepAngle = if (state.clockwise) ringMaxSweep else -ringMaxSweep,
                                        radius = arcCenterRadius,
                                        center = state.center,
                                        strokeWidth = effectiveStrokeWidth,
                                        strokeCap = StrokeCap.Round,
                                    )
                                }

                                val overshoot = if (isInnermostRing) state.overshootDegrees else 0f
                                val effectiveActiveStart: Float
                                val effectiveActiveSweep: Float
                                if (state.clockwise) {
                                    effectiveActiveStart = state.startDegrees + minOf(0f, overshoot)
                                    effectiveActiveSweep = ringSweep + abs(overshoot)
                                } else {
                                    effectiveActiveStart = state.startDegrees
                                    effectiveActiveSweep = -ringSweep + overshoot
                                }
                                if (abs(effectiveActiveSweep) > 0f && strokeMultiplier > 0f) {
                                    drawArc(
                                        color = colors.activeTrackColor.copy(alpha = alpha),
                                        startAngle = effectiveActiveStart,
                                        sweepAngle = effectiveActiveSweep,
                                        radius = arcCenterRadius,
                                        center = state.center,
                                        strokeWidth = effectiveStrokeWidth,
                                        strokeCap = StrokeCap.Round,
                                    )
                                }

                                if (state.interval > 0f && ringMaxSweep > 0f) {
                                    val currentDegreeForTicks = if (isActiveRing) ringSweep else 0f

                                    drawEveryInterval(
                                        startDegrees = state.startDegrees,
                                        sweepDegrees = if (state.clockwise) ringMaxSweep else -ringMaxSweep,
                                        radius = arcCenterRadius,
                                        center = state.center,
                                        interval = state.interval,
                                        currentDegree = currentDegreeForTicks,
                                    ) { data ->
                                        val tickColor = if (data.inActiveRange && isActiveRing) {
                                            colors.activeTickColor.copy(alpha = alpha)
                                        } else {
                                            colors.inactiveTickColor.copy(alpha = alpha)
                                        }
                                        rotate(
                                            degrees = data.rotationAngle,
                                            pivot = data.position
                                        ) {
                                            drawLine(
                                                color = tickColor,
                                                start = data.position - Offset(0f, 4.dp.toPx()),
                                                end = data.position + Offset(0f, 4.dp.toPx()),
                                                strokeWidth = 2.dp.toPx(),
                                                cap = StrokeCap.Round,
                                            )
                                        }
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}
