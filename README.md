# ChromaDial

![Maven Central Version](https://img.shields.io/maven-central/v/com.sinasamaki/chroma-dial)

A customizable circular dial/knob component for Compose Multiplatform.

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
implementation("com.sinasamaki:chroma-dial:<version>")
```

## Usage

```kotlin
var degree by remember { mutableFloatStateOf(0f) }

Dial(
    degree = degree,
    onDegreeChange = { degree = it },
    startDegrees = 135f,
    sweepDegrees = 270f,
    modifier = Modifier.size(200.dp)
)
```
