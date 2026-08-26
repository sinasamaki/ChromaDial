package com.sinasamaki.chroma.dial

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.roundToInt

public enum class RadiusMode {
    WIDTH,
    HEIGHT
}

/**
 * Controls how the dial's radius and center are derived from available layout space.
 *
 * @param radiusMode Which dimension (width or height) the radius is based on.
 * @param radiusFraction Fraction of the chosen dimension to use as radius. Default is 0.5 (half).
 * @param center Center of the dial as a fraction of (width, height). Default is Offset(0.5, 0.5).
 */
public data class DialLayout(
    val radiusMode: RadiusMode = RadiusMode.WIDTH,
    val radiusFraction: Float = 0.5f,
    val center: Offset = Offset(0.5f, 0.5f),
) {
    public companion object {
        public fun width(
            fraction: Float = 0.5f,
            center: Offset = Offset(0.5f, 0.5f),
        ): DialLayout = DialLayout(RadiusMode.WIDTH, fraction, center)

        public fun height(
            fraction: Float = 0.5f,
            center: Offset = Offset(0.5f, 0.5f),
        ): DialLayout = DialLayout(RadiusMode.HEIGHT, fraction, center)
    }
}

@Stable
public class DialState(
    initialDegree: Float = 0f,
) {
    private var degreeState by mutableFloatStateOf(initialDegree)
    private val _degreeAnimatable = Animatable(initialDegree)
    private var radiusState by mutableFloatStateOf(0f)
    private var centerState by mutableStateOf(Offset.Zero)
    private var thumbSizeState by mutableFloatStateOf(0f)
    internal val overshootAnimatable = Animatable(0f)

    private var sweepDegreesState by mutableFloatStateOf(360f)
    private var startDegreesState by mutableFloatStateOf(0f)
    private var intervalState by mutableFloatStateOf(0f)
    private var layoutState by mutableStateOf(DialLayout())
    private var valueRangeState by mutableStateOf<ClosedFloatingPointRange<Float>>(0f..1f)
    private var clockwiseState by mutableStateOf(true)
    private var enabledState by mutableStateOf(true)

    /** Total arc sweep in degrees. Set by the [Dial] composable. */
    public var sweepDegrees: Float
        get() = sweepDegreesState
        internal set(value) { sweepDegreesState = value }

    /** Visual starting position on screen (e.g. 180f for bottom). Set by the [Dial] composable. */
    public var startDegrees: Float
        get() = startDegreesState
        internal set(value) { startDegreesState = value }

    /** Snap interval in degrees (0 = continuous rotation). Set by the [Dial] composable. */
    public var interval: Float
        get() = intervalState
        internal set(value) {
            require(value >= 0f) { "interval must be >= 0" }
            intervalState = value
        }

    /** Controls radius and center derivation. Set by the [Dial] composable. See [DialLayout]. */
    public var layout: DialLayout
        get() = layoutState
        internal set(value) { layoutState = value }

    /** The range that [mappedValue] maps to. Set by the [Dial] composable. */
    public var valueRange: ClosedFloatingPointRange<Float>
        get() = valueRangeState
        internal set(value) { valueRangeState = value }

    /** Rotation direction. When false, rotation is counterclockwise. Set by the [Dial] composable. */
    public var clockwise: Boolean
        get() = clockwiseState
        internal set(value) { clockwiseState = value }

    /** Whether the dial responds to drag input. Set by the [Dial] composable. */
    public var enabled: Boolean
        get() = enabledState
        internal set(value) { enabledState = value }

    /** Called when the user finishes dragging. Set by the [Dial] composable. */
    public var onDegreeChangeFinished: (() -> Unit)? = null
        internal set

    /** Internal allowed rotation range (0f..[sweepDegrees]). */
    public val degreeRange: ClosedFloatingPointRange<Float>
        get() = 0f..sweepDegreesState

    /**
     * The absolute degree for rendering purposes.
     * For clockwise dials: startDegrees + degree (clamped to degreeRange).
     * For counterclockwise dials: startDegrees - degree (clamped to degreeRange).
     */
    public val absoluteDegree: Float
        get() {
            val clamped = degree.coerceIn(degreeRange)
            return if (clockwise) startDegrees + clamped else startDegrees - clamped
        }

    public var radius: Float
        internal set(value) {
            radiusState = value
        }
        get() = radiusState

    /** Dial center in pixels. Set by the [Dial] composable based on constraints and [layout]. */
    public var center: Offset
        internal set(value) {
            centerState = value
        }
        get() = centerState

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

    /** Normalized 0–1 value based on position within [degreeRange]. */
    public val value: Float
        get() {
            val range = degreeRange.endInclusive - degreeRange.start
            return if (range == 0f) 0f else (degree - degreeRange.start) / range
        }

    /** [value] mapped to [valueRange]. */
    public val mappedValue: Float
        get() = valueRange.start + value * (valueRange.endInclusive - valueRange.start)

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
            val value = sign * (1f - kotlin.math.exp(-kotlin.math.abs(x) * k)) / k
            return if (clockwise) value else -value
        }

    /** Invoked with the new degree as the dial is dragged. Wired by the [Dial] composable. */
    internal var onValueChange: (Float) -> Unit = {}

    /**
     * Applies configuration from the [Dial] composable, mutating this state in place so that
     * changing config (interval, clockwise, valueRange, …) never recreates the state or resets
     * [degree]. Called on every composition; assignments to unchanged snapshot fields are no-ops.
     */
    internal fun applyConfig(
        startDegrees: Float,
        sweepDegrees: Float,
        interval: Float,
        layout: DialLayout,
        valueRange: ClosedFloatingPointRange<Float>,
        clockwise: Boolean,
        enabled: Boolean,
        overshootDecay: Float,
        overshootAnimationSpec: AnimationSpec<Float>,
        onDegreeChangeFinished: (() -> Unit)?,
    ) {
        this.startDegrees = startDegrees
        this.sweepDegrees = sweepDegrees
        this.interval = interval
        this.layout = layout
        this.valueRange = valueRange
        this.clockwise = clockwise
        this.enabled = enabled
        this.overshootDecay = overshootDecay
        this.overshootAnimationSpec = overshootAnimationSpec
        this.onDegreeChangeFinished = onDegreeChangeFinished
        if (degree > sweepDegrees) degree = sweepDegrees
    }

    /**
     * Animates [degree] to [targetDegree] using [animationSpec].
     * Must be called from a coroutine scope.
     */
    public suspend fun animateTo(
        targetDegree: Float,
        animationSpec: AnimationSpec<Float> = spring(),
    ) {
        _degreeAnimatable.snapTo(degreeState)
        _degreeAnimatable.animateTo(
            targetValue = targetDegree.coerceIn(degreeRange),
            animationSpec = animationSpec,
        ) {
            degreeState = value
            onValueChange(value)
        }
    }
}

