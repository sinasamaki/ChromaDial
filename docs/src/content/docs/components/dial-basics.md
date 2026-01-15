---
title: Dial Basics
description: Learn the fundamentals of the Dial component and its parameters.
---

The `Dial` composable is the core component of ChromaDial. It provides a circular draggable control that users can interact with by dragging around its circumference.

## Basic Usage

```kotlin
var degree by remember { mutableFloatStateOf(0f) }

Dial(
    degree = degree,
    onDegreeChanged = { degree = it },
    modifier = Modifier.size(200.dp),
    startDegrees = 0f,
    sweepDegrees = 360f,
)
```

## API Variants

ChromaDial provides two overloaded versions of the `Dial` composable:

### Simple API (startDegrees + sweepDegrees)

Use this when you want to define an arc starting from a specific angle:

```kotlin
Dial(
    degree = degree,
    onDegreeChanged = { degree = it },
    startDegrees = 270f,    // Start at 9 o'clock position
    sweepDegrees = 180f,    // Sweep 180 degrees (half circle)
)
```

### Advanced API (degreeRange)

Use this for precise control over the allowed rotation range:

```kotlin
Dial(
    degree = degree,
    onDegreeChanged = { degree = it },
    degreeRange = 135f..405f,  // Custom range
)
```

## Parameters

### degree
**Type:** `Float`

The current rotation angle of the dial in degrees. This is the controlled state value that determines where the thumb is positioned.

- `0f` points upward (12 o'clock)
- `90f` points right (3 o'clock)
- `180f` points down (6 o'clock)
- `270f` points left (9 o'clock)

```kotlin
var degree by remember { mutableFloatStateOf(90f) }  // Start at 3 o'clock
```

### onDegreeChanged
**Type:** `(Float) -> Unit`

Callback invoked when the user drags the dial. Update your state in this callback:

```kotlin
onDegreeChanged = { newDegree -> degree = newDegree }
```

### modifier
**Type:** `Modifier`
**Default:** `Modifier`

Standard Compose modifier for sizing and positioning the dial. The dial's radius is calculated from the modifier's constraints.

```kotlin
modifier = Modifier.size(200.dp)  // 200dp diameter dial
```

For non-square dials (like semi-circles), use different width and height:

```kotlin
modifier = Modifier.size(200.dp, 100.dp)  // Semi-circle
```

### startDegrees
**Type:** `Float`

The starting angle of the allowed rotation range. Used with `sweepDegrees`.

- `0f` - Top (12 o'clock)
- `90f` - Right (3 o'clock)
- `180f` - Bottom (6 o'clock)
- `270f` - Left (9 o'clock)

```kotlin
startDegrees = 270f  // Start at 9 o'clock
```

### sweepDegrees
**Type:** `Float`

How many degrees the dial can sweep from the start position. Used with `startDegrees`.

```kotlin
startDegrees = 270f,
sweepDegrees = 180f,  // Creates a top semi-circle (270° to 450°/90°)
```

### degreeRange
**Type:** `ClosedFloatingPointRange<Float>`
**Default:** `0f..360f`

Alternative to `startDegrees`/`sweepDegrees`. Defines the exact range of allowed rotation.

```kotlin
degreeRange = 0f..360f      // Full circle
degreeRange = 135f..405f    // 270 degree arc
degreeRange = -360f..360f   // Two full rotations allowed
```

### radiusMode
**Type:** `RadiusMode`
**Default:** `RadiusMode.WIDTH`

Determines how the dial's radius is calculated from its constraints:

- `RadiusMode.WIDTH` - Radius = half the width
- `RadiusMode.HEIGHT` - Radius = half the height

```kotlin
radiusMode = RadiusMode.HEIGHT  // Use height for radius calculation
```

This is useful for non-square dials where you want the thumb to follow a specific dimension.

### steps
**Type:** `Int`
**Default:** `0`

Number of discrete snap points between the start and end of the range. When `0`, the dial rotates continuously (smooth rotation).

```kotlin
steps = 0   // Continuous rotation (no snapping)
steps = 10  // 11 snap points (start + 10 steps + end = 12 positions)
```

The formula for total positions is: `steps + 2` (includes start and end positions).

### onValueChangeFinished
**Type:** `(() -> Unit)?`
**Default:** `null`

Callback invoked when the user finishes dragging (lifts their finger/releases mouse). Useful for committing changes or triggering actions.

```kotlin
onValueChangeFinished = {
    saveSettings(degree)
}
```

### interactionSource
**Type:** `MutableInteractionSource`
**Default:** `remember { MutableInteractionSource() }`

Allows observing and customizing interactions (drag, hover). Useful for adding visual feedback during interaction.

```kotlin
val interactionSource = remember { MutableInteractionSource() }
val isDragging by interactionSource.collectIsDraggedAsState()

Dial(
    interactionSource = interactionSource,
    // ... other params
)

// isDragging is true while user is dragging
```

### thumb
**Type:** `@Composable (DialState) -> Unit`

Custom composable for the draggable handle. Receives `DialState` with information about the dial's current state.

```kotlin
thumb = { state ->
    Box(
        Modifier
            .size(32.dp)
            .background(Color.Blue, CircleShape)
    )
}
```

See [Customization](/components/customization/) for advanced examples.

### track
**Type:** `@Composable (DialState) -> Unit`

Custom composable for the background track. Receives `DialState` to draw progress arcs or tick marks.

```kotlin
track = { state ->
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawCircle(
                    color = Color.Gray,
                    style = Stroke(width = 8.dp.toPx()),
                    radius = state.radius - 4.dp.toPx()
                )
            }
    )
}
```

## Common Patterns

### Full Circle Dial

```kotlin
Dial(
    degree = degree,
    onDegreeChanged = { degree = it },
    modifier = Modifier.size(200.dp),
    startDegrees = 0f,
    sweepDegrees = 360f,
)
```

### Semi-Circle (Top Arc)

```kotlin
Dial(
    degree = degree,
    onDegreeChanged = { degree = it },
    modifier = Modifier.size(200.dp, 100.dp),
    startDegrees = 270f,
    sweepDegrees = 180f,
)
```

### Stepped Selector (like a camera mode dial)

```kotlin
Dial(
    degree = animatedDegree,  // Use animation for smooth snapping
    onDegreeChanged = { degree = it },
    modifier = Modifier.size(200.dp),
    startDegrees = -90f,
    sweepDegrees = 220f,
    steps = 10,  // 12 selectable positions
)
```

### Multi-Rotation (like a timer)

```kotlin
Dial(
    degree = degree,
    onDegreeChanged = { degree = it },
    modifier = Modifier.size(300.dp),
    startDegrees = -360f * 4,   // Allow 4 counter-clockwise rotations
    sweepDegrees = 360f * 8,    // Total 8 rotations range
    steps = (60 * 4) - 1,       // Snap every minute
)
```

## Next Steps

- [Customization](/components/customization/) - Create custom thumb and track designs
- [DialState Reference](/reference/dial-state/) - Understand the state object
