---
title: Custom Thumb & Track
description: Build fully custom thumb and track composables for complete visual control.
---

When `DialColors` isn't enough, the `Dial` composable accepts `thumb` and `track` slots — regular `@Composable` lambdas that replace the defaults entirely. Both receive a `DialState` with everything they need to draw the dial correctly.

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    thumb = { state -> MyThumb(state) },
    track = { state -> MyTrack(state) },
)
```

## Custom thumb

The thumb is the draggable handle. The `Dial` positions and rotates it automatically — you just describe how it looks.

```kotlin
thumb = { state ->
    Box(
        Modifier
            .size(32.dp)
            .background(
                brush = Brush.verticalGradient(listOf(Blue500, Cyan400)),
                shape = CircleShape,
            )
            .border(2.dp, White, CircleShape)
    )
}
```

### Invisible thumb

For designs where the track carries all the visual feedback, make the thumb invisible but keep it interactive:

```kotlin
thumb = { Box(Modifier.fillMaxSize()) }
```

<video src="/custom_invisible_thumb.webm" autoplay loop muted playsinline></video>

### Reacting to overshoot

`state.overshootDegrees` is non-zero while the user drags past the limits. Use it to add physical feedback:

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

<video src="/custom_overshoot.webm" autoplay loop muted playsinline></video>

## Custom track

The track composable fills the dial's layout bounds. Use it to draw the arc, tick marks, labels, or any other background graphics.

### Arc track

Use the library's `drawArc` extension (0° = 12 o'clock) to draw stroke arcs. The `radius` parameter is the **outer edge** of the stroke:

```kotlin
track = { state ->
    val sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start

    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
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

### Gradient arc

Pass a `Brush` instead of a `Color` for gradient arcs:

```kotlin
drawArc(
    brush = Brush.sweepGradient(listOf(Cyan400, Blue500, Violet500)),
    startAngle = state.startDegrees,
    sweepAngle = state.degree,
    radius = state.radius,
    strokeWidth = 16.dp,
)
```

### Tick marks

`drawEveryInterval` places a draw callback at regular angular positions. By default
(`orientation = IntervalOrientation.PositionAndRotate`) the canvas origin is moved to each interval
and rotated to the tangent, so you draw relative to `Offset.Zero` with `+y` pointing radially inward:

```kotlin
track = { state ->
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawEveryInterval(
                    interval = 30f,
                    startDegrees = state.startDegrees,
                    sweepDegrees = state.degreeRange.endInclusive - state.degreeRange.start,
                    center = state.center,
                    radius = state.radius,
                    currentDegree = state.degree,
                ) { data ->
                    drawLine(
                        color = if (data.inActiveRange) Blue500 else Zinc500,
                        start = Offset.Zero,          // the interval position
                        end = Offset(0f, 15f),        // +y = inward
                        strokeWidth = 2.dp.toPx(),
                    )
                }
            }
    )
}
```

<video src="/custom_tick_marks.webm" autoplay loop muted playsinline></video>

`interval` is the only required parameter — the rest default to a full 360° circle centered in the
`DrawScope`.

#### Orientation

The `orientation` parameter controls how the canvas is transformed at each interval:

| Value | Effect | Draw at |
|-------|--------|---------|
| `IntervalOrientation.PositionAndRotate` *(default)* | Origin at the interval, rotated to the tangent (`+y` inward) — content follows the arc | `Offset.Zero` |
| `IntervalOrientation.PositionOnly` | Origin at the interval, axes stay screen-aligned — content stays upright | `Offset.Zero` |
| `IntervalOrientation.None` | No transform — position content yourself | `data.position` |

Use `PositionOnly` for content that must stay readable (e.g. numeric labels) instead of tilting with
the arc, and `None` when you compute your own polar geometry from `data.intervalDegree`:

```kotlin
drawEveryInterval(
    interval = 30f,
    startDegrees = 180f,
    sweepDegrees = 275f,
    radius = state.radius,
    currentDegree = state.degree,
    orientation = IntervalOrientation.None,
) { data ->
    // place content yourself using data.position / data.intervalDegree
}
```

### Value display

The track composable is a regular `@Composable`, so you can place any content inside it:

```kotlin
track = { state ->
    Box(Modifier.fillMaxSize()) {
        Text(
            text = "${(state.value * 100).toInt()}%",
            modifier = Modifier.align(Alignment.Center),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
```

## DialInterval — composable labels

For placing composables (not just drawing) at interval positions, use `DialInterval`:

```kotlin
track = { state ->
    DialInterval(
        state = state,
        modifier = Modifier.fillMaxSize(),
        interval = 30f,
        currentDegree = state.degree,
        orientation = IntervalOrientation.PositionOnly,   // keep labels upright
    ) { data ->
        Text(
            text = "${data.intervalDegree.toInt()}°",
            color = if (data.inActiveRange) Blue500 else Zinc500,
            fontSize = 12.sp,
        )
    }
}
```

`DialInterval` takes the same `orientation` parameter as `drawEveryInterval`. It defaults to
`IntervalOrientation.PositionAndRotate` (content rotates to follow the arc); use
`IntervalOrientation.PositionOnly` to keep labels upright, or `IntervalOrientation.None` to place
content yourself via `data.position`.

Or with explicit parameters:

```kotlin
DialInterval(
    startDegrees = 180f,
    sweepDegrees = 275f,
    radius = state.radius,   // null = use layout width
    interval = 30f,
    currentDegree = state.degree,
) { data ->
    Text("${data.index}")
}
```

## IntervalData

Both `drawEveryInterval` and `DialInterval` provide an `IntervalData` at each position:

| Property | Type | Description |
|----------|------|-------------|
| `index` | `Int` | 0-based interval index |
| `position` | `Offset` | Pixel position on the arc |
| `rotationAngle` | `Float` | Tangent angle — rotate by this to align content with the arc (then `+y` = inward) |
| `intervalDegree` | `Float` | Degree value at this position (0..sweepDegrees space) |
| `inActiveRange` | `Boolean` | Whether within the active range |
| `progress` | `Float` | Normalized 0-1 progress within the total range |

## DialState properties for custom composables

Both slots receive the same `DialState`. These are the most useful properties for drawing:

| Property | Description |
|----------|-------------|
| `degree` | Current rotation in degrees (0 to sweepDegrees) |
| `value` | Normalized 0–1 position |
| `mappedValue` | `value` mapped to `valueRange` |
| `startDegrees` | Screen angle where the arc begins |
| `absoluteDegree` | On-screen angle of the thumb (`startDegrees ± degree`) |
| `radius` | Radius in pixels (outer edge of the arc stroke) |
| `center` | Dial center in pixels (respects `layout.center`) |
| `degreeRange` | Full allowed range — use `.endInclusive` for `sweepAngle` |
| `overshootDegrees` | Decay-adjusted overshoot during out-of-bounds drag |

## Complete example: gradient arc dial

<video src="/custom_gradient.webm" autoplay loop muted playsinline></video>

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
        layout = DialLayout.height(),
        thumb = { state ->
            Box(
                Modifier
                    .size(24.dp)
                    .background(
                        brush = Brush.radialGradient(listOf(White, Blue500)),
                        shape = CircleShape,
                    )
                    .border(2.dp, White, CircleShape)
            )
        },
        track = { state ->
            val sweepAngle = state.degreeRange.endInclusive - state.degreeRange.start

            Box(
                Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawArc(
                            color = Zinc700,
                            startAngle = state.startDegrees,
                            sweepAngle = sweepAngle,
                            radius = state.radius,
                            strokeWidth = 16.dp,
                        )

                        drawArc(
                            brush = Brush.sweepGradient(listOf(Cyan400, Blue500, Violet500)),
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
                    fontWeight = FontWeight.Bold,
                )
            }
        },
    )
}
```

## See also

- [Default Dial Colors](/components/dial-colors/) — color-only customization without custom composables
- [DialState Reference](/reference/dial-state/) — all state properties and methods
- [DialLayout Reference](/reference/dial-layout/) — radius, fraction, and center positioning
