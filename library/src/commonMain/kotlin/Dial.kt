package com.sinasamaki.chroma.dial

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
//import theme.Sky500
//import theme.Zinc200
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

val Sky500 = Color.Blue
val Zinc200 = Color.White

@Stable
class DialState(
    initialDegree: Float,
    val degreeRange: ClosedFloatingPointRange<Float>,
    val steps: Int = 0,
    var onValueChangeFinished: (() -> Unit)? = null
) {
    private var degreeState by mutableFloatStateOf(initialDegree)
    private var overshotAngleState by mutableFloatStateOf(0f)

    init {
        require(steps >= 0) { "steps must be >= 0" }
    }

    fun calculateSnappedValue(value: Float): Float {
        if (steps == 0) return value.coerceIn(degreeRange)

        val range = degreeRange.endInclusive - degreeRange.start
        val stepSize = range / (steps + 1)
        val snappedSteps = ((value - degreeRange.start) / stepSize).roundToInt()
        return (degreeRange.start + snappedSteps * stepSize).coerceIn(degreeRange)
    }

    var degree: Float
        set(newVal) {
            degreeState = newVal
        }
        get() = degreeState

    val value: Float
        get() {
            val range = degreeRange.endInclusive - degreeRange.start
            return if (range == 0f) 0f else (degree - degreeRange.start) / range
        }

    var overshotAngle: Float
        set(newVal) {
            overshotAngleState = newVal
        }
        get() = overshotAngleState

    var onValueChange: (Float) -> Unit = {}
}

@Composable
fun Dial(
    degree: Float,
    onDegreeChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    startDegrees: Float,
    sweepDegrees: Float,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    steps: Int = 0,
    thumb: @Composable (DialState) -> Unit = {
        Box(
            Modifier.size(48.dp).background(color = Sky500, shape = CircleShape)
        )
    },
    track: @Composable (DialState) -> Unit = {
        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        color = Zinc200.copy(alpha = .2f),
                        style = Stroke(width = 48.dp.toPx()),
                        radius = (size.width / 2) - 24.dp.toPx()
                    )
                }
        )
    }
) {
    val degreeRange = startDegrees..(startDegrees + sweepDegrees)
    Dial(
        degree = degree,
        onDegreeChanged = onDegreeChanged,
        modifier = modifier,
        degreeRange = degreeRange,
        onValueChangeFinished = onValueChangeFinished,
        interactionSource = interactionSource,
        steps = steps,
        thumb = thumb,
        track = track
    )
}

@Composable
fun Dial(
    degree: Float,
    onDegreeChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    degreeRange: ClosedFloatingPointRange<Float> = 0f..360f,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    steps: Int = 0,
    thumb: @Composable (DialState) -> Unit = {
        Box(
            Modifier.size(48.dp).background(color = Sky500, shape = CircleShape)
        )
    },
    track: @Composable (DialState) -> Unit = {
        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        color = Zinc200.copy(alpha = .2f),
                        style = Stroke(width = 48.dp.toPx()),
                        radius = (size.width / 2) - 24.dp.toPx()
                    )
                }
        )
    }
) {
    val state = remember(degreeRange, steps) {
        DialState(degree, degreeRange, steps, onValueChangeFinished)
    }
    state.onValueChangeFinished = onValueChangeFinished
    state.onValueChange = onDegreeChanged
    state.degree = degree

    Dial(
        state = state,
        modifier = modifier,
        interactionSource = interactionSource,
        thumb = thumb,
        track = track
    )
}

