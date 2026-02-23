import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.DialInterval

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
            onDegreeChange = { amPm = it },
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
            onDegreeChange = { minute = it },
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
                        .fillMaxWidth()
                        .drawWithContent {
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
            onDegreeChange = { hour = it },
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
                        .fillMaxWidth()
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
