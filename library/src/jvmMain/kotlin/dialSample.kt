package com.sinasamaki.chroma.dial

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
import androidx.compose.material.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.singleWindowApplication
import androidx.compose.ui.zIndex
import kotlin.math.PI
import kotlin.math.atan2

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
            fontWeight = FontWeight.Medium
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
                            color = Fuchsia500,
                            shape = CircleShape,
                        )
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val path = Path().apply {
                                addArc(
                                    oval = Rect(
                                        offset = Offset(
                                            32.dp.toPx() / 2,
                                            32.dp.toPx() / 2,
                                        ),
                                        size = Size(
                                            width = (it.radius * 2) - 32.dp.toPx(),
                                            height = (it.radius * 2) - 32.dp.toPx(),
                                        )
                                    ),
                                    startAngleDegrees = it.degreeRange.start - 90f,
                                    sweepAngleDegrees = it.degreeRange.endInclusive - it.degreeRange.start,
                                )
                            }
                            val measure = PathMeasure().apply {
                                setPath(path, false)
                            }
                            drawArc(
                                color = Zinc700,
                                startAngle = it.degreeRange.start - 90f,
                                sweepAngle = it.degreeRange.endInclusive - it.degreeRange.start,
                                topLeft = Offset(
                                    32.dp.toPx() / 2,
                                    32.dp.toPx() / 2,
                                ),
                                size = Size(
                                    width = (it.radius * 2) - 32.dp.toPx(),
                                    height = (it.radius * 2) - 32.dp.toPx(),
                                ),
                                useCenter = false,
                                style = Stroke(width = 32.dp.toPx(), cap = StrokeCap.Butt)
                            )
                            drawArc(
                                color = Fuchsia500.copy(alpha = .6f),
                                startAngle = it.degreeRange.start - 90f,
                                sweepAngle = it.degree - it.degreeRange.start,
                                topLeft = Offset(
                                    32.dp.toPx() / 2,
                                    32.dp.toPx() / 2,
                                ),
                                size = Size(
                                    width = (it.radius * 2) - 32.dp.toPx(),
                                    height = (it.radius * 2) - 32.dp.toPx(),
                                ),
                                useCenter = false,
                                style = Stroke(
                                    width = 32.dp.toPx(),
                                    cap = StrokeCap.Butt,
                                )
                            )

                            drawPath(
                                path = path,
                                color = Green500,
                                style = Stroke(width = 1f)
                            )

                            for (i in 0..80) {
                                val progress = i / 80f
                                val distance = progress * measure.length
                                val pos = measure.getPosition(distance)
                                val tangent = measure.getTangent(distance)
                                val degrees = atan2(tangent.y, tangent.x) * 180f / PI.toFloat()

                                drawCircle(
                                    color = Green500,
                                    radius = 1f,
                                    center = pos,
                                )

                                rotate(
                                    degrees = degrees,
                                    pivot = pos,
                                ) {
                                    drawLine(
                                        color = Green500,
                                        start = pos + Offset(0f, 20f),
                                        end = pos - Offset(0f, 20f)
                                    )
                                }
                            }

                        }
                )
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
            "With 8 Steps",
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
            sweepDegrees = 360f,
            steps = 8,
            thumb = {
                Box(
                    Modifier.size(40.dp)
                        .background(color = Yellow400, shape = CircleShape)
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                color = Yellow400.copy(alpha = .2f),
                                style = Stroke(width = 40.dp.toPx()),
                                radius = (size.width / 2) - 20.dp.toPx()
                            )
                        }
                )
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Thick Track - Green",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        var degree by remember { mutableFloatStateOf(270f) }
        Dial(
            degree = degree,
            onDegreeChanged = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = 0f,
            sweepDegrees = 360f,
            thumb = {
                Box(
                    Modifier.size(24.dp)
                        .background(color = Green500, shape = CircleShape)
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                color = Green500.copy(alpha = .25f),
                                style = Stroke(width = 70.dp.toPx()),
                                radius = (size.width / 2) - 35.dp.toPx()
                            )
                        }
                )
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
                            .fillMaxSize()
                            .drawBehind {
                                val textSize = 24.sp.toPx()
                            },
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