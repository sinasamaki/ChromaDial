---
title: RadiusMode
description: API reference for the RadiusMode enum.
---

`RadiusMode` is an enum that determines how the dial calculates its radius from the available space.

## Definition

```kotlin
enum class RadiusMode {
    WIDTH,
    HEIGHT
}
```

## Values

### WIDTH

The radius is calculated as half of the dial's width.

```kotlin
radius = constraints.maxWidth / 2f
```

This is the default mode and works best for:
- Square dials
- Dials where the width determines the circular path
- Standard full-circle dials

### HEIGHT

The radius is calculated as half of the dial's height.

```kotlin
radius = constraints.maxHeight / 2f
```

Use this mode for:
- Non-square dials where height is smaller
- Semi-circle dials oriented horizontally
- Cases where you want the thumb to follow a path based on height

## Usage

### Default (WIDTH mode)

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(200.dp),  // Square dial
    // radiusMode defaults to WIDTH
)
```

### Explicit WIDTH mode

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(200.dp),
    radiusMode = RadiusMode.WIDTH,
)
```

### HEIGHT mode for Semi-Circles

For a top semi-circle (half height), use HEIGHT mode to ensure the thumb follows the correct path:

```kotlin
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(200.dp, 100.dp),  // Width: 200, Height: 100
    startDegrees = 270f,
    sweepDegrees = 180f,
    radiusMode = RadiusMode.HEIGHT,  // Radius = 50dp (half of height)
)
```

## Visual Comparison

Consider a dial with `Modifier.size(200.dp, 100.dp)`:

| Mode | Radius | Result |
|------|--------|--------|
| `WIDTH` | 100dp | Thumb path extends beyond visible area |
| `HEIGHT` | 50dp | Thumb path fits within the visible area |

## When to Use Each Mode

### Use WIDTH (default) when:

- Your dial is square
- You want a full circle
- The width should determine the circular path
- Most standard dial use cases

### Use HEIGHT when:

- Your dial is wider than it is tall
- You're creating a semi-circle that spans horizontally
- The visible area's height should constrain the thumb path
- You want the thumb to stay within vertical bounds

## Example: Comparing Modes

```kotlin
// These two dials have the same dimensions but different paths

// Width mode - larger radius, thumb may go outside bounds
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(300.dp, 150.dp),
    startDegrees = 270f,
    sweepDegrees = 180f,
    radiusMode = RadiusMode.WIDTH,  // radius = 150dp
)

// Height mode - smaller radius, thumb stays within bounds
Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    modifier = Modifier.size(300.dp, 150.dp),
    startDegrees = 270f,
    sweepDegrees = 180f,
    radiusMode = RadiusMode.HEIGHT,  // radius = 75dp
)
```

## Accessing in DialState

The calculated radius is available in `DialState`:

```kotlin
track = { state ->
    // state.radiusMode - the mode used
    // state.radius - the calculated radius in pixels

    Box(
        Modifier.drawBehind {
            drawCircle(
                color = Color.Gray,
                radius = state.radius,
                style = Stroke(2.dp.toPx())
            )
        }
    )
}
```

## See Also

- [DialState](/reference/dial-state/) - State holder that contains the calculated radius
- [Dial Basics](/components/dial-basics/) - Learn how to use the Dial component
