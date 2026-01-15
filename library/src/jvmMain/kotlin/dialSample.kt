package com.sinasamaki.chroma.dial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.ModeNight
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.singleWindowApplication
import kotlin.math.absoluteValue

fun main() = singleWindowApplication {

    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Neutral950),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ProgressGaugeDial()
            }
            item {
                CameraModeDial()
            }
            item {
                TimerDial()
            }
            item {
                AngleIndicatorDial()
            }
            item {
                NutritionGoalDial()
            }
        }
    }

}


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
        var degree by remember { mutableFloatStateOf(360f) }
        Dial(
            degree = degree,
            onDegreeChanged = { degree = it },
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
//                            drawArc(
//                                color = Zinc700,
//                                startAngle = it.degreeRange.start - 90f,
//                                sweepAngle = it.degreeRange.endInclusive - it.degreeRange.start,
//                                topLeft = Offset(
//                                    32.dp.toPx() / 2,
//                                    32.dp.toPx() / 2,
//                                ),
//                                size = Size(
//                                    width = (it.radius * 2) - 32.dp.toPx(),
//                                    height = (it.radius * 2) - 32.dp.toPx(),
//                                ),
//                                useCenter = false,
//                                style = Stroke(width = 32.dp.toPx(), cap = StrokeCap.Butt)
//                            )
//                            drawArc(
//                                color = Fuchsia500.copy(alpha = .6f),
//                                startAngle = it.degreeRange.start - 90f,
//                                sweepAngle = it.degree - it.degreeRange.start,
//                                topLeft = Offset(
//                                    32.dp.toPx() / 2,
//                                    32.dp.toPx() / 2,
//                                ),
//                                size = Size(
//                                    width = (it.radius * 2) - 32.dp.toPx(),
//                                    height = (it.radius * 2) - 32.dp.toPx(),
//                                ),
//                                useCenter = false,
//                                style = Stroke(
//                                    width = 32.dp.toPx(),
//                                    cap = StrokeCap.Butt,
//                                )
//                            )

                            drawEveryStep(
                                dialState = it,
                                steps = 50,
                                padding = (16).dp,
                            ) { position, degrees, inActiveRange ->
                                drawCircle(
                                    color = Zinc800,
                                    radius = 2f,
                                    center = position,
                                )

                                rotate(
                                    degrees = degrees,
                                    pivot = position,
                                ) {
                                    drawLine(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                if (inActiveRange) Yellow200 else Zinc700,
                                                if (inActiveRange) Green500 else Zinc800,
                                                if (inActiveRange) Yellow200 else Zinc700,
                                            ),
                                            startY = position.y + 20f,
                                            endY = position.y - 20f,
                                        ),
                                        start = position + Offset(0f, 20f),
                                        end = position - Offset(0f, 20f),
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

@Composable
fun CameraModeDial() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Camera Dial",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        var degree by remember { mutableFloatStateOf(0f) }
        val animatedDegree by animateFloatAsState(
            targetValue = degree,
            animationSpec = spring(
                stiffness = Spring.StiffnessHigh,
                dampingRatio = Spring.DampingRatioLowBouncy,
            )
        )
        Dial(
            degree = animatedDegree,
            onDegreeChanged = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = -90f,
            sweepDegrees = 220f,
            steps = 10,
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
//                            drawCircle(
//                                color = Yellow400.copy(alpha = .2f),
//                                style = Stroke(width = 40.dp.toPx()),
//                                radius = (size.width / 2) - 20.dp.toPx()
//                            )
                            drawEveryStep(
                                degreeRange = -180f..0f,
                                steps = 45,
                                radius = it.radius,
                            ) { position, degrees, _ ->
                                rotate(
                                    degrees,
                                    pivot = position
                                ) {
                                    drawLine(
                                        color = White,
                                        start = position,
                                        end = position + Offset(
                                            0f,
                                            if (degrees in (-91f)..(-89f)) 30f else 10f
                                        )
                                    )
                                }
                            }

//                            rotate(
//                                it.degree
//                            ) {
//                                inset(60f) {
//                                    drawEveryStep(
//                                        degreeRange = -180f..0f,
//                                        steps = 10,
//                                        radius = it.radius - 60f,
//                                    ) { position, degrees, _ ->
//                                        rotate(
//                                            degrees,
//                                            pivot = position
//                                        ) {
//                                            drawCircle(
//                                                color = White,
//                                                radius = 10f,
//                                                center = position,
//                                            )
//                                        }
//                                    }
//                                }
//                            }
                        }
                )
                StepContent(
                    modifier = Modifier
                        .graphicsLayer {
                            rotationZ = it.degree
                        }
                        .fillMaxSize()
                        .padding(20.dp),
                    degreeRange = -220f..0f,
//                    radius = it.radius,
                    steps = 10,
                ) { index, position, degree, _ ->
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                transformOrigin = TransformOrigin(
                                    0f, .5f
                                )
//                                rotationZ = degree
                            }
                            .background(
                                color = if (index == 4) Red500 else Transparent,
                            )
                            .border(
                                width = 1.dp,
                                color = when (index) {
                                    0 -> Green500
                                    4 -> Red400
                                    else -> White
                                },
//                                shape = CircleShape,
                            )
                            .padding(2.dp)
                    ) {
                        when (index) {
                            0 -> Text(
                                text = "Auto",
                                fontSize = 8.sp,
                                color = Green500,
                                fontFamily = FontFamily.Monospace,
                            )

                            1 -> Text(
                                text = "P",
                                fontSize = 10.sp,
                                color = White,
                                fontFamily = FontFamily.Monospace,
                            )

                            2 -> Text(
                                text = "A",
                                fontSize = 10.sp,
                                color = White,
                                fontFamily = FontFamily.Monospace,
                            )

                            3 -> Text(
                                text = "S",
                                fontSize = 10.sp,
                                color = White,
                                fontFamily = FontFamily.Monospace,
                            )

                            4 -> Text(
                                text = "M",
                                fontSize = 10.sp,
                                color = Black,
                                fontFamily = FontFamily.Monospace,
                            )

                            5 -> Icon(
                                imageVector = Icons.Default.Api,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(12.dp)
                            )

                            6 -> Icon(
                                imageVector = Icons.Default.BrowseGallery,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(12.dp)
                            )

                            7 -> Text(
                                text = "B",
                                fontSize = 10.sp,
                                color = White,
                                fontFamily = FontFamily.Monospace,
                            )

                            8 -> Text(
                                text = "C",
                                fontSize = 10.sp,
                                color = White,
                                fontFamily = FontFamily.Monospace,
                            )

                            9 -> Icon(
                                imageVector = Icons.Default.ModeNight,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(12.dp)
                            )

                            10 -> Text(
                                text = "Video",
                                fontSize = 8.sp,
                                color = White,
                                fontFamily = FontFamily.Monospace,
                            )

                            11 -> Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(12.dp)
                            )

                            else -> Text(
                                text = "$index",
                                fontSize = 10.sp,
                                color = White,
                                fontFamily = FontFamily.Monospace,
                            )
                        }
                    }
                }
            }
        )
    }
}

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
        var degree by remember { mutableFloatStateOf(0f) }
        val animatedDegree by animateFloatAsState(
            targetValue = degree,
            animationSpec = spring(
                stiffness = Spring.StiffnessHigh,
            )
        )

        val totalMinutes = (degree.absoluteValue / 6).toInt()
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
            onDegreeChanged = { degree = it },
            modifier = Modifier.size(300.dp),
            startDegrees = -360f * 4,
            sweepDegrees = 360f * 4,
            steps = (60 * 4) - 1,
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

                            val numRings = (it.degree.absoluteValue / 360).toInt()
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
                                sweepAngle = it.degree % 360f,
                                topLeft = rect.topLeft,
                                size = rect.size,
                                useCenter = false,
                                style = Stroke(
                                    width = strokeWidth.toPx(),
                                )

                            )
                            rotate(
                                degrees = it.degree
                            ) {
                                drawEveryStep(
                                    degreeRange = 0f..360f,
                                    radius = size.width * .5f,
                                    padding = 25.dp,
                                    steps = 11,
                                ) { position, degree, _ ->
                                    rotate(
                                        degrees = degree,
                                        pivot = position
                                    ) {
                                        drawLine(
                                            color = Zinc800,
                                            start = position,
                                            end = position + Offset(0f, 20f),
                                            strokeWidth = 5f,
                                            cap = StrokeCap.Round,
                                        )
                                    }
                                }
                                drawEveryStep(
                                    degreeRange = 0f..360f,
                                    radius = size.width * .5f,
                                    padding = 25.dp,
                                    steps = 59,
                                ) { position, degree, _ ->
                                    rotate(
                                        degrees = degree,
                                        pivot = position
                                    ) {
                                        drawLine(
                                            color = Zinc800,
                                            start = position,
                                            end = position + Offset(0f, 10f),
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
                StepContent(
                    modifier = Modifier
                        .graphicsLayer {
                            rotationZ = it.degree
                        }
                        .fillMaxSize(),
                    degreeRange = 0f..360f,
                    steps = 11,
                ) { index, position, degree, _ ->
                    if (index < 12) {
                        Text(
                            text = "${index * 5}",
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
            onDegreeChanged = { degree = it },
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
            Dial(
                degree = (proteinGrams / 300f) * 180f + 270f,
                onDegreeChanged = {
                    proteinGrams = ((it - 270f) / 180f * 300f).coerceIn(0f, 300f)
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
            Dial(
                degree = (carbsGrams / 400f) * 180f + 270f,
                onDegreeChanged = {
                    carbsGrams = ((it - 270f) / 180f * 400f).coerceIn(0f, 400f)
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
                                        color = Yellow400,
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
            Dial(
                degree = (fatsGrams / 150f) * 180f + 270f,
                onDegreeChanged = {
                    fatsGrams = ((it - 270f) / 180f * 150f).coerceIn(0f, 150f)
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