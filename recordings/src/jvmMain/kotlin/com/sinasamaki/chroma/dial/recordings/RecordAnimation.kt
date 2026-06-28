package com.sinasamaki.chroma.dial.recordings

import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import java.nio.file.Files

fun easeInOut(t: Float): Float = t * t * (3f - 2f * t)

fun lerp(start: Float, end: Float, fraction: Float): Float = start + (end - start) * fraction

fun recorder(definition: AnimationDefinition, outputDir: File) {
    val frameDir = Files.createTempDirectory("frames").toFile()
    try {
        runFrameLoop(definition) { i, image ->
            frameDir.resolve("frame_%04d.png".format(i))
                .writeBytes(image.encodeToData(EncodedImageFormat.PNG)!!.bytes)
            if (i % 30 == 0) println("  Frame $i/${definition.frameCount}")
        }
        outputDir.mkdirs()
        encodeToWebM(frameDir, outputDir.resolve("${definition.name}.webm"), definition.fps)
    } finally {
        frameDir.deleteRecursively()
    }
}

fun encodeToWebM(frameDir: File, output: File, fps: Int) {
    println("Encoding to WebM: ${output.absolutePath}")
    ProcessBuilder(
        "ffmpeg", "-y",
        "-framerate", "$fps",
        "-i", frameDir.resolve("frame_%04d.png").absolutePath,
        "-c:v", "libvpx-vp9",
        "-pix_fmt", "yuv420p",
        "-b:v", "0",
        "-crf", "10",
        "-an",
        output.absolutePath,
    )
        .inheritIO()
        .start()
        .waitFor()
    println("Saved: ${output.absolutePath} (${output.length() / 1024} KB)")
}