/**
 * Creates and remembers a [DialState] for hoisting the dial's position outside of `thumb`/`track`.
 *
 * Only the initial position lives here; all other configuration (sweep, interval, value range,
 * direction, …) is passed to the [Dial] composable, which applies it onto this state in place.
 * That means changing configuration never recreates the state or resets [DialState.degree].
 *
 * @param initialDegree Initial dial position. Clamped to the sweep once [Dial] applies its config.
 */
@Composable
public fun rememberDialState(
    initialDegree: Float = 0f,
): DialState = remember { DialState(initialDegree) }

// ─── Simple (colors) overloads ───────────────────────────────────────────────

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
    layout: DialLayout = DialLayout(),
    onDegreeChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    interval: Float = 0f,
    steps: Int = 0,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    clockwise: Boolean = true,
    enabled: Boolean = true,
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
        layout = layout,
        onDegreeChangeFinished = onDegreeChangeFinished,
        interactionSource = interactionSource,
        interval = interval,
        steps = steps,
        valueRange = valueRange,
        clockwise = clockwise,
        enabled = enabled,
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
    layout: DialLayout = DialLayout(),
    onDegreeChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    interval: Float = 0f,
    steps: Int = 0,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    clockwise: Boolean = true,
    enabled: Boolean = true,
    overshootDecay: Float = 0.5f,
    overshootAnimationSpec: AnimationSpec<Float> = spring(),
    thumb: @Composable (DialState) -> Unit,
    track: @UiComposable @Composable (DialState) -> Unit,
) {
    val state = remember { DialState(initialDegree = degree) }
    val effectiveInterval = if (steps > 0) sweepDegrees / steps else interval
    state.applyConfig(
        startDegrees = startDegrees,
        sweepDegrees = sweepDegrees,
        interval = effectiveInterval,
        layout = layout,
        valueRange = valueRange,
        clockwise = clockwise,
        enabled = enabled,
        overshootDecay = overshootDecay,
        overshootAnimationSpec = overshootAnimationSpec,
        onDegreeChangeFinished = onDegreeChangeFinished,
    )
    // Controlled mode: the caller owns `degree`. Drag only reports; the parent drives state.degree.
    state.onValueChange = onDegreeChange
    val clampedDegree = degree.coerceIn(state.degreeRange)
    state.degree = clampedDegree
    SideEffect {
        if (clampedDegree != degree) onDegreeChange(clampedDegree)
    }

    DialImpl(
        state = state,
        enabled = enabled,
        modifier = modifier,
        interactionSource = interactionSource,
        thumb = thumb,
        track = track,
    )
}

