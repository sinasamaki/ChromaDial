import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import com.sinasamaki.chroma.dial.createTubePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import icons.CoffeeMakerIcon
import kotlin.math.roundToInt

@Composable
fun CoffeeGrindSettingDial() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .background(White)
            .padding(vertical = 56.dp)
    ) {
        var degree by remember { mutableFloatStateOf(90f) }
        val interaction = remember { MutableInteractionSource() }
        val isDragged by interaction.collectIsDraggedAsState()
        val isHovered by interaction.collectIsHoveredAsState()
        val hoverScale by animateFloatAsState(
            targetValue = when {
                isDragged -> 1.1f
                isHovered -> 1.3f
                else -> .9f
            }
        )
        Dial(
            degree = degree,
            onDegreeChange = { degree = it },
            modifier = Modifier.size(230.dp),
            startDegrees = 225f,
            sweepDegrees = 270f,
            interactionSource = interaction,
            thumb = { state ->
                Box(
                    Modifier
                        .size(40.dp)
                        .drawBehind {
                            scale(
                                scale = hoverScale
                            ) {
                                drawCircle(
                                    color = Zinc900.copy(alpha = .3f),
                                )
                            }
                        }
                        .background(
                            color = White,
                            shape = CircleShape,
                        )
                        .padding(6.dp)
                        .background(
                            color = Black,
                            shape = CircleShape,
                        )
                )
            },
            track = { state ->
                val grind = (1 + state.value * 9).roundToInt()

                BoxWithConstraints {
                    val strokePx = 20.dp.toPx()
                    val center = Offset(this.maxWidth.toPx() / 2f, this.maxHeight.toPx() / 2f)
                    val startAngle = state.startDegrees - 90f
                    val activeSweep = (state.degree - state.degreeRange.start).coerceAtLeast(1f)

                    val path = remember(center, state.radius, strokePx, startAngle, activeSweep) {
                        createTubePath(
                            center = center,
                            radius = state.radius - strokePx,
                            startAngleDegrees = startAngle,
                            sweepAngleDegrees = activeSweep,
                            tubeRadius = strokePx / 2,
                            cornerRadius = strokePx * .5f,
                        )
                    }

                    val pathShape = remember(path, constraints) {
                        object : Shape {
                            override fun createOutline(
                                size: Size,
                                layoutDirection: LayoutDirection,
                                density: Density
                            ): Outline = Outline.Generic(path)
                        }
                    }

                    Box(
                        Modifier
                            .fillMaxSize()
                            .drawBehind {
                                drawPath(
                                    path = path,
                                    color = Zinc300,
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }
                            .clip(pathShape)
                            .blur(
                                radius = 6.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded,
                            )
                            .drawBehind {
                                drawPath(
                                    path = path,
                                    color = Black,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                    )

                    Box(
                        Modifier
                            .fillMaxSize()
                            .blur(
                                radius = 40.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded,
                            )
                            .alpha(.3f)
                            .drawBehind {
                                drawCircle(
                                    color = Black,
                                    radius = 24.dp.toPx(),
                                    center = polarOffset(
                                        center = this.center,
                                        radius = state.radius - strokePx,
                                        degrees = state.absoluteDegree - 90f
                                    )
                                )
                            }
                    )

                    Box(
                        Modifier
                            .fillMaxSize()
                            .blur(
                                radius = 60.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded,
                            )
                            .alpha(.3f)
                            .drawBehind {
                                inset(inset = strokePx) {
                                    drawArc(
                                        color = Black,
                                        startAngle = state.absoluteDegree - 90f,
                                        sweepAngle = -activeSweep * .4f,
                                        useCenter = false,
                                        style = Stroke(width = strokePx * 1.5f)
                                    )
                                }
                            }
                    )

                    Box(
                        Modifier
                            .fillMaxSize()
                            .clip(pathShape)
                            .blur(
                                radius = 30.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded,
                            )
                            .drawBehind {
                                inset(inset = strokePx) {
                                    drawArc(
                                        color = Black,
                                        startAngle = state.absoluteDegree - 90f,
                                        sweepAngle = -activeSweep * .2f,
                                        useCenter = false,
                                        style = Stroke(width = strokePx * 1.5f)
                                    )
                                }
                            }
                    )

                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "$grind",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            ),
                            color = Black,
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Grind setting",
                            modifier = Modifier
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Black.copy(alpha = 0f),
                                            Black.copy(alpha = .08f),
                                        )
                                    ),
                                    shape = CircleShape,
                                )
                                .border(
                                    width = 1.dp,
                                    shape = CircleShape,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Black.copy(alpha = .4f),
                                            Black.copy(alpha = .1f),
                                        )
                                    ),
                                )
                                .padding(vertical = 4.dp, horizontal = 10.dp),
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                            ),
                            color = Black,
                        )

                        Spacer(Modifier.height(10.dp))

                        Icon(
                            imageVector = CoffeeMakerIcon,
                            contentDescription = "Coffee maker",
                            tint = Black,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun androidx.compose.ui.unit.Dp.toPx(): Float {
    val density = LocalDensity.current
    return with(density) { this@toPx.toPx() }
}
