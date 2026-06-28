---
title: Introduction
description: Learn about ChromaDial and what you can build with it.
---

ChromaDial is a Kotlin Multiplatform library that provides a highly customizable circular dial/knob component built with Compose Multiplatform.

## What is ChromaDial?

ChromaDial gives you a draggable circular dial component that can be used for:

- **Volume controls** - Classic rotary knob interfaces
- **Timers** - Circular timer selectors with multiple rotation support
- **Settings** - Camera mode dials, temperature controls, etc.
- **Progress indicators** - Circular progress with user interaction
- **Angle selectors** - Precise angle measurement tools
- **Any circular input** - If you can imagine it as a dial, ChromaDial can build it

## Key Concepts

### Degree-Based Positioning

The Dial component works with degrees. The thumb (draggable handle) is positioned around a circular path based on the current degree value. By default, 0 degrees points upward (12 o'clock position), and degrees increase clockwise.

### Two Customization Approaches

ChromaDial offers two ways to customize the dial's appearance:

1. **Simple API** - Use `DialColors` to customize colors while using the default thumb and track
2. **Custom API** - Provide your own `thumb` and `track` composables for full control

### Reading Values

Beyond the raw `degree`, the dial exposes a normalized `value` (`0f..1f`) and a `mappedValue` rescaled into any `valueRange` you choose — perfect for temperatures, volumes, or percentages.

### Controlled or Hoisted State

Use the controlled `Dial(degree, onDegreeChange)` for the simplest cases, or hoist a `DialState` with `rememberDialState()` when you want to read values elsewhere or drive the dial programmatically with `animateTo()`. See the [State-based API](/components/state-based-api/).

### Layout

`DialLayout` controls how the radius and center are derived from the available space — enabling full circles, horizontal semi-circles, and bottom-anchored gauges from the same component.

### Arc Configuration

<video src="/intro_arc_config.webm" autoplay loop muted playsinline></video>

Use `startDegrees` and `sweepDegrees` to configure the arc:
- `startDegrees` - Where the arc begins visually (0 = top, 90 = right, 180 = bottom, 270 = left)
- `sweepDegrees` - How many degrees the arc spans (can exceed 360 for multi-rotation dials)

### Default Track Features

<video src="/intro_multi_ring.webm" autoplay loop muted playsinline></video>

The default track includes advanced features out of the box:
- **Multi-ring display** - When `sweepDegrees` exceeds 360, completed rotations scale outward with animated transitions
- **Tick marks** - Set `interval` to display tick marks along the track
- **Smooth animations** - Ring scale, alpha, and stroke width animate smoothly

## Platforms

ChromaDial supports all Compose Multiplatform targets:

- Android
- iOS
- Desktop (JVM)
- Web (experimental)

## Next Steps

- [Installation](/guides/installation/) - Add ChromaDial to your project
- [Dial Basics](/components/dial-basics/) - Learn the Dial component API
