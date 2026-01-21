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
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt


public enum class RadiusMode {
    WIDTH,
    HEIGHT
}

@Stable
public class DialState(
    initialDegree: Float,
    public val degreeRange: ClosedFloatingPointRange<Float>,
    public val steps: Int = 0,
    public val radiusMode: RadiusMode = RadiusMode.WIDTH,
    public var onValueChangeFinished: (() -> Unit)? = null,
    public val startDegrees: Float = 0f
) {
    private var degreeState by mutableFloatStateOf(initialDegree)
    private var overshotAngleState by mutableFloatStateOf(0f)
    private var radiusState by mutableFloatStateOf(0f)
    private var thumbSizeState by mutableFloatStateOf(0f)

    init {
        require(steps >= 0) { "steps must be >= 0" }
    }

    /**
     * The absolute degree for rendering purposes.
     * This is the actual angle on the screen (startDegrees + relative degree).
     */
    public val absoluteDegree: Float
        get() = startDegrees + degree

    public var radius: Float
        internal set(value) {
            radiusState = value
        }
        get() = radiusState

    public var thumbSize: Float
        internal set(value) {
            thumbSizeState = value
        }
        get() = thumbSizeState

    public fun calculateSnappedValue(value: Float): Float {
        if (steps == 0) return value.coerceIn(degreeRange)

        val range = degreeRange.endInclusive - degreeRange.start
        val stepSize = range / (steps + 1)
        val snappedSteps = ((value - degreeRange.start) / stepSize).roundToInt()
        return (degreeRange.start + snappedSteps * stepSize).coerceIn(degreeRange)
    }

    public var degree: Float
        set(newVal) {
            degreeState = newVal
        }
        get() = degreeState

    public val value: Float
        get() {
            val range = degreeRange.endInclusive - degreeRange.start
            return if (range == 0f) 0f else (degree - degreeRange.start) / range
        }

    public var overshotAngle: Float
        set(newVal) {
            overshotAngleState = newVal
        }
        get() = overshotAngleState

    public var onValueChange: (Float) -> Unit = {}
}

@Composable
public fun Dial(
    degree: Float,
    onDegreeChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    startDegrees: Float,
    sweepDegrees: Float,
    radiusMode: RadiusMode = RadiusMode.WIDTH,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    steps: Int = 0,
    thumb: @Composable (DialState) -> Unit = {
        Box(
            Modifier.size(48.dp).background(color = Sky500, shape = CircleShape)
        )
    },
    track: @Composable (DialState) -> Unit = { state ->
        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        color = Zinc200.copy(alpha = .2f),
                        style = Stroke(width = 48.dp.toPx()),
                        radius = state.radius - 24.dp.toPx()
                    )
                }
        )
    }
) {
    // degreeRange is now relative: 0 to sweepDegrees
    val degreeRange = 0f..sweepDegrees
    Dial(
        degree = degree,
        onDegreeChanged = onDegreeChanged,
        modifier = modifier,
        degreeRange = degreeRange,
        startDegrees = startDegrees,
        radiusMode = radiusMode,
        onValueChangeFinished = onValueChangeFinished,
        interactionSource = interactionSource,
        steps = steps,
        thumb = thumb,
        track = track
    )
}

@Composable
public fun Dial(
    degree: Float,
    onDegreeChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    degreeRange: ClosedFloatingPointRange<Float> = 0f..360f,
    startDegrees: Float = 0f,
    radiusMode: RadiusMode = RadiusMode.WIDTH,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    steps: Int = 0,
    thumb: @Composable (DialState) -> Unit = {
        Box(
            Modifier.size(48.dp).background(color = Sky500, shape = CircleShape)
        )
    },
    track: @Composable (DialState) -> Unit = { state ->
        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        color = Zinc200.copy(alpha = .2f),
                        style = Stroke(width = 48.dp.toPx()),
                        radius = state.radius - 24.dp.toPx()
                    )
                }
        )
    }
) {
    val state = remember(degreeRange, steps, radiusMode, startDegrees) {
        DialState(degree, degreeRange, steps, radiusMode, onValueChangeFinished, startDegrees)
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
        val dialSize by remember(constraints) {
            derivedStateOf {
                Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
            }
        }

        // Calculate radius based on radiusMode and set it on state
        state.radius = when (state.radiusMode) {
            RadiusMode.WIDTH -> dialSize.width / 2f
            RadiusMode.HEIGHT -> dialSize.height / 2f
        }

        val transformOriginY = if (state.thumbSize > 0f) state.radius / state.thumbSize else 0f

        var thumbPosition by remember { mutableStateOf(Offset.Zero) }


        var dragTracker by remember { mutableStateOf(Offset.Zero) }
        var draggingAngle by remember { mutableStateOf(state.degree.coerceIn(state.degreeRange)) }
        var currentDragInteraction by remember { mutableStateOf<DragInteraction.Start?>(null) }

        Box(Modifier.matchParentSize()) {
            track(state)
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onGloballyPositioned { coordinates ->
                    // Measure the thumb size and store it in state
                    state.thumbSize = coordinates.size.width.toFloat()
                }
                .hoverable(interactionSource)
                .graphicsLayer {
                    rotationZ = state.absoluteDegree
                    transformOrigin = TransformOrigin(0.5f, transformOriginY)
                },
            content = { thumb(state) }
        )

        // Touch sensor Box - only render if thumb size is measured
        if (state.thumbSize > 0f) {
            Box(
                modifier = Modifier
                    .size(with(density) { state.thumbSize.toDp() })
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        // Calculate position based on touchAngle instead of rotating
                        val angleInRadians = (state.absoluteDegree - 90f) * PI.toFloat() / 180f
                        val thumbRadius =
                            state.radius - state.thumbSize / 2f // Offset halfway inwards by thumb size

                        val centerX = state.radius
                        val centerY = state.radius

                        // Calculate target position on the circle
                        val targetX = centerX + thumbRadius * kotlin.math.cos(angleInRadians)
                        val targetY = centerY + thumbRadius * kotlin.math.sin(angleInRadians)

                        // Current position is TopCenter, so thumb center is at (centerX, thumbSize/2)
                        val currentX = state.radius
                        val currentY = state.thumbSize / 2f

                        // Translate to the target position
                        translationX = targetX - currentX
                        translationY = targetY - (state.thumbSize / 2) //currentY
                    }
                    .onGloballyPositioned {
                        thumbPosition = it.positionInParent()
                    }
                    .pointerInput(state.degreeRange) {
                        val centerPx = Offset(state.radius, state.radius)

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
//                            println("drag = $dragOffset \t | ${change.position}")
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
                                        state.overshotAngle =
                                            draggingAngle - state.degreeRange.start
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
                    }
//                    .border(0.dp, Color.Blue)
                ,
                content = { }
            )
        }
    }
}