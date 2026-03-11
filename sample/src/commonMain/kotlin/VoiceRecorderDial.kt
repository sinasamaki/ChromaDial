import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.DialState
import com.sinasamaki.chroma.dial.drawEveryInterval
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val WAVEFORM_SECONDS = 240f
private const val WAVEFORM_SAMPLES_PER_SEC = 20

@Composable
fun VoiceRecorderDial() {
    var degree by remember { mutableFloatStateOf(0f) }
    val waveformData = remember { generateWaveformData() }

    val totalSeconds = degree / 6f
    val displayHours = (totalSeconds / 3600).toInt()
    val displayMinutes = ((totalSeconds % 3600) / 60).toInt()
    val displaySecs = (totalSeconds % 60).toInt()

    Column(
        modifier = Modifier
            .background(color = Zinc200, shape = RoundedCornerShape(8.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        RecordingTimestamp(hours = displayHours, minutes = displayMinutes, seconds = displaySecs)

        Dial(
            degree = degree,
            onDegreeChange = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = 0f,
            sweepDegrees = 360f * 4f,
            interval = 0f,
            thumb = { Box(Modifier.fillMaxSize()) },
            track = { state -> RecordingDialTrack(state) },
        )

        RecordingWaveform(
            waveformData = waveformData,
            degreeProvider = { degree },
        )
    }
}

@Composable
private fun RecordingTimestamp(hours: Int, minutes: Int, seconds: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PulseIndicator()
        Text(
            text = "%02d:%02d:%02d".format(hours, minutes, seconds),
            fontFamily = FontFamily.Monospace,
            fontSize = 18.sp,
            color = Zinc800,
        )
    }
}

@Composable
private fun PulseIndicator() {
    val transition = rememberInfiniteTransition(label = "recordingPulse")
    val pulseProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0f at 0 using LinearEasing
                1f at 300 using LinearEasing
                1f at 1000
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulseProgress",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .graphicsLayer {
                    scaleX = 1f + pulseProgress * 2.5f
                    scaleY = 1f + pulseProgress * 2.5f
                    alpha = 1f - pulseProgress
                }
                .background(Red500, CircleShape),
        )
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(Red500, CircleShape),
        )
    }
}

