---
title: Dial Basics
description: Get started with the Dial component.
---

The `Dial` is a circular draggable control. Users spin it by dragging along its circumference, and you get back a degree value you can use however you like — to control volume, pick a temperature, set a timer, or anything in between.

## Your first Dial

```kotlin
var degree by remember { mutableFloatStateOf(0f) }

Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(200.dp),
)
```

`Dial` is a controlled component — you own the state. Pass the current `degree` in, receive updates via `onDegreeChange`, and store the new value yourself.

## Shaping the arc

<video src="/basics_arc_shape.webm" autoplay loop muted playsinline></video>

Two parameters control where the dial sits on screen and how far it sweeps:

- **`startDegrees`** — the screen angle where `degree = 0`. Think of a clock: `0f` is 12 o'clock, `90f` is 3 o'clock, `180f` is 6 o'clock, `270f` is 9 o'clock.
- **`sweepDegrees`** — how many degrees the user can rotate from the start to the end. Defaults to `360f`. Can exceed 360 for multi-rotation dials.

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    startDegrees = 225f,   // starts at ~7 o'clock
    sweepDegrees = 270f,   // sweeps ¾ of the way around
)
```

## Snapping

<video src="/basics_snapping.webm" autoplay loop muted playsinline></video>

By default the rotation is continuous. Use `interval` or `steps` to add discrete snap points:

```kotlin
interval = 15f   // snaps every 15°
steps = 12       // 12 evenly spaced stops across the sweep
```

`steps` is handy when you know how many positions you need. `interval` is handy when you care about the spacing between them. Setting both? `steps` wins.

## Reading the value

<video src="/basics_mapped_value.webm" autoplay loop muted playsinline></video>

`degree` is the raw angle from `0` to `sweepDegrees`. Two derived readings are usually more convenient:

- **`value`** — normalized `0f..1f` across the sweep
- **`mappedValue`** — `value` rescaled into a `valueRange` you choose (temperature, volume, percent…)

Both live on `DialState`. The simplest way to read them outside the dial is the [state-based API](/components/state-based-api/), where you hold the state yourself:

```kotlin
val state = rememberDialState()

Dial(
    state = state,
    sweepDegrees = 270f,
    valueRange = 0f..100f,
)

Text("${state.mappedValue.toInt()}°C")
```

Inside a custom `thumb` or `track`, the same `state` is handed to you — so you can render the value right on the dial. See [Custom Thumb & Track](/components/customization/).

## Counterclockwise

<video src="/basics_counterclockwise.webm" autoplay loop muted playsinline></video>

Set `clockwise = false` and the thumb moves the other way. Everything else — `degree`, `value`, `startDegrees` — behaves the same.

## Sizing and position

By default the dial is centered in its bounds and its radius is half the **width**. Pass a `layout` to change that — base the radius on height, shrink it to leave room for labels, or move the center (handy for gauges anchored to the bottom edge):

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(200.dp, 110.dp),
    startDegrees = 270f,
    sweepDegrees = 180f,
    layout = DialLayout.height(),   // radius from height instead of width
)
```

See [DialLayout](/reference/dial-layout/) for `radiusFraction` and custom `center` positioning.

## Disabling the dial

Set `enabled = false` to make the dial non-interactive. It still renders, but ignores drag input and hides the pointer cursor.

## What's next

- [Common Patterns](/components/common-patterns/) — full circle, semi-circle, stepped, multi-rotation, and more
- [Default Dial Colors](/components/dial-colors/) — customizing colors with `DialColors`
- [Custom Thumb & Track](/components/customization/) — full visual control with composable slots
- [State-based API](/components/state-based-api/) — manage state externally for programmatic animation
- [Overshoot](/components/overshoot/) — the rubber-band effect at the limits
- [Responding to Input](/components/responding-to-input/) — react to drag, hover, and release events
