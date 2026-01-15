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

### Two API Variants

ChromaDial offers two ways to configure the rotation range:

1. **Simple API** - Use `startDegrees` and `sweepDegrees` for easy arc configuration
2. **Advanced API** - Use `degreeRange` for precise control over allowed rotation

### Customizable Visuals

Both the **thumb** (the draggable handle) and **track** (the background path) are fully customizable composables. You provide your own composables that receive a `DialState` object with all the information needed to render your design.

## Platforms

ChromaDial supports all Compose Multiplatform targets:

- Android
- iOS
- Desktop (JVM)
- Web (experimental)

## Next Steps

- [Installation](/guides/installation/) - Add ChromaDial to your project
- [Dial Basics](/components/dial-basics/) - Learn the Dial component API
