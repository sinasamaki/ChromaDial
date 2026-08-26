---
title: Default Dial Colors
description: Customize the look of the default Dial thumb and track using DialColors.
---

The simplest way to style a `Dial` is with `DialColors`. It lets you change colors without writing any custom composables — the default thumb and track handle the rest.

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    interval = 20f,
    colors = DialColors.default(
        activeTrackColor = Blue500,
        thumbStrokeColor = Blue400,
        activeTickColor = Blue300,
    ),
)
```

When you don't pass `colors`, the dial uses `DialColors.default()` with the built-in lime-on-dark palette — the default thumb, track, and tick marks shown here:

<video src="/dial_colors.webm" class="wide" autoplay loop muted playsinline></video>

## Colors

| Property | Default | Controls |
|----------|---------|----------|
| `inactiveTrackColor` | `Zinc700` | The background arc spanning the full sweep |
| `activeTrackColor` | `Lime500` | The filled arc from start to the current degree |
| `thumbColor` | `Zinc950` | Fill of the circular thumb |
| `thumbStrokeColor` | `Lime400` | Border of the thumb |
| `inactiveTickColor` | `Zinc700` | Tick marks outside the active range |
| `activeTickColor` | `Lime300` | Tick marks within the active range |

Tick marks are only drawn when `interval > 0` or `steps > 0`.

## Default thumb

The built-in thumb is a 24dp circle — a 16dp filled inner circle inside a 4dp stroke ring.

## Default track

The default track draws a 4dp stroke arc with rounded caps. Beyond colors, it handles a few behaviors automatically:

**Tick marks** — drawn at each snap point when `interval > 0`.

**Overshoot feedback** — the active arc stretches slightly when the user drags past the limits. See [Overshoot](/components/overshoot/) for how to configure this.

**Multi-ring display** — when `sweepDegrees > 360`, each completed rotation is shown as a fading outer ring. Rings scale outward, decrease in opacity, and animate in and out as you cross the boundary.

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    sweepDegrees = 360f * 3,  // 3 full rotations
    interval = 30f,
    colors = DialColors.default(),
)
```

## Need more control?

`DialColors` is the right tool when you want to change colors. When you need to change the shape, layout, or behavior of the thumb or track, switch to [Custom Thumb & Track](/components/customization/).
