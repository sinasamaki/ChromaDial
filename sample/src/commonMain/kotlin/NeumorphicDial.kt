import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import kotlin.math.roundToInt

@Composable
fun NeumorphicDial() {
    Column(
        modifier = Modifier
            .height(400.dp)
            .background(
                color = Zinc200,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        Text(
            "Neumorphic",
            color = Zinc500,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )

        var degree by remember { mutableFloatStateOf(90f) }

        val surfaceColor = Zinc200
        val shadowDark = Zinc400
        val shadowLight = White
        val shadowOffset = 12f
        val shadowBlur = 20.dp
        val accentColor = Indigo500

        Box(
            modifier = Modifier.size(220.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Dark shadow (bottom-right)
            Box(
                Modifier
                    .matchParentSize()
                    .blur(shadowBlur, BlurredEdgeTreatment.Unbounded)
                    .drawBehind {
                        drawCircle(
                            color = shadowDark,
                            center = Offset(center.x + shadowOffset, center.y + shadowOffset),
                        )
                    }
            )
            // Light shadow (top-left)
            Box(
                Modifier
                    .matchParentSize()
                    .blur(shadowBlur, BlurredEdgeTreatment.Unbounded)
                    .drawBehind {
                        drawCircle(
                            color = shadowLight,
                            center = Offset(center.x - shadowOffset, center.y - shadowOffset),
                        )
                    }
            )
            // Surface
            Box(
                Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(surfaceColor),
                contentAlignment = Alignment.Center,
            ) {
                Dial(
                    degree = degree,
                    onDegreeChange = { degree = it },
                    modifier = Modifier.fillMaxSize(),
                    startDegrees = 225f,
                    sweepDegrees = 270f,
                    thumb = { _ ->
                        Box(
                            Modifier.size(36.dp),
                        )
                    },
                    track = { state ->
                        Box(
                            Modifier
                                .fillMaxSize()
                                .drawBehind {
                                    val strokeWidth = 7.dp.toPx()
                                    val trackRadius = state.radius - (36.dp.toPx() / 2)
                                    val sweepRange =
                                        state.degreeRange.endInclusive - state.degreeRange.start
                                    val overshoot = state.overshootDegrees
                                    val startAngle =
                                        state.startDegrees - 90f + minOf(0f, overshoot)
                                    val activeSweep =
                                        (state.degree - state.degreeRange.start).coerceAtLeast(1f) +
                                                kotlin.math.abs(overshoot)
                                    val insetOffset = 2.dp.toPx()
                                    val grooveStroke = strokeWidth - 4.dp.toPx()

                                    if (activeSweep > 0f) {
                                        drawArc(
                                            color = accentColor.copy(alpha = 0.85f),
                                            startAngle = startAngle,
                                            sweepAngle = activeSweep,
                                            topLeft = Offset(
                                                center.x - trackRadius,
                                                center.y - trackRadius,
                                            ),
                                            size = Size(trackRadius * 2, trackRadius * 2),
                                            useCenter = false,
                                            style = Stroke(
                                                width = grooveStroke,
                                                cap = StrokeCap.Round,
                                            ),
                                        )
                                    }
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "${(state.value * 100).roundToInt()}",
                                style = TextStyle(
                                    color = Zinc600,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                ),
                            )
                        }
                    },
                )
            }
        }
    }
}
