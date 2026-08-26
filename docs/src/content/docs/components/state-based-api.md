---
title: State-based API
description: Manage dial state externally for programmatic control and animation.
---

The stateless `Dial` overloads (`degree` + `onDegreeChange`) are great for simple cases. When you need to read state properties from outside the composable, or drive the dial programmatically, use `rememberDialState` instead.

## Creating state

`rememberDialState` only holds the dial's position. All configuration lives on the `Dial`
composable, which applies it onto the state in place — so changing config never recreates the
state or resets the current `degree`.

```kotlin
val state = rememberDialState(initialDegree = 0f)
```

Pass the state to either `Dial` overload, alongside the configuration:

```kotlin
// With default colors
Dial(
    state = state,
    startDegrees = 180f,
    sweepDegrees = 275f,
    interval = 15f,
    valueRange = 0f..100f,
    colors = DialColors.default(),
)

// With custom composables
Dial(
    state = state,
    startDegrees = 180f,
    sweepDegrees = 275f,
    thumb = { s -> CustomThumb(s) },
    track = { s -> CustomTrack(s) },
)
```

Dragging the dial writes `state.degree` directly, so you can read `state.value` /
`state.mappedValue` back anywhere. If you also want a callback, pass `onDegreeChange`.

## Programmatic animation

<video src="/basics_programmatic.webm" autoplay loop muted playsinline></video>

`state.animateTo()` is a suspend function that smoothly moves the thumb to any target degree:

```kotlin
val state = rememberDialState()
val scope = rememberCoroutineScope()

Dial(state = state, sweepDegrees = 360f)

Button(onClick = { scope.launch { state.animateTo(0f) } }) {
    Text("Reset")
}
```

You can pass a custom `animationSpec`:

```kotlin
state.animateTo(
    targetDegree = 180f,
    animationSpec = tween(durationMillis = 600, easing = EaseInOutCubic),
)
```

## Reading state outside the dial

Because `state` is held in your composable scope, you can read any of its properties anywhere — not just inside `thumb` or `track`:

```kotlin
val state = rememberDialState()

Dial(state = state, sweepDegrees = 360f, valueRange = 0f..100f)

Text("Current value: ${state.mappedValue.toInt()}")
```

## `rememberDialState` parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| `initialDegree` | `0f` | Starting rotation angle (clamped to the sweep once `Dial` applies its config) |

Everything else is configured on the `Dial` composable, not on the state:

| Parameter | Default | Description |
|-----------|---------|-------------|
| `sweepDegrees` | `360f` | Total rotatable range |
| `startDegrees` | `0f` | Screen angle where `degree = 0` |
| `interval` | `0f` | Degrees between snap points (`0` = continuous) |
| `steps` | `0` | Number of snap stops (overrides `interval` when `> 0`) |
| `layout` | `DialLayout()` | How radius/center are derived from constraints |
| `valueRange` | `0f..1f` | Range that `mappedValue` maps to |
| `clockwise` | `true` | Rotation direction |
| `enabled` | `true` | Whether drag input is accepted |
| `onDegreeChange` | `null` | Called with the new degree as the dial is dragged |
| `onDegreeChangeFinished` | `null` | Called when the user releases the dial |

See [DialState reference](/reference/dial-state/) for a full description of all state properties.
