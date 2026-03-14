---
title: Customization
description: Create custom thumb and track designs for your dials.
---

ChromaDial offers two approaches to customization: the simple `DialColors` API for color customization, and fully custom `thumb` and `track` composables for complete control.

## DialColors (Simple API)

For quick color customization without writing custom composables, use `DialColors`:

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    colors = DialColors.default(
        inactiveTrackColor = Zinc700,
        activeTrackColor = Blue500,
        thumbColor = Zinc950,
        thumbStrokeColor = Blue400,
        inactiveTickColor = Zinc700,
        activeTickColor = Blue300,
    ),
)
```

### DialColors Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `inactiveTrackColor` | `Color` | `Zinc700` | Color of the track background |
| `activeTrackColor` | `Color` | `Lime500` | Color of the active/progress arc |
| `thumbColor` | `Color` | `Zinc950` | Fill color of the thumb |
| `thumbStrokeColor` | `Color` | `Lime400` | Stroke color of the thumb |
| `inactiveTickColor` | `Color` | `Zinc700` | Color of tick marks outside active range |
| `activeTickColor` | `Color` | `Lime300` | Color of tick marks within active range |

### Default Track Features

The default track (used with `DialColors`) includes:

- **Progress arc** - Shows the active range from start to current degree
- **Tick marks** - Automatically displayed when `interval > 0`
- **Multi-ring display** - When `sweepDegrees > 360`, completed rotations scale outward with animated transitions and decreasing alpha
- **Overshoot animation** - The active arc visually extends when the user drags beyond the limits

## Custom Composables (Advanced API)

For full control, use custom `thumb` and `track` composables. Both receive a `DialState` object that provides all the information needed to create rich, interactive designs.

## Custom Thumb

The thumb is the draggable handle that users interact with. It's positioned and rotated automatically by the Dial.

### Basic Custom Thumb

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    thumb = { state ->
        Box(
            Modifier
                .size(32.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Blue500, Cyan400)
                    ),
                    shape = CircleShape
                )
                .border(2.dp, White, CircleShape)
        )
    },
)
```

### Invisible Thumb (Track-Only Dial)

For designs where the track shows all the visual feedback:

```kotlin
thumb = { Box(Modifier.fillMaxSize()) }
```

## Custom Track

The track is the background that shows the dial's path and can display progress, tick marks, or any custom graphics.

### Simple Arc Track

Use the library's `drawArc` helper (from `DrawArc.kt`) for correctly positioned arcs. It treats `0°` as 12 o'clock and the `radius` parameter as the outer edge of the stroke:

```kotlin
track = { state ->
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                val sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start

                // Background arc
                drawArc(
                    color = Zinc700,
                    startAngle = state.startDegrees,
                    sweepAngle = sweepAngle,
                    radius = state.radius,
                    strokeWidth = 8.dp,
                )

                // Progress arc
                drawArc(
                    color = Blue500,
                    startAngle = state.startDegrees,
                    sweepAngle = state.degree,
                    radius = state.radius,
                    strokeWidth = 8.dp,
                )
            }
    )
}
```

### Track with Tick Marks

Use the `drawEveryInterval` utility to draw tick marks at regular angular positions. When passing a `DialState`, use the `spacing` parameter:

```kotlin
track = { state ->
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawEveryInterval(
                    dialState = state,
                    spacing = 30f,   // degrees between each tick mark
                ) { data ->
                    rotate(data.rotationAngle, pivot = data.position) {
                        drawLine(
                            color = if (data.inActiveRange) Blue500 else Zinc500,
                            start = data.position,
                            end = data.position + Offset(0f, 15f),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
            }
    )
}
```

