---
title: DialColors
description: API reference for the DialColors class.
---

`DialColors` provides color customization for the default Dial thumb and track when using the simple API (without custom composables).

## Class Definition

```kotlin
@Immutable
class DialColors internal constructor(
    val inactiveTrackColor: Color,
    val activeTrackColor: Color,
    val thumbColor: Color,
    val thumbStrokeColor: Color,
    val inactiveTickColor: Color,
    val activeTickColor: Color,
)
```

## Creating an Instance

The constructor is internal. Use the `default()` factory method to create instances:

```kotlin
val colors = DialColors.default(
    inactiveTrackColor = Color.Gray,
    activeTrackColor = Color.Blue,
    thumbColor = Color.White,
    thumbStrokeColor = Color.Blue,
    inactiveTickColor = Color.Gray,
    activeTickColor = Color.Blue,
)
```

All parameters are optional and have sensible defaults.

## Factory Method

### DialColors.default()

```kotlin
fun default(
    inactiveTrackColor: Color = Zinc700,
    activeTrackColor: Color = Lime500,
    thumbColor: Color = Zinc950,
    thumbStrokeColor: Color = Lime400,
    inactiveTickColor: Color = Zinc700,
    activeTickColor: Color = Lime300,
): DialColors
```

Creates a `DialColors` instance with the specified colors. All parameters have defaults from the Tailwind CSS color palette.

## Properties

### inactiveTrackColor
**Type:** `Color`
**Default:** `Zinc700`

The color of the track background arc. This arc spans the entire `sweepDegrees` range.

### activeTrackColor
**Type:** `Color`
**Default:** `Lime500`

The color of the active/progress arc. This arc spans from `0` to the current `degree` value.

### thumbColor
**Type:** `Color`
**Default:** `Zinc950`

The fill color of the circular thumb (draggable handle).

### thumbStrokeColor
**Type:** `Color`
**Default:** `Lime400`

The stroke/border color of the thumb. The stroke width is 2dp.

### inactiveTickColor
**Type:** `Color`
**Default:** `Zinc700`

The color of tick marks outside the active range (when `interval > 0`).

### activeTickColor
**Type:** `Color`
**Default:** `Lime300`

The color of tick marks within the active range (when `interval > 0`).

## Usage

### Basic Usage

```kotlin
var degree by remember { mutableFloatStateOf(0f) }

Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    colors = DialColors.default(),  // Use all defaults
)
```

### Custom Colors

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    colors = DialColors.default(
        activeTrackColor = Color.Cyan,
        thumbStrokeColor = Color.Cyan,
        activeTickColor = Color.Cyan,
    ),
)
```

### With Tick Marks

Tick marks are automatically displayed when `interval > 0`:

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    interval = 30f,  // Tick marks every 30 degrees
    colors = DialColors.default(
        inactiveTickColor = Color.Gray.copy(alpha = 0.5f),
        activeTickColor = Color.Blue,
    ),
)
```

## Default Thumb and Track Behavior

When using `DialColors`, the Dial uses built-in default composables:

### Default Thumb
- Circular shape with 24dp diameter
- Filled with `thumbColor`
- 2dp stroke with `thumbStrokeColor`

### Default Track
- 4dp stroke width with rounded caps
- Inactive arc uses `inactiveTrackColor`
- Active arc uses `activeTrackColor`
- Tick marks drawn when `interval > 0`
- **Multi-ring display**: When `sweepDegrees > 360`, completed rotations scale outward with animated transitions and decreasing alpha

## Multi-Ring Behavior

For dials with `sweepDegrees > 360` (e.g., timers), the default track displays multiple rings:

- Each completed 360° rotation creates a new ring
- Rings scale outward with each completion
- Alpha decreases for outer rings (more faded appearance)
- Stroke width animates when rings appear/disappear
- Each ring has its own inactive track and tick marks

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    sweepDegrees = 360f * 3,  // 3 full rotations
    interval = 30f,
    colors = DialColors.default(),
)
```

## Comparison with Custom Composables

| Feature | DialColors | Custom thumb/track |
|---------|------------|-------------------|
| Complexity | Simple | Full control |
| Color customization | Yes | Yes |
| Shape customization | No | Yes |
| Multi-ring support | Built-in | Manual implementation |
| Tick marks | Automatic | Manual with `drawEveryInterval` |

Use `DialColors` for quick theming. Use custom composables when you need non-circular thumbs, custom track shapes, or unique visual effects.

## See Also

- [Dial Basics](/components/dial-basics/) - Learn how to use the Dial component
- [Customization](/components/customization/) - Create custom thumb and track designs
- [DialState](/reference/dial-state/) - State holder for custom composables
