package com.sinasamaki.chroma.dial.sample

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.singleWindowApplication
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.DialInterval
import com.sinasamaki.chroma.dial.drawEveryInterval

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
            item {
                DefaultDial()
            }
            item {
                MinimalClock()
            }
            item {
                AmPmShadowClock()
            }
        }
    }

}

@Composable
fun DefaultDial() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Default",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        var degree by remember { mutableFloatStateOf(90f) }
        Dial(
            degree = degree,
            onDegreeChanged = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = 180f,
            sweepDegrees = 275f,
        )
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
        // With startDegrees=270, sweepDegrees=180, degree now goes from 0 to 180
        // Initial 90f is the midpoint (was 360f when using absolute degrees)
        var degree by remember { mutableFloatStateOf(90f) }
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
        // With startDegrees=-90, sweepDegrees=220, degree now goes from 0 to 220
        // Initial 90f corresponds to absolute 0 degrees (12 o'clock position)
        var degree by remember { mutableFloatStateOf(90f) }
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
            interval = 20f,
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
                            // interval = 4° to get ~46 positions over 180° range (same as steps=45)
                            drawEveryInterval(
                                degreeRange = -180f..0f,
                                interval = 4f,
                                radius = it.radius,
                            ) { data ->
                                rotate(
                                    data.degree,
                                    pivot = data.position
                                ) {
                                    drawLine(
                                        color = White,
                                        start = data.position,
                                        end = data.position + Offset(
                                            0f,
                                            if (data.degree in (-91f)..(-89f)) 30f else 10f
                                        )
                                    )
                                }
                            }
                        }
                )
                DialInterval(
                    modifier = Modifier
                        .graphicsLayer {
                            rotationZ = it.absoluteDegree
                        }
                        .fillMaxSize()
                        .padding(20.dp),
                    degreeRange = -220f..0f,
                    interval = 20f,  // 220 / 11 = 20° to get 12 positions (same as steps=10)
                ) { data ->
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                transformOrigin = TransformOrigin(
                                    0f, .5f
                                )
                            }
                            .background(
                                color = if (data.index == 4) Red500 else Transparent,
                            )
                            .border(
                                width = 1.dp,
                                color = when (data.index) {
                                    0 -> Green500
                                    4 -> Red400
                                    else -> White
                                },
                            )
                            .padding(2.dp)
                    ) {
                        when (data.index) {
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
                                text = "${data.index}",
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
            onDegreeChanged = { degree = it },
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
            // With relative degrees (0 to 180), degree = (proteinGrams / 300f) * 180f
            Dial(
                degree = (proteinGrams / 300f) * 180f,
                onDegreeChanged = {
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
                onDegreeChanged = {
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
                onDegreeChanged = {
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


@Composable
fun MinimalClock() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(
                color = White
            ),
        contentAlignment = Alignment.Center,
    ) {
        var minute by remember { mutableStateOf(0f) }
        Dial(
            degree = minute,
            onDegreeChanged = { minute = it },
            modifier = Modifier
                .size(300.dp),
            thumb = {
                Box(
                    Modifier.size(48.dp)
                )
            },
            track = { dialState ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .dropShadow(
                            shape = CircleShape
                        ) {
                            radius = 40f
                            alpha = .2f
                        }
                        .background(
                            color = Zinc100,
                            shape = CircleShape,
                        )
                        .drawWithContent {
                            val path = Path()
                            path.moveTo(center.x, center.y)
                            path.relativeLineTo(-60f, -center.y - 10f)
                            path.relativeLineTo(120f, 0f)
                            path.close()

                            val matrix = Matrix()
                            matrix.rotateZ(dialState.degree)
                            matrix.translate(-center.x, -center.y)
                            path.transform(matrix)
                            path.transform(Matrix().apply { translate(center.x, center.y) })

                            drawCircle(
                                color = White,
                                radius = center.x - 1.dp.toPx(),
                                style = Stroke(width = 2.dp.toPx())
                            )

                            clipPath(path = path) {
                                this@drawWithContent.drawContent()
                                drawCircle(
                                    color = Lime200,
                                    radius = center.x - 1.dp.toPx(),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                ) {
                    // interval = 30° for 13 positions (same as steps=11)
                    DialInterval(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Lime500,
                                shape = CircleShape,
                            ),
                        degreeRange = 0f..360f,
                        interval = 30f,
                    ) { data ->
                        val showNumber by remember {
                            derivedStateOf {
                                (dialState.degree < 180f && data.index != 12) ||
                                        (dialState.degree > 180f && data.index != 0)
                            }
                        }
                        if (showNumber)
                            Text(
                                text = "${data.index * 5}".padStart(2, '0'),
                                modifier = Modifier
                                    .rotate(-data.degree - 90f)
//                                    .padding(top = 12.dp)
                                    .padding(12.dp),
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Monospace,
                            )

                    }
                }
            }
        )


        var hour by remember { mutableStateOf(0f) }
        val animatedHour by animateFloatAsState(
            targetValue = hour
        )

        Dial(
            degree = animatedHour,
            onDegreeChanged = { hour = it },
            modifier = Modifier
                .size(200.dp),
            thumb = {
                Box(
                    Modifier.size(48.dp)
                )
            },
            interval = 30f,
            track = { dialState ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .dropShadow(
                            shape = CircleShape
                        ) {
                            radius = 20f
                            alpha = .2f
                        }
                        .background(
                            color = Zinc100,
                            shape = CircleShape,
                        )
                        .drawWithContent {
                            val path = Path()
                            path.moveTo(center.x, center.y)
                            path.relativeLineTo(-60f, -center.y - 10f)
                            path.relativeLineTo(120f, 0f)
                            path.close()

                            val matrix = Matrix()
                            matrix.rotateZ(dialState.degree)
                            matrix.translate(-center.x, -center.y)
                            path.transform(matrix)
                            path.transform(Matrix().apply { translate(center.x, center.y) })

                            drawCircle(
                                color = White,
                                radius = center.x - 1.dp.toPx(),
                                style = Stroke(width = 2.dp.toPx())
                            )

                            clipPath(path = path) {
                                this@drawWithContent.drawContent()
                                drawCircle(
                                    color = Lime200,
                                    radius = center.x - 1.dp.toPx(),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                ) {
                    // interval = 30° for 13 positions (same as steps=11)
                    DialInterval(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Lime500,
                                shape = CircleShape,
                            ),
                        degreeRange = 0f..360f,
                        interval = 30f,
                    ) { data ->
                        if (data.index != 0)
                            Text(
                                text = "${data.index}",
                                modifier = Modifier
                                    .rotate(-data.degree - 90f)
                                    .padding(12.dp),
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Monospace,
                            )

                    }
                }
            }
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = Zinc100, shape = CircleShape,
                )
        )

    }
}


@Composable
fun AmPmShadowClock() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(
                color = White
            ),
        contentAlignment = Alignment.Center,
    ) {

        Box(
            modifier = Modifier
                .size(250.dp)
                .drawBehind {
                    drawCircle(
                        color = Yellow400.copy(alpha = 1f),
                    )
                    drawCircle(
                        color = Yellow200,
                        radius = center.x - 1.dp.toPx(),
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
        )


        var amPm by remember { mutableStateOf(0f) }
        val animatedAmPm by animateFloatAsState(
            targetValue = amPm
        )

        val interaction = remember { MutableInteractionSource() }
        val isDragging by interaction.collectIsDraggedAsState()

        LaunchedEffect(isDragging) {
            if (!isDragging) {
                amPm = if (amPm < 180f) 90f else 270f
            }
        }

        Dial(
            degree = animatedAmPm,
            onDegreeChanged = { amPm = it },
            interactionSource = interaction,
            modifier = Modifier
                .size(400.dp)
                .pointerInput(Unit) {
                    detectTapGestures {
                        amPm = if (amPm > 180f) 90f else 270f
                    }
                },
            thumb = {
                Box(
                    Modifier
                        .offset(y = -10.dp)
                        .size(280.dp)
//                        .size(40.dp)
                        .border(
                            width = 4.dp,
                            shape = CircleShape,
                            color = Zinc600,
                        )
                        .background(
                            color = Zinc900,
                            shape = CircleShape,
                        )
                )
            },
            track = { dialState ->

            }
        )

        Box(
            modifier = Modifier
                .size(250.dp)
                .clip(CircleShape)
                .pointerInput(Unit) {}
        )

        Box(
            Modifier
                .size(400.dp)
                .padding(12.dp)
        ) {
            Text(
                text = "PM",
                modifier = Modifier
                    .align(Alignment.CenterStart),
                color = White,
                fontSize = 28.sp,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                text = "AM",
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                color = White,
                fontSize = 28.sp,
                fontFamily = FontFamily.Monospace,
            )
        }


        var minute by remember { mutableStateOf(0f) }
        Dial(
            degree = minute,
            onDegreeChanged = { minute = it },
            modifier = Modifier
                .size(250.dp),
            thumb = {
                Box(
                    Modifier.size(52.dp)
                )
            },
            track = { dialState ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
//                            drawCircle(
//                                color = Yellow400.copy(alpha = .2f),
//                            )
//                            drawCircle(
//                                color = Zinc200,
//                                radius = center.x - 1.dp.toPx(),
//                                style = Stroke(width = 2.dp.toPx())
//                            )
                            drawContent()
                            rotate(
                                degrees = minute
                            ) {
                                drawLine(
                                    color = Zinc200,
                                    start = center,
                                    strokeWidth = 10f,
                                    cap = StrokeCap.Round,
                                    end = Offset(center.x, 36.dp.toPx())
                                )
                            }
                        }
                )
            }
        )


        // interval = 30° for 13 positions (same as steps=11)
        DialInterval(
            modifier = Modifier
                .size(250.dp),
            degreeRange = 0f..360f,
            interval = 30f,
        ) { data ->
            if (data.index != 0)
                Text(
                    text = "${data.index}",
                    modifier = Modifier
                        .rotate(-data.degree - 90f)
//                                    .padding(top = 12.dp)
                        .padding(12.dp),
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Zinc200,
                    style = LocalTextStyle.current.copy(
                        shadow = Shadow(
                            color = Black,
                            offset = Offset(1f, 1f),
                            blurRadius = 10f
                        )
                    )
                )

        }



        var hour by remember { mutableStateOf(0f) }
        val animatedHour by animateFloatAsState(
            targetValue = hour
        )

        Dial(
            degree = animatedHour,
            onDegreeChanged = { hour = it },
            modifier = Modifier
                .size(200.dp),
            thumb = {
                Box(
                    Modifier.size(48.dp)
                )
            },
            interval = 30f,
            track = { dialState ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            rotate(
                                degrees = animatedHour
                            ) {
                                drawLine(
                                    color = Zinc200,
                                    start = center,
                                    strokeWidth = 10f,
                                    cap = StrokeCap.Round,
                                    end = Offset(center.x, 32.dp.toPx())
                                )
                            }
                        }
                ) {
                }
            }
        )
    }
}

