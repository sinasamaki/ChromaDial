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
    onDegreeChange = { degree = it },
    modifier = Modifier.size(200.dp),
    startDegrees = 0f,
    sweepDegrees = 360f,
)
```

## API Variants

ChromaDial provides four overloaded versions of the `Dial` composable.

### Simple API (with DialColors)

Use this when you want to customize colors while using the default thumb and track:

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    startDegrees = 270f,
    sweepDegrees = 180f,
    colors = DialColors.default(
        activeTrackColor = Blue500,
        thumbStrokeColor = Blue400,
    ),
)
```

### Custom API (with thumb/track composables)

Use this when you need full control over the thumb and track appearance:

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    startDegrees = 270f,
    sweepDegrees = 180f,
    thumb = { state -> /* custom thumb */ },
    track = { state -> /* custom track */ },
)
```

### State-based API

Use `rememberDialState` to manage state externally, then pass it to a state-based `Dial` overload. This is useful when you need to call `state.animateTo()` or read state properties outside the thumb/track composables.

```kotlin
val state = rememberDialState(
    initialDegree = 0f,
    sweepDegrees = 275f,
    startDegrees = 180f,
    interval = 15f,
    valueRange = 0f..100f,
)
val scope = rememberCoroutineScope()

// Simple colors variant
Dial(state = state, colors = DialColors.default())

// Or custom composables variant
Dial(
    state = state,
    thumb = { s -> CustomThumb(s) },
    track = { s -> CustomTrack(s) },
)

// Programmatic animation
Button(onClick = { scope.launch { state.animateTo(0f) } }) {
    Text("Reset")
}
```

## Parameters

### degree
**Type:** `Float`

The current rotation angle of the dial in degrees, **relative to `startDegrees`**. This value ranges from `0` to `sweepDegrees`.

- `0f` is at the start position (determined by `startDegrees`)
- `sweepDegrees` is at the end position
- Intermediate values represent positions along the arc

### onDegreeChange
**Type:** `(Float) -> Unit`

Callback invoked when the user drags the dial. Update your state in this callback:

```kotlin
onDegreeChange = { newDegree -> degree = newDegree }
```

### modifier
**Type:** `Modifier`
**Default:** `Modifier`

Standard Compose modifier for sizing and positioning the dial.

```kotlin
modifier = Modifier.size(200.dp)         // Square dial
modifier = Modifier.size(200.dp, 100.dp) // Semi-circle
```

### startDegrees
**Type:** `Float`
**Default:** `0f`

The starting angle of the arc in absolute screen coordinates.

- `0f` - Top (12 o'clock)
- `90f` - Right (3 o'clock)
- `180f` - Bottom (6 o'clock)
- `270f` - Left (9 o'clock)

When `degree = 0f`, the thumb is at `startDegrees`.

### sweepDegrees
**Type:** `Float`
**Default:** `360f`

How many degrees the dial can sweep from the start position. The `degree` parameter ranges from `0` to `sweepDegrees`. Can exceed 360 for multi-rotation dials.

### interval
**Type:** `Float`
**Default:** `0f`

The degree interval between snap points. When `0f`, rotation is continuous. When set to `15f`, the dial snaps every 15 degrees.

```kotlin
interval = 0f   // Continuous rotation
interval = 15f  // Snap every 15 degrees
interval = 30f  // Snap every 30 degrees
```

The end of the range is always a valid snap point.

### steps
**Type:** `Int`
**Default:** `0`

Number of evenly-spaced snap steps. When `> 0`, overrides `interval` by computing `sweepDegrees / steps`. Useful when you know how many discrete positions you need rather than the degree spacing.

```kotlin
steps = 12  // 12 evenly spaced positions across the sweep
```

### valueRange
**Type:** `ClosedFloatingPointRange<Float>`
**Default:** `0f..1f`

Maps `DialState.value` to a custom range via `DialState.mappedValue`. Useful for working in natural units:

```kotlin
valueRange = 0f..100f   // mappedValue gives 0–100
valueRange = 0f..11f    // Goes to 11
```

### clockwise
**Type:** `Boolean`
**Default:** `true`

When `false`, the dial rotates counterclockwise as the user drags.

### enabled
**Type:** `Boolean`
**Default:** `true`

When `false`, the dial ignores drag input and displays without the hand cursor.

### overshootDecay
**Type:** `Float`
**Default:** `0.5f`

Controls how strongly the overshoot is dampened when the user drags beyond the limits. `0f` = no dampening; `1f` = no visible overshoot.

### overshootAnimationSpec
**Type:** `AnimationSpec<Float>`
**Default:** `spring()`

The animation used to spring back after an overshoot.

### colors
**Type:** `DialColors`
**Default:** `DialColors.default()`
*(Simple API only)*

Customize the appearance of the default thumb and track.

### thumb
*(Custom API only)*
**Type:** `@Composable (DialState) -> Unit`

Custom composable for the draggable handle.

### track
*(Custom API only)*
**Type:** `@Composable (DialState) -> Unit`

Custom composable for the background track.

### interactionSource
**Type:** `MutableInteractionSource`
**Default:** `remember { MutableInteractionSource() }`

Allows observing hover and drag states:

```kotlin
val interactionSource = remember { MutableInteractionSource() }
val isDragging by interactionSource.collectIsDraggedAsState()

Dial(interactionSource = interactionSource, /* ... */)
```

### onDegreeChangeFinished
**Type:** `(() -> Unit)?`
**Default:** `null`

Callback invoked when the user finishes dragging (lifts finger / releases mouse).

## Common Patterns

### Full Circle Dial

```kotlin
var degree by remember { mutableFloatStateOf(0f) }

Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(200.dp),
    startDegrees = 0f,
    sweepDegrees = 360f,
)
```

### Semi-Circle (Top Arc)

```kotlin
var degree by remember { mutableFloatStateOf(90f) }

Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(200.dp, 100.dp),
    startDegrees = 270f,
    sweepDegrees = 180f,
    radiusMode = RadiusMode.HEIGHT,
)
```

### Stepped Selector (like a camera mode dial)

```kotlin
var degree by remember { mutableFloatStateOf(90f) }
val animatedDegree by animateFloatAsState(degree)

Dial(
    degree = animatedDegree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(200.dp),
    startDegrees = -90f,
    sweepDegrees = 220f,
    interval = 20f,
)
```

### Multi-Rotation (like a timer)

When `sweepDegrees` exceeds 360, the default track displays multiple rings with animated transitions.

```kotlin
val sweepDegrees = 360f * 4
var degree by remember { mutableFloatStateOf(sweepDegrees) }

Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(300.dp),
    sweepDegrees = sweepDegrees,
    interval = 6f,
)
```

### Programmatic Animation

```kotlin
val state = rememberDialState(sweepDegrees = 360f)
val scope = rememberCoroutineScope()

Dial(state = state)

Button(onClick = { scope.launch { state.animateTo(180f) } }) {
    Text("Go to halfway")
}
```

## Next Steps

- [Customization](/components/customization/) - Create custom thumb and track designs
- [DialColors Reference](/reference/dial-colors/) - Complete colors API documentation
- [DialState Reference](/reference/dial-state/) - Understand the state object
