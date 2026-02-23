import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.DialInterval
import com.sinasamaki.chroma.dial.drawEveryInterval

@Composable
fun TimerDial() {
    Column(
        modifier = Modifier
            .background(
                color = Zinc50,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "60 min timer",
            color = Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        // With startDegrees=-1440, sweepDegrees=1440, degree now goes from 0 to 1440
        // Initial 1440f is at the end of range (0 minutes), was 0f when using absolute degrees
        val sweepDegrees = 360f * 4
        var degree by remember { mutableFloatStateOf(sweepDegrees) }
        val animatedDegree by animateFloatAsState(
            targetValue = degree,
            animationSpec = spring(
                stiffness = Spring.StiffnessHigh,
            )
        )

        // Minutes calculation: at degree=sweepDegrees (1440), minutes=0; at degree=0, minutes=240
        val totalMinutes = ((sweepDegrees - degree) / 6).toInt()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        Text(
            text = if (totalMinutes < 60) {
                "$totalMinutes min"
            } else {
                val hourText = if (hours == 1) "hr" else "hrs"
                "$hours $hourText $minutes min"
            },
            modifier = Modifier.padding(12.dp),
            fontSize = 28.sp,
            color = Zinc800,
            fontFamily = FontFamily.Monospace
        )

        Dial(
            degree = animatedDegree,
            onDegreeChange = { degree = it },
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            startDegrees = -360f * 4,
            sweepDegrees = 360f * 4,
            interval = 6f,
            thumb = {
                Box(
                    Modifier.fillMaxSize()
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val strokeWidth = 120.dp
                            drawCircle(
                                color = Zinc400.copy(alpha = .01f),
                                style = Stroke(width = strokeWidth.toPx()),
                                radius = (size.width / 2) - (strokeWidth / 2).toPx()
                            )
                            val rect = Rect(
                                center = center,
                                radius = (size.width / 2) - (strokeWidth / 2).toPx(),
                            )

                            // Calculate elapsed degrees from the end of range
                            val elapsedDegrees = sweepDegrees - it.degree
                            val numRings = (elapsedDegrees / 360).toInt()
                            val baseRadius = (size.width / 2)
                            val ringSpacing = 3.dp.toPx()

                            for (i in 0 until numRings) {
                                drawCircle(
                                    color = Red500,
                                    radius = baseRadius + (i + 1) * ringSpacing,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                            drawArc(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Red600,
                                        Red500,
                                    )
                                ),
                                startAngle = -90f,
                                // Sweep counter-clockwise (negative) based on elapsed degrees
                                sweepAngle = -(elapsedDegrees % 360f),
                                topLeft = rect.topLeft,
                                size = rect.size,
                                useCenter = false,
                                style = Stroke(
                                    width = strokeWidth.toPx(),
                                )

                            )
                            rotate(
                                degrees = it.absoluteDegree
                            ) {
                                // interval = 30° for 13 positions around full circle (same as steps=11)
                                drawEveryInterval(
                                    degreeRange = 0f..360f,
                                    radius = it.radius,
                                    padding = 25.dp,
                                    interval = 30f,
                                ) { data ->
                                    rotate(
                                        degrees = data.degree,
                                        pivot = data.position
                                    ) {
                                        drawLine(
                                            color = Zinc800,
                                            start = data.position,
                                            end = data.position + Offset(0f, 20f),
                                            strokeWidth = 5f,
                                            cap = StrokeCap.Round,
                                        )
                                    }
                                }
                                // interval = 6° for 61 positions (same as steps=59)
                                drawEveryInterval(
                                    degreeRange = 0f..360f,
                                    radius = it.radius,
                                    padding = 25.dp,
                                    interval = 6f,
                                ) { data ->
                                    rotate(
                                        degrees = data.degree,
                                        pivot = data.position
                                    ) {
                                        drawLine(
                                            color = Zinc800,
                                            start = data.position,
                                            end = data.position + Offset(0f, 10f),
                                            strokeWidth = 3f,
                                        )
                                    }
                                }

                            }

                            if (degree == 0f) {
                                drawCircle(
                                    color = Red500,
                                    radius = 8.dp.toPx()
                                )
                                drawLine(
                                    color = Red500,
                                    start = Offset(center.x, 45.dp.toPx()),
                                    end = center,
                                    strokeWidth = 4.dp.toPx(),
                                    cap = StrokeCap.Round,
                                )
                            }
                        }
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        AnimatedVisibility(
                            visible = degree != 0f,
                            enter = scaleIn(),
                            exit = scaleOut(),
                        ) {
                            Image(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(White),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .size(48.dp)
                                    .background(
                                        color = Zinc900,
                                        shape = CircleShape,
                                    )
                            )
                        }
                    }

                }
                // interval = 30° for 13 positions (same as steps=11)
                DialInterval(
                    modifier = Modifier
                        .graphicsLayer {
                            rotationZ = it.absoluteDegree
                        }
                        .fillMaxSize(),
                    degreeRange = 0f..360f,
                    radius = it.radius,
                    interval = 30f,
                ) { data ->
                    if (data.index < 12) {
                        Text(
                            text = "${data.index * 5}",
                            modifier = Modifier
                                .rotate(-90f)
                                .padding(horizontal = 4.dp),
                            color = Zinc950,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
        )
    }
}
