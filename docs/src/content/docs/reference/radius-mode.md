---
title: RadiusMode
description: API reference for the RadiusMode enum.
---

`RadiusMode` selects **which dimension** a [`DialLayout`](/reference/dial-layout/) measures the
dial's radius against. It's the `radiusMode` field of `DialLayout` — you don't pass it to `Dial`
directly.

## Definition

```kotlin
enum class RadiusMode {
    WIDTH,
    HEIGHT
}
```

## Values

### WIDTH

Radius is `radiusFraction × width`. This is the default and the right choice for square dials and
full circles.

```kotlin
layout = DialLayout(radiusMode = RadiusMode.WIDTH)
// or simply: DialLayout.width()
```

### HEIGHT

Radius is `radiusFraction × height`. Use it when the box is wider than it is tall — typically a
horizontal semi-circle or gauge — so the arc is sized by the short axis and stays within bounds.

```kotlin
layout = DialLayout(radiusMode = RadiusMode.HEIGHT)
// or simply: DialLayout.height()
```

## Why it matters

Consider `Modifier.size(200.dp, 100.dp)` with a 180° top arc:

| Mode | Radius | Result |
|------|--------|--------|
| `WIDTH` | 100dp | Arc is sized by width — the thumb path overflows the short height |
| `HEIGHT` | 50dp | Arc is sized by height — the half-circle fits the box |

## See also

- [DialLayout](/reference/dial-layout/) — the full layout type that uses `RadiusMode`, plus
  `radiusFraction` and `center`
- [DialState](/reference/dial-state/) — exposes the resolved `radius` in pixels
