---
title: Responding to Input
description: React to drag, hover, and release events from the Dial.
---

<video src="/input_interaction.webm" autoplay loop muted playsinline></video>

The `Dial` reports interaction through standard Compose channels: a `MutableInteractionSource` for drag/hover state, `onDegreeChange` for live values, and `onDegreeChangeFinished` for the release.

## Detecting drag and hover

Pass a `MutableInteractionSource` to observe the dial's interaction state:

```kotlin
val interactionSource = remember { MutableInteractionSource() }
val isDragging by interactionSource.collectIsDraggedAsState()
val isHovered by interactionSource.collectIsHoveredAsState()

Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    interactionSource = interactionSource,
)

Text(
    text = when {
        isDragging -> "Dragging"
        isHovered  -> "Hovering"
        else       -> "Idle"
    }
)
```

This is standard Compose `InteractionSource` — anything that works there works here.

## Reacting when the user releases

`onDegreeChangeFinished` fires once when the user lifts their finger or releases the mouse button:

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    onDegreeChangeFinished = {
        println("Settled at $degree°")
    },
)
```

This is useful for triggering side effects — saving preferences, snapping with a custom animation, or logging — without firing on every intermediate drag position.

## Animating snapped positions

When using `interval` or `steps`, you often want the thumb to visually snap rather than jump. Animate `degree` with `animateFloatAsState` and let `onDegreeChange` update the underlying state:

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

The `degree` state holds the snapped target; `animatedDegree` smoothly chases it.

## Disabling the dial

Set `enabled = false` to make the dial non-interactive. It still renders normally, but ignores drag input and hides the hand cursor on desktop.

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    enabled = isEditable,
)
```
