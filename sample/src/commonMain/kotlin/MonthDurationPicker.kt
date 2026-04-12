import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.TubeShape
import com.sinasamaki.chroma.dial.drawEveryInterval
import com.sinasamaki.chroma.dial.drawTubePath
import kotlin.math.roundToInt

@Composable
fun MonthDurationPicker() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Zinc200, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {

        val swatch = Red
        var degree by remember { mutableStateOf(90f) }
        val animatedDegree by animateFloatAsState(targetValue = degree)
        Dial(
            degree = animatedDegree,
            onDegreeChange = { degree = it },
            sweepDegrees = 330f,
            startDegrees = 30f,
            interval = 30f,
            modifier = Modifier.size(280.dp),
            valueRange = 1f..12f,
            thumb = {
                Box(
                    Modifier
                        .size(56.dp)
                        .padding(6.dp)
                        .graphicsLayer {
                            rotationZ = -it.absoluteDegree
                        }
                        .dropShadow(
                            shape = CircleShape
                        ) {
                            radius = 10f
                            alpha = .4f
                        }
                        .border(
                            width = 3.dp,
                            shape = CircleShape,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Zinc50,
                                    Zinc400,
                                )
                            )
                        )
                        .background(Zinc200, CircleShape)
                )
            },
            track = { dialState ->
                BoxWithConstraints {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .dropShadow(shape = CircleShape) {
                                radius = 7f
                                alpha = .15f
                                offset = Offset(0f, -8f)
                                spread = 4f
                            }
                            .background(
                                shape = CircleShape,
                                color = Zinc100
                            )
                            .drawBehind {
                                val ringStroke = 56.dp.toPx()
                                val ringRadius = center.x - (ringStroke / 2)

                                drawCircle(
                                    color = Zinc300,
                                    radius = ringRadius,
                                    style = Stroke(width = ringStroke),
                                )

                                drawEveryInterval(
                                    startDegrees = 0f,
                                    sweepDegrees = 330f,
                                    radius = ringRadius,
                                    spacing = 30f,
                                ) { data ->
                                    drawCircle(
                                        color = Neutral500,
                                        radius = 3.dp.toPx(),
                                        center = data.position,
                                    )
                                }
                            },
                    )

                    val tubeRadius = with(LocalDensity.current) { 56.dp.toPx() / 2 }
                    val tubeCornerRadius = RoundedCornerShape(
                        topStart = 20f,
                        bottomStart = 20f,
                        topEnd = tubeRadius,
                        bottomEnd = tubeRadius,
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                TubeShape(
                                    startAngleDegrees = 0f,
                                    sweepAngleDegrees = 360f,
                                    tubeRadius = tubeRadius,
                                    cornerRadius = tubeCornerRadius,
                                )
                            )
                            .blur(
                                radius = 5.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded,
                            )
                            .drawBehind {
                                drawTubePath(
                                    startAngleDegrees = 0f,
                                    sweepAngleDegrees = 360f,
                                    tubeRadius = tubeRadius,
                                    cornerRadius = tubeCornerRadius,
                                    color = Neutral400,
                                    style = Stroke(width = 5.dp.toPx()),
                                )
                            },
                    )


                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                TubeShape(
                                    startAngleDegrees = 0f,
                                    sweepAngleDegrees = 360f,
                                    tubeRadius = tubeRadius,
                                    cornerRadius = tubeCornerRadius,
                                )
                            )
                            .blur(
                                radius = 30.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded,
                            )
                            .drawBehind {
                                drawTubePath(
                                    startAngleDegrees = 0f,
                                    sweepAngleDegrees = 30f + dialState.degree + dialState.overshootDegrees,
                                    tubeRadius = tubeRadius,
                                    cornerRadius = tubeCornerRadius,
                                    color = swatch.v500,
                                )
                            },
                    )


                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawBehind {
                                drawTubePath(
                                    startAngleDegrees = 0f,
                                    sweepAngleDegrees = 30f + dialState.degree + dialState.overshootDegrees,
                                    tubeRadius = tubeRadius,
                                    cornerRadius = tubeCornerRadius,
                                    color = swatch.v500,
                                )
                                drawTubePath(
                                    startAngleDegrees = 0f,
                                    sweepAngleDegrees = 30f + dialState.degree + dialState.overshootDegrees,
                                    tubeRadius = tubeRadius,
                                    cornerRadius = tubeCornerRadius,
                                    brush = Brush.radialGradient(
                                        .7f to (swatch + 1).v600,
                                        1f to Transparent,
                                    ),
                                )
                            },
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                TubeShape(
                                    startAngleDegrees = 0f,
                                    sweepAngleDegrees = 30f + dialState.degree + dialState.overshootDegrees,
                                    tubeRadius = tubeRadius,
                                    cornerRadius = tubeCornerRadius,
                                )
                            )
                            .blur(
                                radius = 20.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded,
                            )
                            .drawBehind {
                                drawTubePath(
                                    startAngleDegrees = 0f,
                                    sweepAngleDegrees = 30f + dialState.degree + dialState.overshootDegrees,
                                    tubeRadius = tubeRadius,
                                    cornerRadius = tubeCornerRadius,
                                    color = swatch.v300.copy(alpha = .2f),
                                )
                            },
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                TubeShape(
                                    startAngleDegrees = 0f,
                                    sweepAngleDegrees = 30f + dialState.degree + dialState.overshootDegrees,
                                    tubeRadius = tubeRadius,
                                    cornerRadius = tubeCornerRadius,
                                )
                            )
                            .blur(
                                radius = 4.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded,
                            )
                            .offset(
                                x = (-3).dp,
                                y = (-3).dp,
                            )
                            .drawBehind {
                                drawTubePath(
                                    startAngleDegrees = 0f,
                                    sweepAngleDegrees = 30f + dialState.degree + dialState.overshootDegrees,
                                    tubeRadius = tubeRadius,
                                    cornerRadius = tubeCornerRadius,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Transparent,
                                            Zinc100,
                                        )
                                    ),
                                    style = Stroke(width = 5f),
                                )
                            },
                    )




                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(56.dp)
                            .background(color = Zinc200, shape = CircleShape)
                            .innerShadow(shape = CircleShape) {
                                radius = 6f
                                offset = Offset(0f, -6f)
                                alpha = .2f
                                spread = 5f
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            0.dp,
                            alignment = Alignment.CenterVertically,
                        ),
                    ) {
                        val monthIndex = dialState.mappedValue.roundToInt()
                        AnimatedContent(
                            targetState = monthIndex,
                            modifier = Modifier.fillMaxWidth(),
                            transitionSpec = {
                                slideInVertically(
                                    initialOffsetY = {
                                        (
                                                if (targetState > initialState)
                                                    it * .05f
                                                else
                                                    -it * .05f
                                                ).roundToInt()
                                    }
                                ) togetherWith fadeOut(snap())
                            }
                        ) {
                            Text(
                                text = "$it",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = Neutral950,
                                fontSize = 72.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            text = if (monthIndex == 1) "month" else "months",
                            color = Zinc600,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            },
        )
    }
}
