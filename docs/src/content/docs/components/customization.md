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
    onDegreeChanged = { degree = it },
    colors = DialColors.default(
        inactiveTrackColor = Color.Gray,
        activeTrackColor = Color.Blue,
        thumbColor = Color.White,
        thumbStrokeColor = Color.Blue,
        inactiveTickColor = Color.Gray,
        activeTickColor = Color.Blue,
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

## Custom Composables (Advanced API)

For full control, use custom `thumb` and `track` composables. Both receive a `DialState` object that provides all the information needed to create rich, interactive designs.

## Custom Thumb

The thumb is the draggable handle that users interact with. It's positioned and rotated automatically by the Dial.

### Basic Custom Thumb

```kotlin
Dial(
    degree = degree,
    onDegreeChanged = { degree = it },
    thumb = { state ->
        Box(
            Modifier
                .size(32.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Blue, Color.Cyan)
                    ),
                    shape = CircleShape
                )
                .border(2.dp, Color.White, CircleShape)
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

```kotlin
track = { state ->
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                // Background arc
                drawArc(
                    color = Color.Gray.copy(alpha = 0.3f),
                    startAngle = state.degreeRange.start - 90f,
                    sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start,
                    topLeft = Offset(16.dp.toPx(), 16.dp.toPx()),
                    size = Size(
                        state.radius * 2 - 32.dp.toPx(),
                        state.radius * 2 - 32.dp.toPx()
                    ),
                    useCenter = false,
                    style = Stroke(width = 32.dp.toPx(), cap = StrokeCap.Round)
                )

                // Progress arc
                drawArc(
                    color = Color.Blue,
                    startAngle = state.degreeRange.start - 90f,
                    sweepAngle = state.degree - state.degreeRange.start,
                    topLeft = Offset(16.dp.toPx(), 16.dp.toPx()),
                    size = Size(
                        state.radius * 2 - 32.dp.toPx(),
                        state.radius * 2 - 32.dp.toPx()
                    ),
                    useCenter = false,
                    style = Stroke(width = 32.dp.toPx(), cap = StrokeCap.Round)
                )
            }
    )
}
```

### Track with Tick Marks

Use the `drawEveryInterval` utility to draw tick marks at interval positions:

```kotlin
track = { state ->
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawEveryInterval(
                    dialState = state,
                    interval = 30f,  // Degrees between each tick mark
                    padding = 16.dp,
                ) { data ->
                    rotate(data.degree, pivot = data.position) {
                        drawLine(
                            color = if (data.inActiveRange) Color.Blue else Color.Gray,
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
| `degree` | `Float` | Current rotation in degrees |
| `value` | `Float` | Normalized 0-1 value based on position within range |
| `degreeRange` | `ClosedFloatingPointRange<Float>` | Allowed rotation range |
| `radius` | `Float` | Calculated radius in pixels |
| `thumbSize` | `Float` | Measured thumb size in pixels |
| `overshotAngle` | `Float` | Amount dragged beyond limits (for rubber-band effects) |

### Using the value Property

The `value` property normalizes the current degree to a 0-1 range:

```kotlin
track = { state ->
    // state.value is 0.0 at degreeRange.start
    // state.value is 1.0 at degreeRange.endInclusive
    val percentage = (state.value * 100).toInt()
}
```

### Using overshotAngle

Create rubber-band effects when users drag beyond the limits:

```kotlin
thumb = { state ->
    val scale = 1f - (state.overshotAngle.absoluteValue / 180f).coerceIn(0f, 0.3f)
    Box(
        Modifier
            .size(32.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(Color.Blue, CircleShape)
    )
}
```

## IntervalData

When using `drawEveryInterval` or `DialInterval`, you receive an `IntervalData` object with:

| Property | Type | Description |
|----------|------|-------------|
| `index` | `Int` | The index of this interval (0-based) |
| `position` | `Offset` | Pixel position on the dial path |
| `degree` | `Float` | Tangent angle for rotation |
| `intervalDegree` | `Float` | Actual degree value on the dial |
| `inActiveRange` | `Boolean` | Whether within the active range |
| `progress` | `Float` | Normalized progress (0-1) within the range |

## DialInterval Composable

For placing composables at interval positions around the dial, use `DialInterval`:

```kotlin
track = { state ->
    DialInterval(
        modifier = Modifier.fillMaxSize(),
        degreeRange = state.degreeRange,
        interval = 30f,  // Degrees between each position
    ) { data ->
        Text(
            text = "${data.index}",
            color = if (data.inActiveRange) Color.Blue else Color.Gray,
            fontSize = 12.sp
        )
    }
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
    onDegreeChanged = { degree = it },
    interval = 30f,  // Combine with interval for bouncy snapping
)
```

## Complete Example: Gradient Arc Dial

```kotlin
@Composable
fun GradientArcDial() {
    var degree by remember { mutableFloatStateOf(270f) }

    Dial(
        degree = degree,
        onDegreeChanged = { degree = it },
        modifier = Modifier.size(200.dp, 100.dp),
        startDegrees = 270f,
        sweepDegrees = 180f,
        thumb = { state ->
            Box(
                Modifier
                    .size(24.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, Color.Blue)
                        ),
                        shape = CircleShape
                    )
                    .border(2.dp, Color.White, CircleShape)
            )
        },
        track = { state ->
            Box(
                Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val strokeWidth = 16.dp.toPx()
                        val arcSize = Size(
                            state.radius * 2 - strokeWidth,
                            state.radius * 2 - strokeWidth
                        )

                        // Background
                        drawArc(
                            color = Color.Gray.copy(alpha = 0.2f),
                            startAngle = 180f,
                            sweepAngle = 180f,
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                            size = arcSize,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )

                        // Progress
                        val progress = state.value * 180f
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(Color.Cyan, Color.Blue, Color.Magenta)
                            ),
                            startAngle = 180f,
                            sweepAngle = progress,
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                            size = arcSize,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
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