@Composable
private fun Dial(
    state: DialState,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource,
    thumb: @Composable (DialState) -> Unit,
    track: @Composable (DialState) -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = modifier
//            .clip(CircleShape)
    ) {
        // Calculate transform origin for thumb rotation
//        val dialSize = constraints.maxWidth.toFloat()
        val dialSize by remember {
            derivedStateOf {
                Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
            }
        }

        val thumbSize = with(density) { 48.dp.toPx() }
        val transformOriginY = (dialSize.width / 2f) / thumbSize

        var thumbPosition by remember { mutableStateOf(Offset.Zero) }


        var dragTracker by remember { mutableStateOf(Offset.Zero) }
        var draggingAngle by remember { mutableStateOf(state.degree.coerceIn(state.degreeRange)) }
        var currentDragInteraction by remember { mutableStateOf<DragInteraction.Start?>(null) }

        Box(Modifier.matchParentSize()) {
            track(state)
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.TopCenter)
                .hoverable(interactionSource)
                .graphicsLayer {
                    rotationZ = state.degree
                    transformOrigin = TransformOrigin(0.5f, transformOriginY)
                },
            content = { thumb(state) }
        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    // Calculate position based on touchAngle instead of rotating
                    val angleInRadians = (state.degree - 90f) * PI.toFloat() / 180f
                    val radius =
                        dialSize.width / 2f - thumbSize / 2f // Offset halfway inwards by thumb size
                    val centerX = dialSize.width / 2f
                    val centerY = dialSize.height / 2f

                    // Calculate target position on the circle
                    val targetX = centerX + radius * kotlin.math.cos(angleInRadians)
                    val targetY = centerY + radius * kotlin.math.sin(angleInRadians)

                    // Current position is TopCenter, so thumb center is at (centerX, thumbSize/2)
                    val currentX = dialSize.width / 2f
                    val currentY = thumbSize / 2f

                    // Translate to the target position
                    translationX = targetX - currentX
                    translationY = targetY - currentY
                }
                .onGloballyPositioned {
                    thumbPosition = it.positionInParent()
                }
                .pointerInput(state.degreeRange) {
                    val centerPx = Offset(dialSize.width / 2f, dialSize.height / 2f)

                    fun calculateAngle(offset: Offset): Float {
                        val dx = offset.x - centerPx.x
                        val dy = offset.y - centerPx.y
                        val radians = atan2(dy, dx)
                        val degrees = radians * 180f / PI.toFloat()
                        return degrees
                    }

                    var previousAngle by mutableStateOf(0f)
                    var dragOffset by mutableStateOf(Offset.Zero)
                    var dragInteraction: Interaction? = null
                    detectDragGestures(
                        onDragStart = { offset ->
                            dragOffset = offset + thumbPosition
                            previousAngle = calculateAngle(dragOffset)
                            val interaction = DragInteraction.Start()
                            dragInteraction = interaction
                            scope.launch {
                                interactionSource.emit(interaction)
                            }
                        },
                        onDrag = { change, _ ->
                            dragOffset += change.positionChange()
                            dragTracker = dragOffset
                            val dragAngle = calculateAngle(dragOffset)
                            println("drag = $dragOffset \t | ${change.position}")
                            var delta = dragAngle - previousAngle

                            // Handle wrap-around at 0/360 degrees for delta calculation
                            if (delta > 180f) delta -= 360f
                            if (delta < -180f) delta += 360f

                            // Calculate what the new angle would be
                            draggingAngle += delta
                            val targetAngle = state.degree + delta

                            // Only update if target is within range
                            if (draggingAngle in state.degreeRange) {
//                                state.degree = draggingAngle
                                state.overshotAngle = 0f
                                state.onValueChange(state.calculateSnappedValue(draggingAngle))
                            } else {
                                // Clamp to range but don't update previousAngle
                                // This keeps the finger "tracking" so it can come back
                                val clampedAngle = draggingAngle.coerceIn(state.degreeRange)

                                // Calculate overshot amount
                                if (draggingAngle < state.degreeRange.start) {
                                    state.overshotAngle = draggingAngle - state.degreeRange.start
                                } else if (draggingAngle > state.degreeRange.endInclusive) {
                                    state.overshotAngle =
                                        draggingAngle - state.degreeRange.endInclusive
                                }

//                                state.degree = clampedAngle
                                state.onValueChange(state.calculateSnappedValue(clampedAngle))
                            }
                            previousAngle = dragAngle
                        },
                        onDragEnd = {
                            dragTracker = Offset.Zero
                            draggingAngle = state.degree
                            state.overshotAngle = 0f
                            state.onValueChangeFinished?.invoke()
                            (dragInteraction as? DragInteraction.Start)?.let {
                                scope.launch {
                                    interactionSource.emit(
                                        DragInteraction.Stop(
                                            it
                                        )
                                    )
                                }
                            }
                        }
                    )
                },
            content = { }
        )
    }
}