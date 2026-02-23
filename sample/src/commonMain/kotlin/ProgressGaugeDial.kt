import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.drawEveryInterval

@Composable
fun ProgressGaugeDial() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Semi-Circle Arc",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
        )
        // With startDegrees=270, sweepDegrees=180, degree now goes from 0 to 180
        // Initial 90f is the midpoint (was 360f when using absolute degrees)
        var degree by remember { mutableFloatStateOf(90f) }
        Dial(
            degree = degree,
            onDegreeChange = { degree = it },
            modifier = Modifier
                .size(
                    200.dp,
                    100.dp,
                ),
            startDegrees = 270f,
            sweepDegrees = 180f,
            thumb = {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Zinc950,
                                    Green950,
                                )
                            ),
                            shape = CircleShape,
                        )
                        .border(
                            width = 1.dp,
                            shape = CircleShape,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Green400,
                                    Green800,
                                )
                            )
                        )
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            // interval = 180 / 51 ≈ 3.53° to get ~52 positions (same as steps=50)
                            drawEveryInterval(
                                dialState = it,
                                interval = 180f / 51f,
                                padding = (16).dp,
                            ) { data ->
                                drawCircle(
                                    color = Zinc800,
                                    radius = 2f,
                                    center = data.position,
                                )

                                rotate(
                                    degrees = data.degree,
                                    pivot = data.position,
                                ) {
                                    drawLine(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                if (data.inActiveRange) Yellow200 else Zinc700,
                                                if (data.inActiveRange) Green500 else Zinc800,
                                                if (data.inActiveRange) Yellow200 else Zinc700,
                                            ),
                                            startY = data.position.y + 20f,
                                            endY = data.position.y - 20f,
                                        ),
                                        start = data.position + Offset(0f, 20f),
                                        end = data.position - Offset(0f, 20f),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }
                            }
                        }
                ) {
                    Text(
                        text = "${(it.value * 100).toInt()}",
                        modifier = Modifier.align(Alignment.BottomCenter),
                        color = Green400,
                        fontSize = 32.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        )
    }
}
