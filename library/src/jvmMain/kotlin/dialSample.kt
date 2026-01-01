package com.sinasamaki.chroma.dial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.singleWindowApplication

fun main() = singleWindowApplication {

    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color(0xFF0A0A0A)),
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
            color = Color.White,
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
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        var degree by remember { mutableFloatStateOf(135f) }
        Dial(
            degree = degree,
            onDegreeChanged = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = 135f,
            sweepDegrees = 180f,
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawArc(
                                color = Color(0xFF3A3A3A),
                                startAngle = 135f - 90f,
                                sweepAngle = 180f,
                                useCenter = false,
                                style = Stroke(width = 32.dp.toPx(), cap = StrokeCap.Round)
                            )
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
            color = Color.White,
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
                        .background(color = Color.Cyan, shape = CircleShape)
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawArc(
                                color = Color.Cyan.copy(alpha = .3f),
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
            color = Color.White,
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
                        .background(color = Color.Yellow, shape = CircleShape)
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                color = Color.Yellow.copy(alpha = .2f),
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
            color = Color.White,
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
                                colors = listOf(Color.Magenta, Color(0xFFFF1493))
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
                                        Color.Magenta.copy(alpha = .3f),
                                        Color.Cyan.copy(alpha = .3f),
                                        Color.Yellow.copy(alpha = .3f),
                                        Color.Magenta.copy(alpha = .3f)
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
            color = Color.White,
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
                        .background(color = Color.Green, shape = CircleShape)
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                color = Color.Green.copy(alpha = .25f),
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
            color = Color.White,
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
                            color = Color(0xFFFF6B35),
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
                                color = Color(0xFFFF6B35).copy(alpha = .2f),
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
            color = Color.White,
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
                                colors = listOf(Color(0xFF9D4EDD), Color(0xFF7209B7))
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
                                        Color(0xFF9D4EDD).copy(alpha = .3f),
                                        Color(0xFF7209B7).copy(alpha = .3f),
                                        Color(0xFF9D4EDD).copy(alpha = .3f)
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