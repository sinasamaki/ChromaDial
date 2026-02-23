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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial

@Composable
fun AngleIndicatorDial() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Angle Indicator",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        var degree by remember { mutableFloatStateOf(45f) }

        Dial(
            degree = degree,
            onDegreeChange = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = 0f,
            sweepDegrees = 360f,
            thumb = {
                Box(Modifier.fillMaxSize())
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val radius = it.radius
                            val centerPoint = center

                            // Draw arc at shorter radius (0 to current degree) with fill
                            val arcRadius = radius * 0.3f
                            drawArc(
                                color = Blue500.copy(alpha = 0.2f),
                                startAngle = -90f,
                                sweepAngle = it.degree,
                                topLeft = Offset(
                                    centerPoint.x - arcRadius,
                                    centerPoint.y - arcRadius
                                ),
                                size = androidx.compose.ui.geometry.Size(
                                    arcRadius * 2,
                                    arcRadius * 2
                                ),
                                useCenter = true,
                                style = androidx.compose.ui.graphics.drawscope.Fill
                            )

                            // Draw arc stroke
                            drawArc(
                                color = Blue500,
                                startAngle = -90f,
                                sweepAngle = it.degree,
                                topLeft = Offset(
                                    centerPoint.x - arcRadius,
                                    centerPoint.y - arcRadius
                                ),
                                size = androidx.compose.ui.geometry.Size(
                                    arcRadius * 2,
                                    arcRadius * 2
                                ),
                                useCenter = false,
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // Line at 0 degrees (pointing up)
                            rotate(degrees = 0f, pivot = centerPoint) {
                                drawLine(
                                    color = Orange500,
                                    start = centerPoint,
                                    end = centerPoint + Offset(0f, -radius),
                                    strokeWidth = 3.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }

                            // Line at current degrees
                            rotate(degrees = it.degree, pivot = centerPoint) {
                                drawLine(
                                    color = Orange500,
                                    start = centerPoint,
                                    end = centerPoint + Offset(0f, -radius),
                                    strokeWidth = 3.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                )

                // Position text at midpoint angle or after current degree if too low
                val textAngle = if (it.degree < 30f) {
                    it.degree + 15f
                } else {
                    it.degree / 2f
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationZ = textAngle
                        }
                        .padding(30.dp)
                ) {
                    Text(
                        text = "${it.degree.toInt()}°",
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .rotate(-textAngle)
                    )
                }
            }
        )
    }
}
