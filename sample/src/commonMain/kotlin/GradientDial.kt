import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import androidx.compose.ui.graphics.PathIterator
import androidx.compose.ui.graphics.PathSegment
import androidx.compose.ui.graphics.StrokeCap

@Composable
fun GradientDial() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Gradient",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        var degree by remember { mutableFloatStateOf(0f) }
        Dial(
            degree = degree,
            onDegreeChange = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = 0f,
            sweepDegrees = 360f,
            thumb = {
                Box(Modifier
                    .size(56.dp)
                )
            },
            track = { state ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationZ = state.degree - 90f
                        }
                        .drawBehind {
                            val strokeWidth = 16.dp.toPx()
                            val trackRadius = state.radius - strokeWidth

                            val colors = listOf(
                                Zinc950.copy(alpha = 1f),
                                Sky400,
                                Blue500,
                            )

                            drawArc(
                                brush = Brush.sweepGradient(
                                    colors = colors,
                                    center = center
                                ),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                topLeft = Offset(
                                    center.x - trackRadius,
                                    center.y - trackRadius
                                ),
                                size = Size(trackRadius * 2, trackRadius * 2),
                                useCenter = false,
                            )

                            val path = Path()

                            path.addArc(
                                oval = Rect(
                                    center, state.radius - (strokeWidth / 1),
                                ),
                                19f, 330f
                            )
                            drawPath(
                                path,
                                brush = Brush.sweepGradient(
                                    colors = colors,
                                    center = center
                                ),
                                style = Stroke(
                                    width = strokeWidth * 2,
                                    cap = StrokeCap.Round,
                                )
                            )
                        }
                )
            }
        )
    }
}
