package com.sinasamaki.chroma.dial.recordings

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.unit.Density
import org.jetbrains.skia.Image
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalComposeUiApi::class)
open class AnimationDefinition {
    open val name: String get() = ""
    open val durationMs: Long get() = 2000L
    open val fps: Int get() = 60
    open val width: Int get() = 400
    open val height: Int get() = 400

    val frameCount get() = (durationMs * fps / 1000).toInt()
    val frameMs get() = 1000L / fps

    open fun onUpdate(scene: ImageComposeScene, frameIndex: Int, frameTimeMs: Long, progress: Float) {}

    open val Content: @Composable () -> Unit = {}
}

/**
 * Sends a pointer event at a position on a circular arc centered on the scene.
 *
 * [angleDeg] uses clock convention: 0 = 12 o'clock, 90 = 3 o'clock, 180 = 6 o'clock, 270 = 9 o'clock.
 *
 * [radius] defaults to 45% of the scene's shorter axis, matching ChromaDial's default track radius.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun ImageComposeScene.sendPointerEventOnArc(
    eventType: PointerEventType,
    angleDeg: Double,
    timeMillis: Long,
    radius: Float = minOf(constraints.maxWidth, constraints.maxHeight) * 0.45f,
    type: PointerType = PointerType.Touch,
) {
    val cx = constraints.maxWidth / 2f
    val cy = constraints.maxHeight / 2f
    val rad = Math.toRadians(angleDeg - 90)
    sendPointerEvent(
        eventType = eventType,
        position = Offset(
            (cx + radius * cos(rad)).toFloat(),
            (cy + radius * sin(rad)).toFloat(),
        ),
        timeMillis = timeMillis,
        type = type,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
internal fun runFrameLoop(definition: AnimationDefinition, onFrame: (index: Int, image: Image) -> Unit) {
    val frameCount = definition.frameCount
    val frameMs = definition.frameMs

    val scene = ImageComposeScene(
        width = definition.width,
        height = definition.height,
        density = Density(2f),
        content = { definition.Content() },
    )

    println("Rendering $frameCount frames for '${definition.name}'...")
    try {
        for (i in 0 until frameCount) {
            val frameTimeMs = i * frameMs
            val progress = i.toFloat() / (frameCount - 1).coerceAtLeast(1)
            definition.onUpdate(scene, i, frameTimeMs, progress)
            onFrame(i, scene.render(nanoTime = i * frameMs * 1_000_000L))
        }
    } finally {
        scene.close()
    }
}
