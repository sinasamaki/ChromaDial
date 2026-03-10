package com.sinasamaki.chroma.dial

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
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
            activeTrackColor: Color = Lime500,
            thumbColor: Color = Zinc950,
            thumbStrokeColor: Color = Lime400,
            inactiveTickColor: Color = Zinc700,
            activeTickColor: Color = Lime300,
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
    public var onDegreeChangeFinished: (() -> Unit)? = null,
    public val startDegrees: Float = 0f
) {
    private var degreeState by mutableFloatStateOf(initialDegree)
    private var radiusState by mutableFloatStateOf(0f)
    private var thumbSizeState by mutableFloatStateOf(0f)
    internal val overshootAnimatable = Animatable(0f)

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

    public var overshootDecay: Float = 0.5f
        internal set

    public var overshootAnimationSpec: AnimationSpec<Float> = spring()
        internal set

    public val overshootDegrees: Float
        get() {
            val x = overshootAnimatable.value
            if (overshootDecay <= 0f) return x
            if (overshootDecay >= 1f) return 0f
            val k = 0.05f * overshootDecay / (1f - overshootDecay)
            val sign = if (x < 0f) -1f else 1f
            return sign * (1f - kotlin.math.exp(-kotlin.math.abs(x) * k)) / k
        }

    public var onValueChange: (Float) -> Unit = {}
}

/**
 * Simple Dial composable with color customization.
 * Uses the default thumb and track styles with custom colors.
 */
