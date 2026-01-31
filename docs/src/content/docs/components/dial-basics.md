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

### Simple API (with DialColors)

Use this when you want to customize colors while using the default thumb and track:

```kotlin
Dial(
    degree = degree,
    onDegreeChanged = { degree = it },
    startDegrees = 270f,
    sweepDegrees = 180f,
    colors = DialColors.default(
        activeTrackColor = Color.Blue,
        thumbStrokeColor = Color.Blue,
    ),
)
```

### Custom API (with thumb/track composables)

Use this when you need full control over the thumb and track appearance:

```kotlin
Dial(
    degree = degree,
    onDegreeChanged = { degree = it },
    startDegrees = 270f,
    sweepDegrees = 180f,
    thumb = { state -> /* custom thumb */ },
    track = { state -> /* custom track */ },
)
```

## Parameters

### degree
**Type:** `Float`

The current rotation angle of the dial in degrees, **relative to `startDegrees`**. This value ranges from `0` to `sweepDegrees`.

- `0f` is at the start position (determined by `startDegrees`)
- `sweepDegrees` is at the end position
- Intermediate values represent positions along the arc

```kotlin
// For a dial with startDegrees=0f, sweepDegrees=360f:
var degree by remember { mutableFloatStateOf(90f) }  // 90° into the range (3 o'clock)

// For a dial with startDegrees=270f, sweepDegrees=180f (top semi-circle):
var degree by remember { mutableFloatStateOf(90f) }  // Midpoint of the arc
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

The starting angle of the arc in absolute screen coordinates. This determines where the dial's track begins visually.

- `0f` - Top (12 o'clock)
- `90f` - Right (3 o'clock)
- `180f` - Bottom (6 o'clock)
- `270f` - Left (9 o'clock)

```kotlin
startDegrees = 270f  // Arc starts at 9 o'clock
```

**Note:** The `degree` parameter is always relative to this starting position. When `degree = 0f`, the thumb is at `startDegrees`.

### sweepDegrees
**Type:** `Float`

How many degrees the dial can sweep from the start position. The `degree` parameter ranges from `0` to `sweepDegrees`.

```kotlin
startDegrees = 270f,
sweepDegrees = 180f,  // Creates a top semi-circle
// degree ranges from 0f (at 9 o'clock) to 180f (at 3 o'clock)
```

### colors
**Type:** `DialColors`
**Default:** `DialColors.default()`

Customize the appearance of the default thumb and track. Only available with the simple API (not with custom `thumb`/`track` composables).

```kotlin
colors = DialColors.default(
    inactiveTrackColor = Color.Gray,
    activeTrackColor = Color.Blue,
    thumbColor = Color.White,
    thumbStrokeColor = Color.Blue,
    inactiveTickColor = Color.Gray,
    activeTickColor = Color.Blue,
)
```

The default track automatically displays tick marks when `interval > 0`, using the tick colors from `DialColors`.

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

### interval
**Type:** `Float`
**Default:** `0f`

The degree interval between snap points. When `0f`, the dial rotates continuously (smooth rotation). When set to a value like `15f`, the dial will snap to positions every 15 degrees.

```kotlin
interval = 0f   // Continuous rotation (no snapping)
interval = 15f  // Snap every 15 degrees
interval = 30f  // Snap every 30 degrees (like an hour hand on a clock)
```

The end of the range is always a valid snap point, even if the interval doesn't divide evenly into the range. This ensures the dial can always reach its maximum value.

### onDegreeChangeFinished
**Type:** `(() -> Unit)?`
**Default:** `null`

Callback invoked when the user finishes dragging (lifts their finger/releases mouse). Useful for committing changes or triggering actions.

```kotlin
onDegreeChangeFinished = {
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

### thumb (Custom API only)
**Type:** `@Composable (DialState) -> Unit`

Custom composable for the draggable handle. Receives `DialState` with information about the dial's current state. Only available when using the custom API overload.

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

### track (Custom API only)
**Type:** `@Composable (DialState) -> Unit`

Custom composable for the background track. Receives `DialState` to draw progress arcs or tick marks. Only available when using the custom API overload.

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
// degree ranges from 0 to 360
var degree by remember { mutableFloatStateOf(0f) }

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
// degree ranges from 0 to 180
// 0 = left side (9 o'clock), 180 = right side (3 o'clock)
var degree by remember { mutableFloatStateOf(90f) }  // Start at center

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
// degree ranges from 0 to 220
var degree by remember { mutableFloatStateOf(90f) }  // 90° into the range

Dial(
    degree = animatedDegree,  // Use animation for smooth snapping
    onDegreeChanged = { degree = it },
    modifier = Modifier.size(200.dp),
    startDegrees = -90f,
    sweepDegrees = 220f,
    interval = 20f,  // Snap every 20 degrees (11 positions plus the end)
)
```

### Multi-Rotation (like a timer)

When `sweepDegrees` exceeds 360, the default track displays multiple rings. Completed rotations scale outward with animated transitions and decreasing alpha.

```kotlin
val sweepDegrees = 360f * 4  // 4 full rotations
// degree ranges from 0 to 1440 (4 * 360)
var degree by remember { mutableFloatStateOf(sweepDegrees) }  // Start at end

Dial(
    degree = degree,
    onDegreeChanged = { degree = it },
    modifier = Modifier.size(300.dp),
    startDegrees = 0f,
    sweepDegrees = sweepDegrees,
    interval = 6f,  // Snap every 6 degrees (1 minute = 6°)
)
```

### With Custom Colors

```kotlin
var degree by remember { mutableFloatStateOf(0f) }

Dial(
    degree = degree,
    onDegreeChanged = { degree = it },
    modifier = Modifier.size(200.dp),
    startDegrees = 0f,
    sweepDegrees = 360f,
    interval = 30f,  // Show tick marks every 30 degrees
    colors = DialColors.default(
        activeTrackColor = Color.Cyan,
        thumbStrokeColor = Color.Cyan,
        activeTickColor = Color.Cyan,
    ),
)
```

## Next Steps

- [Customization](/components/customization/) - Create custom thumb and track designs
- [DialColors Reference](/reference/dial-colors/) - Complete colors API documentation
- [DialState Reference](/reference/dial-state/) - Understand the state object
