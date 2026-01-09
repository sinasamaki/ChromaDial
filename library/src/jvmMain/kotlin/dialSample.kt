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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.singleWindowApplication
import androidx.compose.ui.zIndex
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
                DialExample1()
            }
            item {
                DialExample2()
            }
            item {
                DialExample3()
            }
            item {
                DialExample4()
            }
            item {
                DialExample5()
            }
            item {
                DialExample6()
            }
            item {
                DialExample7()
            }
            item {
                DialExample8()
            }
            item {
                DialExample9()
            }
        }
    }

}

@Composable
fun DialExample1() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Full Circle",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        var degree by remember { mutableFloatStateOf(0f) }
        Dial(
            degree = degree,
            onDegreeChanged = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = 0f,
            sweepDegrees = 360f
        )
    }
}

@Composable
fun DialExample2() {
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
fun DialExample3() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Quarter Circle - Cyan",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        var degree by remember { mutableFloatStateOf(0f) }
        Dial(
            degree = degree,
            onDegreeChanged = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = 0f,
            sweepDegrees = 90f,
            thumb = {
                Box(
                    Modifier.size(48.dp)
                        .background(color = Cyan500, shape = CircleShape)
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawArc(
                                color = Cyan500.copy(alpha = .3f),
                                startAngle = 0f - 90f,
                                sweepAngle = 90f,
                                useCenter = false,
                                style = Stroke(width = 48.dp.toPx())
                            )
                        }
                )
            }
        )
    }
}

@Composable
fun DialExample4() {
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
fun DialExample5() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Gradient Track",
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
                Box(
                    Modifier.size(56.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Fuchsia500, Pink500)
                            ),
                            shape = CircleShape
                        )
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        Fuchsia500.copy(alpha = .3f),
                                        Cyan500.copy(alpha = .3f),
                                        Yellow400.copy(alpha = .3f),
                                        Fuchsia500.copy(alpha = .3f)
                                    )
                                ),
                                style = Stroke(width = 56.dp.toPx()),
                                radius = (size.width / 2) - 28.dp.toPx()
                            )
                        }
                )
            }
        )
    }
}

@Composable
fun DialExample6() {
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

        Text(
            text = "${(degree.absoluteValue / 6).toInt()} min",
            modifier = Modifier.padding(12.dp),
            fontSize = 28.sp,
            color = Zinc800,
            fontFamily = FontFamily.Monospace
        )

        Dial(
            degree = animatedDegree,
            onDegreeChanged = { degree = it },
            modifier = Modifier.size(300.dp),
            startDegrees = -360f * 1,
            sweepDegrees = 360f * 1,
            steps = 59 * 1,
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
                            drawArc(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Red600,
                                        Red500,
                                    )
                                ),
                                startAngle = -90f,
                                sweepAngle = it.degree,
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
fun DialExample7() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Large Square Thumb",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        var degree by remember { mutableFloatStateOf(180f) }
        Dial(
            degree = degree,
            onDegreeChanged = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = 0f,
            sweepDegrees = 360f,
            thumb = {
                Box(
                    Modifier.size(60.dp)
                        .background(
                            color = Orange500,
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                color = Orange500.copy(alpha = .2f),
                                style = Stroke(width = 60.dp.toPx()),
                                radius = (size.width / 2) - 30.dp.toPx()
                            )
                        }
                )
            }
        )
    }
}

@Composable
fun DialExample8() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "3/4 Arc - Purple",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        var degree by remember { mutableFloatStateOf(225f) }
        Dial(
            degree = degree,
            onDegreeChanged = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = 135f,
            sweepDegrees = 270f,
            steps = 5,
            thumb = {
                Box(
                    Modifier.size(44.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Purple400, Purple600)
                            ),
                            shape = CircleShape
                        )
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawArc(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        Purple400.copy(alpha = .3f),
                                        Purple600.copy(alpha = .3f),
                                        Purple400.copy(alpha = .3f)
                                    )
                                ),
                                startAngle = 135f - 90f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = 44.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                )
            }
        )
    }
}

@Composable
fun DialExample9() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Gradient Arc - Dark Theme",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        var degree by remember { mutableFloatStateOf(135f) }

        Box(
            modifier = Modifier
                .size(280.dp)
                .background(
                    color = Zinc900,
                    shape = RoundedCornerShape(48.dp)
                )
                .drawBehind {
                    // Subtle shadow effect
                    drawRoundRect(
                        color = Black.copy(alpha = 0.3f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(48.dp.toPx())
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(200.dp)) {
                Dial(
                    degree = degree,
                    onDegreeChanged = { degree = it },
                    modifier = Modifier.fillMaxSize(),
                    startDegrees = 135f,
                    sweepDegrees = 270f,
                    thumb = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
//                                .size(64.dp)
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            Box(
                                Modifier
                                    .zIndex(200f)
                                    .size(16.dp)
                                    .background(
                                        color = White,
                                        shape = CircleShape
                                    )
                            )
                        }
                    },
                    track = {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .blur(
                                    radius = 15.dp,
                                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                                )
                                .drawBehind {
                                    val strokeWidth = 20.dp.toPx()

                                    // Calculate active arc sweep
                                    val normalizedDegree = (degree - 135f).coerceIn(0f, 270f)

                                    // Active gradient arc
                                    if (normalizedDegree > 0) {
                                        drawArc(
                                            brush = Brush.sweepGradient(
                                                135f / 360f to Orange500,
                                                (135f + normalizedDegree * 0.5f) / 360f to Color(
                                                    0xFFFF8C42
                                                ),
                                                (135f + normalizedDegree) / 360f to Yellow400,
                                                (135f + normalizedDegree + 1f) / 360f to Transparent
                                            ),
                                            startAngle = 135f - 90f,
                                            sweepAngle = normalizedDegree,
                                            useCenter = false,
                                            style = Stroke(
                                                width = strokeWidth,
                                                cap = StrokeCap.Round
                                            )
                                        )
                                    }
                                }
                        )

                        Box(
                            Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .blur(
                                    radius = 1.dp,
                                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                                )
                                .drawBehind {
                                    val strokeWidth = 2.dp.toPx()

                                    // Calculate active arc sweep
                                    val normalizedDegree = (degree - 135f).coerceIn(0f, 270f)

                                    // Active gradient arc
                                    if (normalizedDegree > 0) {
                                        drawArc(
                                            brush = Brush.sweepGradient(
                                                135f / 360f to Orange400,
                                                (135f + normalizedDegree * 0.5f) / 360f to Color(
                                                    0xFFFFA76F
                                                ),
                                                (135f + normalizedDegree) / 360f to Yellow300,
                                                (135f + normalizedDegree + 1f) / 360f to Transparent
                                            ),
                                            startAngle = 135f - 90f,
                                            sweepAngle = normalizedDegree,
                                            useCenter = false,
                                            style = Stroke(
                                                width = strokeWidth,
                                                cap = StrokeCap.Round
                                            )
                                        )
                                    }
                                }
                        )


                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                                .background(
                                    color = Black,
                                    shape = CircleShape,
                                )
                        )
                    }
                )

                // "off" text indicator when at minimum
                if (degree <= 135f + 5f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            "off",
                            color = Gray600,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
            }
        }
    }
}