With explicit parameters, you can also pass a custom `center` offset (defaults to the `DrawScope`'s center):

```kotlin
drawEveryInterval(
    startDegrees = 180f,
    sweepDegrees = 275f,
    radius = state.radius,
    spacing = 30f,
    center = this.center,  // optional, defaults to DrawScope center
    currentDegree = state.degree,
) { data -> /* ... */ }
```

### Track with Value Display

```kotlin
track = { state ->
    Box(Modifier.fillMaxSize()) {
        Text(
            text = "${(state.value * 100).toInt()}%",
            modifier = Modifier.align(Alignment.Center),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
```

## Using DialState

Both `thumb` and `track` receive a `DialState` object with these useful properties:

| Property | Type | Description |
|----------|------|-------------|
| `degree` | `Float` | Current rotation in degrees (relative to start) |
| `value` | `Float` | Normalized 0-1 value based on position within range |
| `mappedValue` | `Float` | `value` mapped to `valueRange` |
| `degreeRange` | `ClosedFloatingPointRange<Float>` | Allowed rotation range |
| `startDegrees` | `Float` | Visual start angle in screen coordinates |
| `radius` | `Float` | Calculated radius in pixels |
| `thumbSize` | `Float` | Measured thumb size in pixels |
| `overshootDegrees` | `Float` | Decay-adjusted overshoot when dragging beyond limits |

### Using the value Property

The `value` property normalizes the current degree to a 0-1 range:

```kotlin
track = { state ->
    val percentage = (state.value * 100).toInt()
}
```

### Using overshootDegrees

React to the user dragging beyond the dial's limits:

```kotlin
thumb = { state ->
    val scale = 1f - (state.overshootDegrees.absoluteValue / 180f).coerceIn(0f, 0.3f)
    Box(
        Modifier
            .size(32.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(Blue500, CircleShape)
    )
}
```

## IntervalData

When using `drawEveryInterval` or `DialInterval`, you receive an `IntervalData` object with:

| Property | Type | Description |
|----------|------|-------------|
| `index` | `Int` | The index of this interval (0-based) |
| `position` | `Offset` | Pixel position on the dial path |
| `rotationAngle` | `Float` | Tangent angle in degrees at this position (use for rotating content to align with the arc) |
| `intervalDegree` | `Float` | Actual degree value on the dial (in 0..sweepDegrees space) |
| `inActiveRange` | `Boolean` | Whether within the active range |
| `progress` | `Float` | Normalized progress (0-1) within the range |

## DialInterval Composable

For placing composables at interval positions around the dial, use `DialInterval`. Pass the `DialState` directly via the `state` overload:

```kotlin
track = { state ->
    DialInterval(
        state = state,
        modifier = Modifier.fillMaxSize(),
        spacing = 30f,
        currentDegree = state.degree,
    ) { data ->
        Text(
            text = "${data.intervalDegree.toInt()}°",
            color = if (data.inActiveRange) Blue500 else Zinc500,
            fontSize = 12.sp
        )
    }
}
```

Or use explicit parameters without a `DialState`:

```kotlin
DialInterval(
    startDegrees = 180f,
    sweepDegrees = 275f,
    radius = state.radius,   // null = use layout width
    spacing = 30f,
    currentDegree = state.degree,
) { data ->
    Text("${data.index}")
}
```

## Animation Integration

Combine with Compose animations for smooth interactions:

```kotlin
var degree by remember { mutableFloatStateOf(0f) }
val animatedDegree by animateFloatAsState(
    targetValue = degree,
    animationSpec = spring(
        stiffness = Spring.StiffnessHigh,
        dampingRatio = Spring.DampingRatioLowBouncy,
    )
)

Dial(
    degree = animatedDegree,
    onDegreeChange = { degree = it },
    interval = 30f,
)
```

Or use `DialState.animateTo()` for programmatic animation:

```kotlin
val state = rememberDialState(sweepDegrees = 360f)
val scope = rememberCoroutineScope()

Dial(state = state)
Button(onClick = { scope.launch { state.animateTo(0f) } }) {
    Text("Reset")
}
```

## Complete Example: Gradient Arc Dial

```kotlin
@Composable
fun GradientArcDial() {
    var degree by remember { mutableFloatStateOf(0f) }

    Dial(
        degree = degree,
        onDegreeChange = { degree = it },
        modifier = Modifier.size(200.dp, 100.dp),
        startDegrees = 270f,
        sweepDegrees = 180f,
        radiusMode = RadiusMode.HEIGHT,
        thumb = { state ->
            Box(
                Modifier
                    .size(24.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(White, Blue500)
                        ),
                        shape = CircleShape
                    )
                    .border(2.dp, White, CircleShape)
            )
        },
        track = { state ->
            Box(
                Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start

                        // Background
                        drawArc(
                            color = Zinc700,
                            startAngle = state.startDegrees,
                            sweepAngle = sweepAngle,
                            radius = state.radius,
                            strokeWidth = 16.dp,
                        )

                        // Progress with gradient brush
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(Cyan400, Blue500, Violet500)
                            ),
                            startAngle = state.startDegrees,
                            sweepAngle = state.degree,
                            radius = state.radius,
                            strokeWidth = 16.dp,
                        )
                    }
            ) {
                Text(
                    text = "${(state.value * 100).toInt()}%",
                    modifier = Modifier.align(Alignment.BottomCenter),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
```

## Next Steps

- [DialColors Reference](/reference/dial-colors/) - Complete colors API documentation
- [DialState Reference](/reference/dial-state/) - Complete state API documentation
- [RadiusMode Reference](/reference/radius-mode/) - Understand radius calculation
