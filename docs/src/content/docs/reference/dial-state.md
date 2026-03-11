---
title: DialState
description: API reference for the DialState class.
---

`DialState` is the state holder class that manages the dial's rotation state and provides information to custom thumb and track composables.

## Class Definition

```kotlin
@Stable
class DialState(
    initialDegree: Float,
    val degreeRange: ClosedFloatingPointRange<Float>,
    val interval: Float = 0f,
    val radiusMode: RadiusMode = RadiusMode.WIDTH,
    var onDegreeChangeFinished: (() -> Unit)? = null,
    val startDegrees: Float = 0f,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    val clockwise: Boolean = true,
)
```

Use `rememberDialState()` to create and remember a `DialState` in a composable.

## Constructor Parameters

### initialDegree
**Type:** `Float`

The initial rotation angle in degrees.

### degreeRange
**Type:** `ClosedFloatingPointRange<Float>`

The allowed range of rotation. The dial will clamp values to this range. This is always `0f..sweepDegrees`.

### interval
**Type:** `Float`
**Default:** `0f`

The degree interval between snap points. When `0f`, rotation is continuous. When set to a value like `15f`, the dial snaps every 15 degrees. The end of the range is always a valid snap point.

### radiusMode
**Type:** `RadiusMode`
**Default:** `RadiusMode.WIDTH`

How to calculate the radius from the dial's constraints.

### onDegreeChangeFinished
**Type:** `(() -> Unit)?`
**Default:** `null`

Callback invoked when dragging ends.

### startDegrees
**Type:** `Float`
**Default:** `0f`

The absolute starting angle for the dial arc in screen coordinates. Used internally for positioning the thumb and track.

### valueRange
**Type:** `ClosedFloatingPointRange<Float>`
**Default:** `0f..1f`

The range that `mappedValue` maps to. Use this to get values in a custom domain (e.g., `0f..100f` for percentages).

### clockwise
**Type:** `Boolean`
**Default:** `true`

When `false`, the dial rotates counterclockwise.

## Properties

### degree
**Type:** `Float` (read/write)

The current rotation angle in degrees, **relative to `startDegrees`**. This value ranges from `degreeRange.start` to `degreeRange.endInclusive` (typically `0` to `sweepDegrees`).

```kotlin
val currentAngle = state.degree
```

### absoluteDegree
**Type:** `Float` (read-only)

The absolute rotation angle in screen coordinates. For clockwise dials this is `startDegrees + degree`; for counterclockwise dials this is `startDegrees - degree`. Use this for drawing operations that need the actual screen angle.

```kotlin
// For a clockwise dial with startDegrees=270f and degree=90f:
// absoluteDegree = 360f
val screenAngle = state.absoluteDegree
```

### value
**Type:** `Float` (read-only)

A normalized value between `0` and `1` representing the position within the degree range.

- Returns `0f` when `degree` equals `degreeRange.start`
- Returns `1f` when `degree` equals `degreeRange.endInclusive`

```kotlin
val percentage = state.value * 100  // 0 to 100
```

**Formula:**
```
value = (degree - degreeRange.start) / (degreeRange.endInclusive - degreeRange.start)
```

### mappedValue
**Type:** `Float` (read-only)

`value` mapped to `valueRange`. Useful when you want values in a custom unit:

```kotlin
// With valueRange = 0f..100f
val temperature = state.mappedValue  // 0.0 to 100.0
```

**Formula:**
```
mappedValue = valueRange.start + value * (valueRange.endInclusive - valueRange.start)
```

### degreeRange
**Type:** `ClosedFloatingPointRange<Float>` (read-only)

The allowed range for the `degree` property. This is always `0f..sweepDegrees`.

```kotlin
val totalSweep = state.degreeRange.endInclusive - state.degreeRange.start
val isAtEnd = state.degree == state.degreeRange.endInclusive
```

### interval
**Type:** `Float` (read-only)

The degree interval between snap points. When `0f`, the dial rotates continuously.

### radiusMode
**Type:** `RadiusMode` (read-only)

The radius calculation mode, as specified during construction.

### radius
**Type:** `Float` (read-only, internally set)

The calculated radius in pixels. Set by the Dial composable based on constraints and `radiusMode`.

```kotlin
val arcRadius = state.radius - 12.dp.toPx()
```

### thumbSize
**Type:** `Float` (read-only, internally set)

The measured size of the thumb composable in pixels. Set after the thumb is measured.

### clockwise
**Type:** `Boolean` (read-only)

Whether the dial rotates clockwise. Affects `absoluteDegree` and drag direction.

### enabled
**Type:** `Boolean` (read-only, set by Dial composable)

Whether the dial responds to drag input.

### overshootDegrees
**Type:** `Float` (read-only, computed)

The rendered overshoot offset when the user drags beyond the allowed range. This value is decay-adjusted (non-linear) for a natural rubber-band feel.

- Negative when dragging below `degreeRange.start`
- Positive when dragging above `degreeRange.endInclusive`
- `0f` when within range or after drag ends (springs back to zero)

```kotlin
// The track uses this to extend the active arc visually during overshoot
val overshoot = state.overshootDegrees
```

### overshootDecay
**Type:** `Float`
**Default:** `0.5f`

Controls how strongly the raw drag overshoot is dampened. `0f` means no dampening (linear); `1f` means full dampening (no visible overshoot).

### overshootAnimationSpec
**Type:** `AnimationSpec<Float>`
**Default:** `spring()`

The animation spec used when springing back from an overshoot position after the drag ends.

## Methods

### animateTo

```kotlin
suspend fun animateTo(
    targetDegree: Float,
    animationSpec: AnimationSpec<Float> = spring(),
)
```

Animates `degree` to `targetDegree`. Must be called from a coroutine scope. The target is clamped to `degreeRange`.

```kotlin
val scope = rememberCoroutineScope()
Button(onClick = { scope.launch { state.animateTo(180f) } }) {
    Text("Go to center")
}
```

### calculateSnappedValue

```kotlin
fun calculateSnappedValue(value: Float): Float
```

Returns the nearest snap position for a given degree value. Used internally by the Dial.

- If `interval == 0f`, returns the value clamped to the range (no snapping)
- Otherwise, returns the nearest snap position based on the interval
- The end of the range is always a valid snap point

## Callbacks

### onValueChange
**Type:** `(Float) -> Unit`

Internal callback used by the Dial to notify of value changes. Set to the `onDegreeChange` parameter.

### onDegreeChangeFinished
**Type:** `(() -> Unit)?`

Callback invoked when the user finishes dragging. Can be set via constructor or directly on the property.

## Usage in Custom Composables

### In Thumb Composable

```kotlin
thumb = { state ->
    Box(
        Modifier
            .size(32.dp)
            .graphicsLayer {
                alpha = if (state.overshootDegrees != 0f) 0.7f else 1f
            }
            .background(Color.Blue, CircleShape)
    )
}
```

### In Track Composable

```kotlin
track = { state ->
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                val sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start

                // Background arc
                drawArc(
                    color = Gray300,
                    startAngle = state.startDegrees,
                    sweepAngle = sweepAngle,
                    radius = state.radius,
                )

                // Progress arc
                drawArc(
                    color = Blue500,
                    startAngle = state.startDegrees,
                    sweepAngle = state.degree,
                    radius = state.radius,
                )
            }
    )
}
```

## See Also

- [Dial Basics](/components/dial-basics/) - Learn how to use the Dial component
- [RadiusMode](/reference/radius-mode/) - Understand radius calculation modes