// ─── State-hoisting overloads ─────────────────────────────────────────────────

/**
 * Dial composable that takes an externally-managed [DialState], with color customization.
 * Use [rememberDialState] to create and remember a [DialState]. The dial owns [DialState.degree]:
 * drag input writes it directly, so it can be read back anywhere (e.g. `state.mappedValue`).
 */
@Composable
public fun Dial(
    state: DialState = rememberDialState(),
    modifier: Modifier = Modifier,
    startDegrees: Float = 0f,
    sweepDegrees: Float = 360f,
    interval: Float = 0f,
    steps: Int = 0,
    layout: DialLayout = DialLayout(),
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    clockwise: Boolean = true,
    enabled: Boolean = true,
    overshootDecay: Float = 0.5f,
    overshootAnimationSpec: AnimationSpec<Float> = spring(),
    onDegreeChange: ((Float) -> Unit)? = null,
    onDegreeChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: DialColors = DialColors.default(),
) {
    Dial(
        state = state,
        modifier = modifier,
        startDegrees = startDegrees,
        sweepDegrees = sweepDegrees,
        interval = interval,
        steps = steps,
        layout = layout,
        valueRange = valueRange,
        clockwise = clockwise,
        enabled = enabled,
        overshootDecay = overshootDecay,
        overshootAnimationSpec = overshootAnimationSpec,
        onDegreeChange = onDegreeChange,
        onDegreeChangeFinished = onDegreeChangeFinished,
        interactionSource = interactionSource,
        thumb = { s -> DefaultDialThumb(s, colors) },
        track = { s -> DefaultDialTrack(s, colors) },
    )
}

/**
 * Dial composable that takes an externally-managed [DialState] with full customization.
 * Use [rememberDialState] to create and remember a [DialState]. The dial owns [DialState.degree]:
 * drag input writes it directly, so it can be read back anywhere (e.g. `state.mappedValue`).
 */
@Composable
public fun Dial(
    state: DialState = rememberDialState(),
    modifier: Modifier = Modifier,
    startDegrees: Float = 0f,
    sweepDegrees: Float = 360f,
    interval: Float = 0f,
    steps: Int = 0,
    layout: DialLayout = DialLayout(),
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    clockwise: Boolean = true,
    enabled: Boolean = true,
    overshootDecay: Float = 0.5f,
    overshootAnimationSpec: AnimationSpec<Float> = spring(),
    onDegreeChange: ((Float) -> Unit)? = null,
    onDegreeChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable (DialState) -> Unit,
    track: @UiComposable @Composable (DialState) -> Unit,
) {
    val effectiveInterval = if (steps > 0) sweepDegrees / steps else interval
    state.applyConfig(
        startDegrees = startDegrees,
        sweepDegrees = sweepDegrees,
        interval = effectiveInterval,
        layout = layout,
        valueRange = valueRange,
        clockwise = clockwise,
        enabled = enabled,
        overshootDecay = overshootDecay,
        overshootAnimationSpec = overshootAnimationSpec,
        onDegreeChangeFinished = onDegreeChangeFinished,
    )
    // Hoisted mode: the state owns `degree`. Drag writes it directly; onDegreeChange just notifies.
    state.onValueChange = { newDegree ->
        state.degree = newDegree
        onDegreeChange?.invoke(newDegree)
    }
    DialImpl(
        state = state,
        enabled = enabled,
        modifier = modifier,
        interactionSource = interactionSource,
        thumb = thumb,
        track = track,
    )
}