@Composable
public fun Dial(
    degree: Float,
    onDegreeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    startDegrees: Float = 0f,
    sweepDegrees: Float = 360f,
    radiusMode: RadiusMode = RadiusMode.WIDTH,
    onDegreeChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    interval: Float = 0f,
    overshootDecay: Float = 0.5f,
    overshootAnimationSpec: AnimationSpec<Float> = spring(),
    colors: DialColors = DialColors.default(),
) {
    Dial(
        degree = degree,
        onDegreeChange = onDegreeChange,
        modifier = modifier,
        startDegrees = startDegrees,
        sweepDegrees = sweepDegrees,
        radiusMode = radiusMode,
        onDegreeChangeFinished = onDegreeChangeFinished,
        interactionSource = interactionSource,
        interval = interval,
        overshootDecay = overshootDecay,
        overshootAnimationSpec = overshootAnimationSpec,
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
    onDegreeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    startDegrees: Float = 0f,
    sweepDegrees: Float = 360f,
    radiusMode: RadiusMode = RadiusMode.WIDTH,
    onDegreeChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    interval: Float = 0f,
    overshootDecay: Float = 0.5f,
    overshootAnimationSpec: AnimationSpec<Float> = spring(),
    thumb: @Composable (DialState) -> Unit,
    track: @UiComposable @Composable (DialState) -> Unit,
) {
    val degreeRange = 0f..sweepDegrees
    val state = remember(degreeRange, interval, radiusMode, startDegrees) {
        DialState(degree, degreeRange, interval, radiusMode, onDegreeChangeFinished, startDegrees)
    }
    state.onDegreeChangeFinished = onDegreeChangeFinished
    state.onValueChange = onDegreeChange
    state.degree = degree
    SideEffect {
        state.overshootDecay = overshootDecay
        state.overshootAnimationSpec = overshootAnimationSpec
    }

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
    track: @UiComposable @Composable (DialState) -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = modifier
    ) {
        val dialSize by remember(constraints) {
            derivedStateOf {
                Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
            }
        }

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
                    state.thumbSize = coordinates.size.width.toFloat()
                }
                .graphicsLayer {
                    rotationZ = state.absoluteDegree + state.overshootDegrees
                    transformOrigin = TransformOrigin(0.5f, transformOriginY)
                },
            content = { thumb(state) }
        )

        if (state.thumbSize > 0f) {
            Box(
                modifier = Modifier
                    .size(with(density) { state.thumbSize.toDp() })
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        val angleInRadians = (state.absoluteDegree - 90f) * PI.toFloat() / 180f
                        val thumbRadius =
                            state.radius - state.thumbSize / 2f

                        val centerX = state.radius
                        val centerY = state.radius

                        val targetX = centerX + thumbRadius * kotlin.math.cos(angleInRadians)
                        val targetY = centerY + thumbRadius * kotlin.math.sin(angleInRadians)

                        val currentX = state.radius
                        val currentY = state.thumbSize / 2f

                        translationX = targetX - currentX
                        translationY = targetY - (state.thumbSize / 2)
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
                                var delta = dragAngle - previousAngle

                                if (delta > 180f) delta -= 360f
                                if (delta < -180f) delta += 360f

                                draggingAngle += delta

                                if (draggingAngle in state.degreeRange) {
                                    scope.launch { state.overshootAnimatable.snapTo(0f) }
                                    val newValue = state.calculateSnappedValue(draggingAngle)
                                    if (newValue != state.degree) {
                                        state.onValueChange(newValue)
                                    }
                                } else {
                                    val clampedAngle = draggingAngle.coerceIn(state.degreeRange)
                                    val rawOvershoot = when {
                                        draggingAngle < state.degreeRange.start ->
                                            draggingAngle - state.degreeRange.start
                                        else ->
                                            draggingAngle - state.degreeRange.endInclusive
                                    }
                                    scope.launch { state.overshootAnimatable.snapTo(rawOvershoot) }

                                    val newValue = state.calculateSnappedValue(clampedAngle)
                                    if (newValue != state.degree) {
                                        state.onValueChange(newValue)
                                    }
                                }
                                previousAngle = dragAngle
                            },
                            onDragEnd = {
                                dragTracker = Offset.Zero
                                draggingAngle = state.degree
                                scope.launch {
                                    state.overshootAnimatable.animateTo(0f, state.overshootAnimationSpec)
                                }
                                state.onDegreeChangeFinished?.invoke()
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
                    .pointerHoverIcon(PointerIcon.Hand, true)
                    .hoverable(interactionSource),
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

private const val RING_ALPHA_DECAY = 0.4f
private const val RING_SCALE_INCREMENT = 0.15f

/**
 * Default track for the Dial component.
 * A simple arc track with active portion overlay and optional ticks.
 * Supports multi-ring display when sweep exceeds 360 degrees.
 */
@Composable
private fun DefaultDialTrack(state: DialState, colors: DialColors) {
    val trackWidth = 4.dp
    val sweepRange = state.degreeRange.endInclusive - state.degreeRange.start
    val totalSweep = state.degree - state.degreeRange.start

    val maxPossibleRings = maxOf(1, kotlin.math.ceil(sweepRange / 360.0).toInt())
    val numActiveRings =
        maxOf(1, kotlin.math.ceil(totalSweep.coerceAtLeast(0.001f) / 360.0).toInt())

    Box(Modifier.fillMaxSize()) {
        for (ringIndex in (maxPossibleRings - 1) downTo 0) {
            val ringStartSweep = ringIndex * 360f
            val ringMaxSweep = (sweepRange - ringStartSweep).coerceIn(0f, 360f)
            val ringSweep = (totalSweep - ringStartSweep).coerceIn(0f, ringMaxSweep)
            val isActiveRing = ringIndex < numActiveRings
            val ringsAbove = if (isActiveRing) (numActiveRings - 1 - ringIndex)
                .coerceAtLeast(0) else 0
            val targetScale = 1f + ringsAbove * RING_SCALE_INCREMENT
            val targetAlpha = (1f - ringsAbove * RING_ALPHA_DECAY).coerceAtLeast(0.01f)
            val isInnermostRing = ringIndex == numActiveRings - 1
            val targetStrokeMultiplier = if (isActiveRing) 1f else 0f

            key(ringIndex) {
                val scale by animateFloatAsState(
                    targetValue = targetScale,
                    animationSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                    )
                )
                val strokeMultiplier by animateFloatAsState(
                    targetValue = targetStrokeMultiplier,
                    animationSpec = spring(
                        stiffness = if (isActiveRing)
                            Spring.StiffnessLow
                        else
                            Spring.StiffnessHigh
                    )
                )
                val alpha by animateFloatAsState(
                    targetValue = targetAlpha,
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMedium,
                    )
                )

                if (strokeMultiplier > 0f || ringMaxSweep > 0f) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .drawBehind {
                                val baseStrokeWidth = trackWidth.toPx()
                                val strokeWidth = baseStrokeWidth * strokeMultiplier
                                val trackRadius = state.radius - 12.dp.toPx()
                                val startAngle = state.startDegrees - 90f

                                if (ringMaxSweep > 0f) {
                                    drawArc(
                                        color = colors.inactiveTrackColor.copy(alpha = alpha),
                                        startAngle = startAngle,
                                        sweepAngle = ringMaxSweep,
                                        topLeft = Offset(
                                            center.x - trackRadius,
                                            center.y - trackRadius
                                        ),
                                        size = Size(trackRadius * 2, trackRadius * 2),
                                        useCenter = false,
                                        style = Stroke(
                                            width = strokeWidth,
                                            cap = StrokeCap.Round
                                        )
                                    )
                                }

                                if (ringSweep > 0f && strokeWidth > 0f) {
                                    drawArc(
                                        color = colors.activeTrackColor.copy(alpha = alpha),
                                        startAngle = startAngle,
                                        sweepAngle = ringSweep,
                                        topLeft = Offset(
                                            center.x - trackRadius,
                                            center.y - trackRadius
                                        ),
                                        size = Size(trackRadius * 2, trackRadius * 2),
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }

                                if (state.interval > 0f && ringMaxSweep > 0f) {
                                    val tickRangeStart = state.startDegrees
                                    val tickRangeEnd = state.startDegrees + ringMaxSweep
                                    val currentDegreeForTicks = if (isActiveRing) {
                                        state.startDegrees + ringSweep
                                    } else {
                                        state.startDegrees
                                    }

                                    drawEveryInterval(
                                        degreeRange = tickRangeStart..tickRangeEnd,
                                        radius = state.radius,
                                        interval = state.interval,
                                        padding = 12.dp,
                                        currentDegree = currentDegreeForTicks,
                                    ) { data ->
                                        val tickColor = if (data.inActiveRange && isActiveRing) {
                                            colors.activeTickColor.copy(alpha = alpha)
                                        } else {
                                            colors.inactiveTickColor.copy(alpha = alpha)
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
            }
        }
    }
}