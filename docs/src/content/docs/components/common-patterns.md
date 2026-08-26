---
title: Common Patterns
description: Practical examples for common dial configurations.
---

## Full Circle Dial

<video src="/basics_full_circle.webm" autoplay loop muted playsinline></video>

A complete 360° dial starting at 12 o'clock. The thumb can travel all the way around.

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

## Semi-Circle

<video src="/basics_semi_circle.webm" autoplay loop muted playsinline></video>

A half-arc stretched horizontally across the top. Use `DialLayout.height()` so the radius is based on the component height rather than width, keeping the arc flush with the edges.

```kotlin
var degree by remember { mutableFloatStateOf(90f) }

Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(200.dp, 100.dp),
    startDegrees = 270f,
    sweepDegrees = 180f,
    layout = DialLayout.height(),
)
```

For a true bottom-anchored gauge (the flat edge along the bottom), move the center down and let the radius fill the full height — see [DialLayout](/reference/dial-layout/):

```kotlin
layout = DialLayout(
    radiusMode = RadiusMode.HEIGHT,
    radiusFraction = 1f,
    center = Offset(0.5f, 1f),   // center on the bottom edge
)
```

## Stepped Selector

<video src="/basics_stepped.webm" autoplay loop muted playsinline></video>

Discrete stops like a camera mode dial. Combine `interval` with `animateFloatAsState` so the thumb visually snaps to each position.

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

## Multi-Rotation

<video src="/basics_multi_rotation.webm" autoplay loop muted playsinline></video>

When `sweepDegrees` exceeds 360°, the default track stacks completed rotations as fading outer rings. Useful for timers and fine-grained controls.

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

## Programmatic Animation

<video src="/basics_programmatic.webm" autoplay loop muted playsinline></video>

Use [State-based API](/components/state-based-api/) to drive the dial from code with `state.animateTo()`.

```kotlin
val state = rememberDialState()
val scope = rememberCoroutineScope()

Dial(state = state, sweepDegrees = 360f)

Button(onClick = { scope.launch { state.animateTo(180f) } }) {
    Text("Go to halfway")
}
```
