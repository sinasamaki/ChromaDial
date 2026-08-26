import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.DialInterval
import com.sinasamaki.chroma.dial.IntervalOrientation
import com.sinasamaki.chroma.dial.drawEveryInterval
import com.sinasamaki.chroma.dial.drawArc

/**
 * Demonstrates [IntervalOrientation.PositionOnly]: labels are placed at each interval position but
 * stay upright — even at the bottom of the dial, where the default [IntervalOrientation.PositionAndRotate]
 * would render them upside-down. The tick marks use the default orientation for contrast, so they
 * radiate along the arc while the numbers stay readable.
 */
@Composable
fun UprightLabelsDial() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            "Upright Labels",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )

        // startDegrees=0 (top), sweepDegrees=330 leaves a small gap and yields 12 labels (0..330 step 30)
        var degree by remember { mutableFloatStateOf(90f) }

        Dial(
            degree = degree,
            onDegreeChange = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = 0f,
            sweepDegrees = 330f,
            interval = 30f,
            thumb = {
                Box(
                    Modifier
                        .size(16.dp)
                        .drawBehind {
                            drawCircle(color = Neutral950)
                            drawCircle(color = Lime400, style = Stroke(width = 3.dp.toPx()))
                        }
                )
            },
            track = { state ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            // Background track
                            drawArc(
                                color = Zinc800,
                                startAngle = state.startDegrees,
                                sweepAngle = 330f,
                                radius = state.radius,
                                strokeWidth = 3.dp,
                                strokeCap = StrokeCap.Round,
                            )
                            // Active track up to the current degree
                            drawArc(
                                color = Lime500,
                                startAngle = state.startDegrees,
                                sweepAngle = state.degree,
                                radius = state.radius,
                                strokeWidth = 3.dp,
                                strokeCap = StrokeCap.Round,
                            )
                            // Tick marks: default PositionAndRotate — origin at each interval,
                            // rotated to the tangent, so +y radiates inward.
                            drawEveryInterval(
                                startDegrees = state.startDegrees,
                                sweepDegrees = 330f,
                                interval = 30f,
                                radius = state.radius - 12.dp.toPx(),
                                currentDegree = state.degree,
                            ) { data ->
                                drawLine(
                                    color = if (data.inActiveRange) Lime400 else Zinc600,
                                    start = Offset.Zero,
                                    end = Offset(0f, 8.dp.toPx()),  // +y = inward
                                    strokeWidth = 2.dp.toPx(),
                                    cap = StrokeCap.Round,
                                )
                            }
                        }
                )

                // Number labels: PositionOnly — placed at each interval but kept upright so they
                // stay readable all the way around, including the bottom of the dial.
                DialInterval(
                    state = state,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),
                    interval = 30f,
                    currentDegree = state.degree,
                    orientation = IntervalOrientation.PositionOnly,
                ) { data ->
                    Text(
                        text = "${data.index}",
                        color = if (data.inActiveRange) Lime400 else Zinc500,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                    )
                }
            },
        )
    }
}
