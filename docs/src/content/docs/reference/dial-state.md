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
    val startDegrees: Float = 0f
)
```

## Constructor Parameters

### initialDegree
**Type:** `Float`

The initial rotation angle in degrees.

### degreeRange
**Type:** `ClosedFloatingPointRange<Float>`

The allowed range of rotation. The dial will clamp values to this range.

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

## Properties

### degree
**Type:** `Float` (read/write)

The current rotation angle in degrees, **relative to `startDegrees`**. This value ranges from `degreeRange.start` to `degreeRange.endInclusive` (typically `0` to `sweepDegrees`).

```kotlin
// Reading the relative angle
val currentAngle = state.degree

// Writing (typically done internally)
state.degree = 90f
```

### absoluteDegree
**Type:** `Float` (read-only)

The absolute rotation angle in screen coordinates. Calculated as `startDegrees + degree`. Use this for drawing operations that need the actual screen angle.

```kotlin
// For a dial with startDegrees=270f and degree=90f:
// absoluteDegree = 360f (pointing upward)
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

### degreeRange
**Type:** `ClosedFloatingPointRange<Float>` (read-only)

The allowed range for the `degree` property. This is always `0f..sweepDegrees`.

```kotlin
// Get the sweep angle (total rotation range)
val totalSweep = state.degreeRange.endInclusive - state.degreeRange.start

// Check if degree is at start
val isAtStart = state.degree == state.degreeRange.start

// Check if degree is at end
val isAtEnd = state.degree == state.degreeRange.endInclusive
```

### interval
**Type:** `Float` (read-only)

The degree interval between snap points, as specified during construction. When `0f`, the dial rotates continuously.

### radiusMode
**Type:** `RadiusMode` (read-only)

The radius calculation mode, as specified during construction.

### radius
**Type:** `Float` (read-only, internally set)

The calculated radius in pixels. Set by the Dial composable based on constraints and `radiusMode`.

```kotlin
// Draw a circle at the edge of the dial
drawCircle(
    color = Color.Blue,
    radius = 4.dp.toPx(),
    center = Offset(state.radius, 0f)
)
```

### thumbSize
**Type:** `Float` (read-only, internally set)

The measured size of the thumb composable in pixels. Set after the thumb is measured.

```kotlin
// Offset drawing by thumb size
val padding = state.thumbSize / 2
```

### overshotAngle
**Type:** `Float` (read/write)

The amount in degrees that the user has dragged beyond the allowed range. Useful for implementing rubber-band effects.

- Negative when dragging below `degreeRange.start`
- Positive when dragging above `degreeRange.endInclusive`
- `0f` when within range or when drag ends

```kotlin
// Scale down thumb when overshooting
val scale = 1f - (state.overshotAngle.absoluteValue / 180f).coerceIn(0f, 0.3f)
```

## Methods

### calculateSnappedValue

```kotlin
fun calculateSnappedValue(value: Float): Float
```

Calculates the nearest snap position for a given degree value. Used internally to implement step snapping.

**Parameters:**
- `value`: The degree value to snap

**Returns:** The snapped degree value, clamped to the degree range

**Behavior:**
- If `interval == 0f`, returns the value clamped to the range (no snapping)
- Otherwise, returns the nearest snap position based on the interval
- The end of the range is always a valid snap point, even if the interval doesn't divide evenly

```kotlin
// With interval = 30f and degreeRange = 0f..100f
// Snap positions: 0°, 30°, 60°, 90°, 100° (end is always valid)
state.calculateSnappedValue(85f)  // Returns 90f (nearest regular snap)
state.calculateSnappedValue(96f)  // Returns 100f (end of range)
```

## Callbacks

### onValueChange
**Type:** `(Float) -> Unit`

Internal callback used by the Dial to notify of value changes. Set to the `onDegreeChanged` parameter.

### onDegreeChangeFinished
**Type:** `(() -> Unit)?`

Callback invoked when the user finishes dragging. Can be set via constructor or directly on the property.

## Usage in Custom Composables

### In Thumb Composable

```kotlin
thumb = { state ->
    // Access state properties
    val currentDegree = state.degree
    val normalizedValue = state.value
    val isOvershooting = state.overshotAngle != 0f

    Box(
        Modifier
            .size(32.dp)
            .graphicsLayer {
                // Use overshot for visual feedback
                alpha = if (isOvershooting) 0.7f else 1f
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
                // Use radius for drawing
                val arcRadius = state.radius - state.thumbSize / 2

                // Use startDegrees for arc positioning (absolute screen angle)
                val startAngle = state.startDegrees - 90f
                val sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start

                // Use degree for progress (relative to start)
                val progress = state.degree

                // Draw background arc
                drawArc(
                    color = Color.Gray,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    // ...
                )

                // Draw progress arc
                drawArc(
                    color = Color.Blue,
                    startAngle = startAngle,
                    sweepAngle = progress,
                    // ...
                )
            }
    )
}
```

## See Also

- [Dial Basics](/components/dial-basics/) - Learn how to use the Dial component
- [RadiusMode](/reference/radius-mode/) - Understand radius calculation modes
