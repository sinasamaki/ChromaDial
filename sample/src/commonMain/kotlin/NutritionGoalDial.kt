import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial

@Composable
fun NutritionGoalDial() {
    Column(
        modifier = Modifier
            .background(
                color = lerp(Zinc950, Purple950, .2f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Set your daily intake goal",
            color = Purple400,
            fontSize = 18.sp,
            fontWeight = FontWeight.Thin,
            fontFamily = FontFamily.Monospace
        )

        var proteinGrams by remember { mutableFloatStateOf(150f) }
        var carbsGrams by remember { mutableFloatStateOf(200f) }
        var fatsGrams by remember { mutableFloatStateOf(75f) }

        // Dials container
        Box(
            modifier = Modifier
                .size(300.dp, 200.dp)
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Outermost dial - Protein (Blue)
            // With relative degrees (0 to 180), degree = (proteinGrams / 300f) * 180f
            Dial(
                degree = (proteinGrams / 300f) * 180f,
                onDegreeChange = {
                    proteinGrams = (it / 180f * 300f).coerceIn(0f, 300f)
                },
                modifier = Modifier.size(300.dp, 150.dp),
                startDegrees = 270f,
                sweepDegrees = 180f,
                thumb = {
                    Box(
                        Modifier
                            .size(16.dp)
                            .background(color = Blue500, shape = CircleShape)
                            .border(width = 3.dp, color = White, shape = CircleShape)
                    )
                },
                track = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .drawBehind {
                                val strokeWidth = 16.dp.toPx()

                                // Background track (lighter)
                                drawArc(
                                    color = Blue200.copy(alpha = 0.3f),
                                    startAngle = 270f - 90f,
                                    sweepAngle = 180f,
                                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                    size = Size(
                                        width = size.width - strokeWidth,
                                        height = (size.width - strokeWidth)
                                    ),
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )

                                // Active track (solid)
                                val activeSweep = (proteinGrams / 300f) * 180f
                                if (activeSweep > 0f) {
                                    drawArc(
                                        color = Blue400,
                                        startAngle = 270f - 90f,
                                        sweepAngle = activeSweep,
                                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                        size = Size(
                                            width = size.width - strokeWidth,
                                            height = (size.width - strokeWidth)
                                        ),
                                        useCenter = false,
                                        style = Stroke(
                                            width = strokeWidth + 10f,
                                            cap = StrokeCap.Round
                                        )
                                    )
                                    drawArc(
                                        brush = Brush.sweepGradient(
                                            colors = listOf(
                                                Blue200,
                                                Blue700,
                                            ),
                                            center = Offset(center.x, size.height)
                                        ),
                                        startAngle = 270f - 90f,
                                        sweepAngle = activeSweep,
                                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                        size = Size(
                                            width = size.width - strokeWidth,
                                            height = (size.width - strokeWidth)
                                        ),
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                            }
                    )
                }
            )

            // Middle dial - Carbs (Yellow)
            // With relative degrees (0 to 180), degree = (carbsGrams / 400f) * 180f
            Dial(
                degree = (carbsGrams / 400f) * 180f,
                onDegreeChange = {
                    carbsGrams = (it / 180f * 400f).coerceIn(0f, 400f)
                },
                modifier = Modifier.size(240.dp, 120.dp),
                startDegrees = 270f,
                sweepDegrees = 180f,
                thumb = {
                    Box(
                        Modifier
                            .size(16.dp)
                            .background(color = Yellow500, shape = CircleShape)
                            .border(width = 3.dp, color = White, shape = CircleShape)
                    )
                },
                track = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .drawBehind {
                                val strokeWidth = 16.dp.toPx()

                                // Background track (lighter)
                                drawArc(
                                    color = Yellow200.copy(alpha = 0.3f),
                                    startAngle = 270f - 90f,
                                    sweepAngle = 180f,
                                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                    size = Size(
                                        width = size.width - strokeWidth,
                                        height = (size.width - strokeWidth)
                                    ),
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )

                                // Active track (solid)
                                val activeSweep = (carbsGrams / 400f) * 180f
                                if (activeSweep > 0f) {
                                    drawArc(
                                        color = Yellow500,
                                        startAngle = 270f - 90f,
                                        sweepAngle = activeSweep,
                                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                        size = Size(
                                            width = size.width - strokeWidth,
                                            height = (size.width - strokeWidth)
                                        ),
                                        useCenter = false,
                                        style = Stroke(
                                            width = strokeWidth + 10f,
                                            cap = StrokeCap.Round
                                        )
                                    )
                                    drawArc(
                                        brush = Brush.sweepGradient(
                                            colors = listOf(
                                                Yellow50,
                                                Yellow600,
                                            ),
                                            center = Offset(center.x, size.height)
                                        ),
                                        startAngle = 270f - 90f,
                                        sweepAngle = activeSweep,
                                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                        size = Size(
                                            width = size.width - strokeWidth,
                                            height = (size.width - strokeWidth)
                                        ),
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                            }
                    )
                }
            )

            // Innermost dial - Fats (Red)
            // With relative degrees (0 to 180), degree = (fatsGrams / 150f) * 180f
            Dial(
                degree = (fatsGrams / 150f) * 180f,
                onDegreeChange = {
                    fatsGrams = (it / 180f * 150f).coerceIn(0f, 150f)
                },
                modifier = Modifier.size(180.dp, 90.dp),
                startDegrees = 270f,
                sweepDegrees = 180f,
                thumb = {
                    Box(
                        Modifier
                            .size(16.dp)
                            .background(color = Red500, shape = CircleShape)
                            .border(width = 3.dp, color = White, shape = CircleShape)
                    )
                },
                track = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .drawBehind {
                                val strokeWidth = 16.dp.toPx()

                                // Background track (lighter)
                                drawArc(
                                    color = Red200.copy(alpha = 0.3f),
                                    startAngle = 270f - 90f,
                                    sweepAngle = 180f,
                                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                    size = Size(
                                        width = size.width - strokeWidth,
                                        height = (size.width - strokeWidth)
                                    ),
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )

                                // Active track (solid)
                                val activeSweep = (fatsGrams / 150f) * 180f
                                if (activeSweep > 0f) {
                                    drawArc(
                                        color = Red400,
                                        startAngle = 270f - 90f,
                                        sweepAngle = activeSweep,
                                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                        size = Size(
                                            width = size.width - strokeWidth,
                                            height = (size.width - strokeWidth)
                                        ),
                                        useCenter = false,
                                        style = Stroke(
                                            width = strokeWidth + 10f,
                                            cap = StrokeCap.Round
                                        )
                                    )
                                    drawArc(
                                        brush = Brush.sweepGradient(
                                            colors = listOf(
                                                Red200,
                                                Red700,
                                            ),
                                            center = Offset(center.x, size.height)
                                        ),
                                        startAngle = 270f - 90f,
                                        sweepAngle = activeSweep,
                                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                        size = Size(
                                            width = size.width - strokeWidth,
                                            height = (size.width - strokeWidth)
                                        ),
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                            }
                    )
                }
            )


            // Goal values display
            Column(
                modifier = Modifier
                    .offset(y = (28).dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.Bottom)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "protein",
                        color = Blue500,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(145.dp)
                    )
                    Text(
                        text = ":",
                        color = Blue500,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = " ${proteinGrams.toInt()}g",
                        color = Blue500,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "carbs",
                        color = Yellow600,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(145.dp)
                    )
                    Text(
                        text = ":",
                        color = Yellow500,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = " ${carbsGrams.toInt()}g",
                        color = Yellow500,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "fat",
                        color = Red600,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(145.dp)
                    )
                    Text(
                        text = ":",
                        color = Red500,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = " ${fatsGrams.toInt()}g",
                        color = Red500,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

        }

    }
}
