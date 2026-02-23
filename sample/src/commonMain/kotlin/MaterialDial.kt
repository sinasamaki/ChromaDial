import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial

@Composable
fun MaterialDial() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Material",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        var degree by remember { mutableFloatStateOf(90f) }
        Dial(
            degree = degree,
            onDegreeChange = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = 180f,
            sweepDegrees = 275f,
            thumb = { state ->
                Box(
                    Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val showThumb by remember {
                        derivedStateOf {
                            state.degree < state.degreeRange.endInclusive
                        }
                    }
                    val scale by animateFloatAsState(
                        targetValue = if (showThumb) 1f else 0f
                    )
                    Box(
                        Modifier
                            .offset(y = (-16).dp)
                            .width(6.dp)
                            .height(40.dp)
                            .graphicsLayer {
                                scaleY = scale
                            }
                            .background(
                                color = Lime400,
                                shape = CircleShape
                            )
                    )
                }
            },
            track = { state ->
                val trackWidth = 16.dp
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val strokeWidth = trackWidth.toPx()
                            val trackRadius = state.radius - strokeWidth / 2
                            val startAngle = state.startDegrees - 90f
                            val sweepRange =
                                state.degreeRange.endInclusive - state.degreeRange.start
                            val activeSweep = (state.degree - state.degreeRange.start) - 10f
                            val inactiveSweep = sweepRange - activeSweep - 20f
                            val inactiveStart = startAngle + activeSweep + 20f

                            // Draw inactive track (from current position to end)
                            if (inactiveSweep > 0f) {
                                drawArc(
                                    color = Violet500.copy(alpha = .2f),
                                    startAngle = inactiveStart,
                                    sweepAngle = inactiveSweep,
                                    topLeft = Offset(
                                        center.x - trackRadius,
                                        center.y - trackRadius
                                    ),
                                    size = Size(trackRadius * 2, trackRadius * 2),
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }

                            // Draw active track (from start to current position)
                            if (activeSweep > 0f) {
                                drawArc(
                                    color = Violet500,
                                    startAngle = startAngle,
                                    sweepAngle = activeSweep,
                                    topLeft = Offset(
                                        center.x - trackRadius,
                                        center.y - trackRadius
                                    ),
                                    size = Size(trackRadius * 2, trackRadius * 2),
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }

                            // Draw stop indicator at the end of the track
                            val endAngleRadians =
                                (startAngle + sweepRange) * kotlin.math.PI.toFloat() / 180f
                            val stopIndicatorX =
                                center.x + trackRadius * kotlin.math.cos(endAngleRadians)
                            val stopIndicatorY =
                                center.y + trackRadius * kotlin.math.sin(endAngleRadians)
                            drawCircle(
                                color = Lime300,
                                radius = 4.dp.toPx(),
                                center = Offset(stopIndicatorX, stopIndicatorY)
                            )
                        }
                )
            }
        )
    }
}