@Composable
private fun RecordingDialTrack(state: DialState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val strokePx = 2.dp.toPx()
                val outerRadius = state.radius - strokePx / 2f

                drawCircle(
                    color = Orange500,
                    radius = outerRadius,
                    style = Stroke(width = strokePx),
                )

                val markerLen = 10.dp.toPx()
                val markerSpacing = 4.dp.toPx()
                val markerY = center.y - outerRadius
                val markerStroke = 2.dp.toPx()
                listOf(-markerSpacing / 2f, markerSpacing / 2f).forEach { xOffset ->
                    drawLine(
                        color = Orange600,
                        start = Offset(center.x + xOffset, markerY - markerLen / 2f),
                        end = Offset(center.x + xOffset, markerY + markerLen / 2f),
                        strokeWidth = markerStroke,
                        cap = StrokeCap.Round,
                    )
                }

                val handDegree = state.degree % 360f
                val handRad = (handDegree - 90f) * (PI / 180f)
                val handCos = cos(handRad).toFloat()
                val handSin = sin(handRad).toFloat()
                val centerRingRadius = 16.dp.toPx()
                val handReach = outerRadius * 0.62f
                val handStroke = 1.5.dp.toPx()

                drawLine(
                    color = Orange500,
                    start = Offset(
                        center.x + centerRingRadius * handCos,
                        center.y + centerRingRadius * handSin
                    ),
                    end = Offset(center.x + handReach * handCos, center.y + handReach * handSin),
                    strokeWidth = handStroke,
                    cap = StrokeCap.Round,
                )

                drawLine(
                    color = Orange500,
                    start = Offset(
                        center.x - centerRingRadius * handCos,
                        center.y - centerRingRadius * handSin
                    ),
                    end = Offset(center.x - handReach * handCos, center.y - handReach * handSin),
                    strokeWidth = handStroke,
                    cap = StrokeCap.Round,
                )

                rotate(
                    state.degree + state.overshootDegrees
                ) {
                    drawEveryInterval(
                        sweepDegrees = 360f,
                        radius = state.radius,
                        interval = 5f,
                        padding = 10.dp
                    ) { interval ->
                        translate(
                            left = interval.position.x,
                            top = interval.position.y,
                        ) {
                            rotate(
                                degrees = interval.rotationAngle,
                                pivot = Offset(0f, 0f)
                            ) {
                                drawLine(
                                    color = Orange400.copy(alpha = .4f),
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, 15f),
                                    strokeWidth = 2f,
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }

                    drawEveryInterval(
                        sweepDegrees = 360f,
                        radius = state.radius,
                        interval = 10f,
                        padding = 25.dp
                    ) { interval ->
                        translate(
                            left = interval.position.x,
                            top = interval.position.y,
                        ) {
                            rotate(
                                degrees = interval.rotationAngle,
                                pivot = Offset(0f, 0f)
                            ) {
                                drawLine(
                                    color = Orange400.copy(alpha = .4f),
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, 10f),
                                    strokeWidth = 2f,
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }

                }

                drawCircle(
                    color = Orange500,
                    radius = 16.dp.toPx(),
                    style = Stroke(width = strokePx),
                )

                drawCircle(
                    color = Orange500,
                    radius = 9.dp.toPx(),
                )

                drawCircle(
                    color = Orange300,
                    radius = 9.dp.toPx(),
                    style = Stroke(width = strokePx),
                )
            },
    )
}

@Composable
private fun RecordingWaveform(waveformData: List<Float>, degreeProvider: () -> Float) {
    val totalSamples = waveformData.size

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .drawBehind {
                val progress = degreeProvider() / (6f * WAVEFORM_SECONDS)
                val barWidth = 3.dp.toPx()
                val barGap = 1.5.dp.toPx()
                val barStride = barWidth + barGap
                val halfBars = (size.width / barStride / 2).toInt() + 1
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val maxBarHeight = size.height * 0.85f

                val sampleFloat = (progress * totalSamples).coerceIn(0f, totalSamples - 1f)
                val sampleInt = sampleFloat.toInt()
                val subPixelOffset = (sampleFloat - sampleInt) * barStride

                for (offset in -halfBars..halfBars) {
                    val sampleIndex = (sampleInt + offset).coerceIn(0, totalSamples - 1)
                    val barX = centerX + offset * barStride - subPixelOffset
                    if (barX < -barWidth || barX > size.width + barWidth) continue

                    val barHeight = waveformData[sampleIndex] * maxBarHeight
                    val isActive = sampleIndex <= sampleInt

                    drawRoundRect(
                        color = if (isActive) Orange500 else Zinc400,
                        topLeft = Offset(barX - barWidth / 2f, centerY - barHeight / 2f),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(1.dp.toPx()),
                    )
                }

                drawLine(
                    color = Zinc800,
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, size.height),
                    strokeWidth = 1.dp.toPx(),
                )

                val triSize = 5.dp.toPx()
                val triPath = Path().apply {
                    moveTo(centerX, triSize * 1.5f)
                    lineTo(centerX - triSize, 0f)
                    lineTo(centerX + triSize, 0f)
                    close()
                }
                drawPath(triPath, color = Zinc800)
            },
    )
}

private fun generateWaveformData(): List<Float> {
    val random = Random(1337)
    val totalSamples = (WAVEFORM_SECONDS * WAVEFORM_SAMPLES_PER_SEC).toInt()
    return List(totalSamples) {
        val base = 0.15f + random.nextFloat() * 0.5f
        val spike = if (random.nextFloat() > 0.88f) random.nextFloat() * 0.35f else 0f
        (base + spike).coerceIn(0.05f, 1f)
    }
}
