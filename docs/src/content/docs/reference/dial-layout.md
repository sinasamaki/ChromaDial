---
title: DialLayout
description: Control how the dial's radius and center are derived from the available space.
---

`DialLayout` decides **how big** the dial is and **where its center sits** inside the space the
composable is given. Pass it to any `Dial` overload via the `layout` parameter, or read it back from
[`DialState.layout`](/reference/dial-state/).

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(200.dp, 110.dp),
    layout = DialLayout.height(),
)
```

## Definition

```kotlin
data class DialLayout(
    val radiusMode: RadiusMode = RadiusMode.WIDTH,
    val radiusFraction: Float = 0.5f,
    val center: Offset = Offset(0.5f, 0.5f),
)
```

| Parameter | Default | Description |
|-----------|---------|-------------|
| `radiusMode` | `RadiusMode.WIDTH` | Which dimension the radius is measured against — see [RadiusMode](/reference/radius-mode/) |
| `radiusFraction` | `0.5f` | Radius as a fraction of that dimension. `0.5` = half (touches the edge); smaller values leave breathing room for ticks or labels |
| `center` | `Offset(0.5f, 0.5f)` | Center of the dial as a fraction of `(width, height)`. `(0.5, 0.5)` is the middle; `(0.5, 1.0)` sits on the bottom edge |

The radius in pixels works out to `radiusFraction × (width or height)`, and is exposed as
[`DialState.radius`](/reference/dial-state/). The pixel center is exposed as `DialState.center`.

## Factory helpers

For the two common cases you don't need the full constructor:

```kotlin
DialLayout.width(fraction = 0.5f, center = Offset(0.5f, 0.5f))   // radius from width
DialLayout.height(fraction = 0.5f, center = Offset(0.5f, 0.5f))  // radius from height
```

## Recipes

### Full circle (default)

A square dial fills its bounds with a centered circle — nothing to configure.

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(200.dp),
    // layout = DialLayout()  ← the default
)
```

### Horizontal semi-circle

For a half-arc in a wide, short box, measure the radius from the height so the arc stays flush with
the top and bottom edges instead of overflowing.

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(200.dp, 100.dp),
    startDegrees = 270f,
    sweepDegrees = 180f,
    layout = DialLayout.height(),
)
```

### Bottom-anchored gauge

<video src="/layout_gauge.webm" autoplay loop muted playsinline></video>

To pin the flat edge of a gauge to the bottom, drop the center onto the bottom edge and let the
radius fill the **whole** height (`radiusFraction = 1f`):

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(220.dp, 120.dp),
    startDegrees = 270f,
    sweepDegrees = 180f,
    layout = DialLayout(
        radiusMode = RadiusMode.HEIGHT,
        radiusFraction = 1f,
        center = Offset(0.5f, 1f),
    ),
)
```

### Leaving room for ticks or labels

When you draw ticks or text outside the track, shrink the radius so they don't clip against the
edges:

```kotlin
layout = DialLayout.width(fraction = 0.4f)   // arc occupies the inner 80% of the box
```

## See also

- [RadiusMode](/reference/radius-mode/) — the `WIDTH` / `HEIGHT` enum used by `radiusMode`
- [DialState](/reference/dial-state/) — exposes the resolved `radius` and `center` in pixels
- [Common Patterns](/components/common-patterns/) — semi-circle and multi-rotation layouts in context
