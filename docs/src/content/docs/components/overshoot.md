---
title: Overshoot
description: Configure the rubber-band effect when the dial is dragged beyond its limits.
---

When a user drags past the start or end of the dial's range, it doesn't hard-stop — it stretches slightly and springs back. This gives it a physical, rubber-band feel.

<video src="/state_overshoot.webm" autoplay loop muted playsinline></video>

## Configuring overshoot

Two parameters control the behavior:

### `overshootDecay`
**Type:** `Float` — **Default:** `0.5f`

Controls how strongly the drag is dampened beyond the limits. At `0f`, the overshoot follows the drag linearly. At `1f`, there's no visible overshoot at all.

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    overshootDecay = 0.3f,   // more visible stretch
)
```

### `overshootAnimationSpec`
**Type:** `AnimationSpec<Float>` — **Default:** `spring()`

The animation used to snap back to the limit after the user releases. Swap in a different spec for a bouncier or snappier return:

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    overshootAnimationSpec = spring(
        stiffness = Spring.StiffnessMedium,
        dampingRatio = Spring.DampingRatioMediumBouncy,
    ),
)
```

## Reacting to overshoot in custom composables

When using [custom thumb or track composables](/components/customization/), `DialState.overshootDegrees` tells you how far the user has stretched past the limit:

- `0f` — within the normal range (or after spring-back)
- **Negative** — dragged below the start
- **Positive** — dragged past the end

Use it to add visual feedback that mirrors the stretch:

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

The value is already decay-adjusted, so you can use it directly without additional dampening math.