// ─── Internal implementation ──────────────────────────────────────────────────

@Composable
private fun DialImpl(
    state: DialState,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource,
    thumb: @Composable (DialState) -> Unit,
    track: @UiComposable @Composable (DialState) -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = modifier.semantics {
            contentDescription = "Dial"
            stateDescription = "${(state.value * 100).roundToInt()}%"
            setProgress { targetValue ->
                val clampedTarget = targetValue.coerceIn(0f, 1f)
                val targetDegree = state.degreeRange.start +
                        clampedTarget * (state.degreeRange.endInclusive - state.degreeRange.start)
                scope.launch { state.animateTo(targetDegree) }
                true
            }
        }
    ) {
        val dialSize by remember(constraints) {
            derivedStateOf {
                Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
            }
        }

        state.radius = state.layout.radiusFraction * when (state.layout.radiusMode) {
            RadiusMode.WIDTH -> dialSize.width
            RadiusMode.HEIGHT -> dialSize.height
        }
        state.center = Offset(
            dialSize.width * state.layout.center.x,
            dialSize.height * state.layout.center.y,
        )

        var thumbPosition by remember { mutableStateOf(Offset.Zero) }
        var draggingAngle by remember { mutableStateOf(state.degree.coerceIn(state.degreeRange)) }
        var currentDragInteraction by remember { mutableStateOf<DragInteraction.Start?>(null) }

        Box(Modifier.matchParentSize()) {
            track(state)
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .onGloballyPositioned { coordinates ->
                    state.thumbSize = coordinates.size.width.toFloat()
                }
                .graphicsLayer {
                    val angle = state.absoluteDegree + state.overshootDegrees
                    val angleInRadians = (angle - 90f) * PI.toFloat() / 180f
                    val thumbRadius = state.radius - state.thumbSize / 2f
                    val targetX = state.center.x + thumbRadius * kotlin.math.cos(angleInRadians)
                    val targetY = state.center.y + thumbRadius * kotlin.math.sin(angleInRadians)
                    translationX = targetX - state.thumbSize / 2f
                    translationY = targetY - state.thumbSize / 2f
                    rotationZ = angle
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                    alpha = if (state.thumbSize > 0f) 1f else 0f
                },
            content = { thumb(state) }
        )

        if (state.thumbSize > 0f) {
            Box(
                modifier = Modifier
                    .size(with(density) { state.thumbSize.toDp() })
                    .align(Alignment.TopStart)
                    .graphicsLayer {
                        val angleInRadians = (state.absoluteDegree - 90f) * PI.toFloat() / 180f
                        val thumbRadius = state.radius - state.thumbSize / 2f

                        val centerX = state.center.x
                        val centerY = state.center.y

                        val targetX = centerX + thumbRadius * kotlin.math.cos(angleInRadians)
                        val targetY = centerY + thumbRadius * kotlin.math.sin(angleInRadians)

                        translationX = targetX - state.thumbSize / 2f
                        translationY = targetY - state.thumbSize / 2f
                    }
                    .onGloballyPositioned {
                        thumbPosition = it.positionInParent()
                    }
                    .then(
                        if (enabled) {
                            Modifier
                                .pointerInput(state.degreeRange) {
                                    val centerPx = state.center

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
                                            val dragAngle = calculateAngle(dragOffset)
                                            var delta = dragAngle - previousAngle

                                            if (delta > 180f) delta -= 360f
                                            if (delta < -180f) delta += 360f

                                            if (!state.clockwise) delta = -delta

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
                                            draggingAngle = state.degree
                                            scope.launch {
                                                state.overshootAnimatable.animateTo(
                                                    0f,
                                                    state.overshootAnimationSpec
                                                )
                                            }
                                            state.onDegreeChangeFinished?.invoke()
                                            (dragInteraction as? DragInteraction.Start)?.let {
                                                scope.launch {
                                                    interactionSource.emit(
                                                        DragInteraction.Stop(it)
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                                .pointerHoverIcon(PointerIcon.Hand, true)
                                .hoverable(interactionSource)
                        } else {
                            Modifier
                        }
                    ),
                content = { }
            )
        }
    }
}

