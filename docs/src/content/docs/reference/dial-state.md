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
    val steps: Int = 0,
    val radiusMode: RadiusMode = RadiusMode.WIDTH,
    var onValueChangeFinished: (() -> Unit)? = null
)
```

## Constructor Parameters

### initialDegree
**Type:** `Float`

The initial rotation angle in degrees.

### degreeRange
**Type:** `ClosedFloatingPointRange<Float>`

The allowed range of rotation. The dial will clamp values to this range.

### steps
**Type:** `Int`
**Default:** `0`

Number of discrete snap points. When `0`, rotation is continuous.

### radiusMode
**Type:** `RadiusMode`
**Default:** `RadiusMode.WIDTH`

How to calculate the radius from the dial's constraints.

### onValueChangeFinished
**Type:** `(() -> Unit)?`
**Default:** `null`

Callback invoked when dragging ends.

## Properties

### degree
**Type:** `Float` (read/write)

The current rotation angle in degrees. This is the primary state value.

```kotlin
// Reading
val currentAngle = state.degree

// Writing (typically done internally)
state.degree = 90f
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

The allowed rotation range, as specified during construction.

```kotlin
val startAngle = state.degreeRange.start
val endAngle = state.degreeRange.endInclusive
val totalSweep = endAngle - startAngle
```

### steps
**Type:** `Int` (read-only)

Number of discrete snap points, as specified during construction.

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
- If `steps == 0`, returns the value clamped to the range (no snapping)
- Otherwise, returns the nearest step position

```kotlin
// With steps = 4 and degreeRange = 0f..360f
// Step positions: 0°, 72°, 144°, 216°, 288°, 360°
state.calculateSnappedValue(100f)  // Returns 72f or 144f (nearest)
```

## Callbacks

### onValueChange
**Type:** `(Float) -> Unit`

Internal callback used by the Dial to notify of value changes. Set to the `onDegreeChanged` parameter.

### onValueChangeFinished
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

                // Use degreeRange for arc bounds
                val startAngle = state.degreeRange.start - 90f
                val sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start

                // Use degree for progress
                val progress = state.degree - state.degreeRange.start

                drawArc(
                    color = Color.Gray,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    // ...
                )

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
