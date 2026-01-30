package com.sinasamaki.chroma.dial

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colors for customizing the Dial appearance when using the simple Dial overload.
 */
@Immutable
public class DialColors internal constructor(
    public val inactiveTrackColor: Color,
    public val activeTrackColor: Color,
    public val thumbColor: Color,
    public val thumbStrokeColor: Color,
    public val inactiveTickColor: Color,
    public val activeTickColor: Color,
) {
    public companion object {
        /**
         * Creates a [DialColors] instance with default colors.
         */
        public fun default(
            inactiveTrackColor: Color = Zinc700,
            activeTrackColor: Color = Violet500,
            thumbColor: Color = Violet950,
            thumbStrokeColor: Color = Violet500,
            inactiveTickColor: Color = Zinc600,
            activeTickColor: Color = Violet400,
        ): DialColors = DialColors(
            inactiveTrackColor = inactiveTrackColor,
            activeTrackColor = activeTrackColor,
            thumbColor = thumbColor,
            thumbStrokeColor = thumbStrokeColor,
            inactiveTickColor = inactiveTickColor,
            activeTickColor = activeTickColor,
        )
    }
}

public enum class RadiusMode {
    WIDTH,
    HEIGHT
}

@Stable
public class DialState(
    initialDegree: Float,
    public val degreeRange: ClosedFloatingPointRange<Float>,
    public val interval: Float = 0f,
    public val radiusMode: RadiusMode = RadiusMode.WIDTH,
    public var onValueChangeFinished: (() -> Unit)? = null,
    public val startDegrees: Float = 0f
) {
    private var degreeState by mutableFloatStateOf(initialDegree)
    private var overshotAngleState by mutableFloatStateOf(0f)
    private var radiusState by mutableFloatStateOf(0f)
    private var thumbSizeState by mutableFloatStateOf(0f)

    init {
        require(interval >= 0f) { "interval must be >= 0" }
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
        if (interval == 0f) return value.coerceIn(degreeRange)

        val coercedValue = value.coerceIn(degreeRange)
        val relativeValue = coercedValue - degreeRange.start

        // Find nearest regular snap point based on interval
        val snappedIndex = (relativeValue / interval).roundToInt()
        val regularSnap = (degreeRange.start + snappedIndex * interval).coerceIn(degreeRange)

        // The end of range is always a valid snap point, even if not aligned with interval
        val distToRegularSnap = kotlin.math.abs(coercedValue - regularSnap)
        val distToEnd = kotlin.math.abs(coercedValue - degreeRange.endInclusive)

        return if (distToEnd < distToRegularSnap) degreeRange.endInclusive else regularSnap
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

/**
 * Simple Dial composable with color customization.
 * Uses the default thumb and track styles with custom colors.
 */
@Composable
public fun Dial(
    degree: Float,
    onDegreeChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    startDegrees: Float = 0f,
    sweepDegrees: Float = 360f,
    radiusMode: RadiusMode = RadiusMode.WIDTH,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    interval: Float = 0f,
    colors: DialColors = DialColors.default(),
) {
    Dial(
        degree = degree,
        onDegreeChanged = onDegreeChanged,
        modifier = modifier,
        startDegrees = startDegrees,
        sweepDegrees = sweepDegrees,
        radiusMode = radiusMode,
        onValueChangeFinished = onValueChangeFinished,
        interactionSource = interactionSource,
        interval = interval,
        thumb = { state -> DefaultDialThumb(state, colors) },
        track = { state -> DefaultDialTrack(state, colors) }
    )
}

/**
 * Dial composable with full customization via thumb and track composables.
 */
@Composable
public fun Dial(
    degree: Float,
    onDegreeChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    startDegrees: Float = 0f,
    sweepDegrees: Float = 360f,
    radiusMode: RadiusMode = RadiusMode.WIDTH,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    interval: Float = 0f,
    thumb: @Composable (DialState) -> Unit,
    track: @Composable (DialState) -> Unit,
) {
    val degreeRange = 0f..sweepDegrees
    val state = remember(degreeRange, interval, radiusMode, startDegrees) {
        DialState(degree, degreeRange, interval, radiusMode, onValueChangeFinished, startDegrees)
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
                .graphicsLayer {
                    rotationZ = state.absoluteDegree
                    transformOrigin = TransformOrigin(0.5f, transformOriginY)
                }
                .hoverable(interactionSource),
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
                                draggingAngle = state.degree
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

/**
 * Default style thumb for the Dial component.
 * A simple circle with stroke.
 */
@Composable
private fun DefaultDialThumb(state: DialState, colors: DialColors) {
    Box(
        Modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(16.dp)
                .drawBehind {
                    drawCircle(
                        color = colors.thumbStrokeColor,
                        style = Stroke(width = 4.dp.toPx())
                    )
                    drawCircle(
                        color = colors.thumbColor,
                        radius = size.minDimension / 2 - 2.dp.toPx()
                    )
                }
        )
    }
}

/**
 * Default track for the Dial component.
 * A simple arc track with active portion overlay and optional ticks.
 */
@Composable
private fun DefaultDialTrack(state: DialState, colors: DialColors) {
    val trackWidth = 4.dp
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                val strokeWidth = trackWidth.toPx()
                val trackRadius = state.radius - 12.dp.toPx()
                val startAngle = state.startDegrees - 90f
                val sweepRange = state.degreeRange.endInclusive - state.degreeRange.start
                val activeSweep = state.degree - state.degreeRange.start

                // Draw background track (full sweep)
                drawArc(
                    color = colors.inactiveTrackColor,
                    startAngle = startAngle,
                    sweepAngle = sweepRange,
                    topLeft = Offset(center.x - trackRadius, center.y - trackRadius),
                    size = Size(trackRadius * 2, trackRadius * 2),
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Draw active track (from start to current position)
                if (activeSweep > 0f) {
                    drawArc(
                        color = colors.activeTrackColor,
                        startAngle = startAngle,
                        sweepAngle = activeSweep,
                        topLeft = Offset(center.x - trackRadius, center.y - trackRadius),
                        size = Size(trackRadius * 2, trackRadius * 2),
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Draw ticks if interval > 0
                if (state.interval > 0f) {
                    drawEveryInterval(
                        dialState = state,
                        interval = state.interval,
                        padding = 12.dp,
                    ) { data ->
                        val tickColor = if (data.inActiveRange) {
                            colors.activeTickColor
                        } else {
                            colors.inactiveTickColor
                        }
                        rotate(
                            degrees = data.degree,
                            pivot = data.position
                        ) {
                            drawLine(
                                color = tickColor,
                                start = data.position - Offset(0f, 4.dp.toPx()),
                                end = data.position + Offset(0f, 4.dp.toPx()),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round,
                            )
                        }
                    }
                }
            }
    )
}