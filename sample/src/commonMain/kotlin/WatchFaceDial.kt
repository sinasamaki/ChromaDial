import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.drawEveryInterval
import kotlin.math.sqrt

@Composable
fun WatchFaceDial() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(color = Neutral950),
        contentAlignment = Alignment.Center,
    ) {
        var minute by remember { mutableStateOf(0f) }

        // Hour hand moves 1/12 as fast as the minute hand
        val hourDegree = minute / 12f

        Dial(
            degree = minute,
            onDegreeChange = { minute = it },
            sweepDegrees = 360f * 12,
            startDegrees = 0f,
            interval = 6f,
            modifier = Modifier.size(280.dp),
            thumb = {
                // Invisible thumb — just provides the draggable hit area
                Box(Modifier.size(56.dp))
            },
            track = { dialState ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            // Watch face background
                            drawCircle(color = Neutral950)

                            // Outer bezel ring
                            drawRoundRect(
                                color = Lime500,
                                style = Stroke(width = 0.dp.toPx()),
                                cornerRadius = CornerRadius(100f, 100f)
                            )
                            inset(10f) {
                                drawRoundRect(
                                    color = Lime500,
                                    style = Stroke(width = 0.dp.toPx()),
                                    cornerRadius = CornerRadius(90f, 90f)
                                )
                            }
                            clipPath(
                                path = Path().apply {
                                    addRoundRect(
                                        roundRect = RoundRect(
                                            rect = Rect(
                                                center = center,
                                                radius = center.x - 20f,
                                            ),
                                            radiusX = 80f,
                                            radiusY = 80f,
                                        )
                                    )
                                }
                            ) {

                                val tickOuterRadius = center.x - 10.dp.toPx()

                                // 60 tick marks (6° spacing across 354° to avoid duplicate at 360°)
                                drawEveryInterval(
                                    startDegrees = 0f,
                                    sweepDegrees = 359f,
                                    radius = tickOuterRadius,
                                    spacing = 3f,
                                ) { data ->
                                    val isHourMark = data.intervalDegree % 30f == 0f
                                    val tickLength = if (isHourMark) 100.dp.toPx() else 6.dp.toPx()
                                    val strokeWidth = if (isHourMark) 2.dp.toPx() else 1f.dp.toPx()
                                    val color = if (isHourMark) Lime500 else Lime800

                                    // Inward direction from the arc point toward center
                                    val dx = data.position.x - center.x
                                    val dy = data.position.y - center.y
                                    val dist = sqrt(dx * dx + dy * dy)
                                    val nx = dx / dist
                                    val ny = dy / dist

                                    rotate(
                                        degrees = data.rotationAngle,
                                        pivot = data.position,
                                    ) {
                                        drawLine(
                                            color = color,
                                            start = data.position + Offset(0f, 40f),
                                            end = data.position + Offset(0f, -200f),
                                            strokeWidth = strokeWidth,
                                        )
                                    }
                                }

                                inset(60f) {
                                    drawRoundRect(
                                        color = Neutral950,
//                                        style = Stroke(width = 0.dp.toPx()),
                                        cornerRadius = CornerRadius(140f, 140f)
                                    )
                                }
                            }

                            // Hour hand — shorter, thicker, derived from minute position
                            rotate(degrees = hourDegree) {
                                drawLine(
                                    color = Lime500,
                                    start = center,
                                    end = Offset(center.x, center.y - center.x * 0.48f),
                                    strokeWidth = 6.dp.toPx(),
                                    cap = StrokeCap.Round,
                                )
                            }

                            // Minute hand — longer, thinner, tracks the dial with overshoot
                            rotate(degrees = dialState.degree + dialState.overshootDegrees) {
                                drawLine(
                                    color = Lime400,
                                    start = center,
                                    end = Offset(center.x, center.y - center.x * 0.7f),
                                    strokeWidth = 3.dp.toPx(),
                                    cap = StrokeCap.Round,
                                )
                            }

                            // Center cap
                            drawCircle(
                                color = Lime300,
                                radius = 5.dp.toPx(),
                            )
                        }
                )
            },
        )
    }
}
