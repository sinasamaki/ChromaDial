package com.sinasamaki.chroma.dial.recordings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.skia.Image

/**
 * Renders all frames for [definition] up front, then replays them in a loop at the
 * animation's native fps. Shows a loading spinner while rendering. The ↺ button
 * re-renders from scratch.
 */
@Composable
fun Previewer(
    definition: () -> AnimationDefinition,
    modifier: Modifier = Modifier,
) {
    var frames by remember { mutableStateOf<List<Image>>(emptyList()) }
    var currentFrame by remember { mutableStateOf(0) }
    var reloadKey by remember { mutableStateOf(0) }

    val animation = remember(definition) { definition() }

    LaunchedEffect(animation, reloadKey) {
        frames = emptyList()
        val collected = mutableListOf<Image>()
        runFrameLoop(animation) { _, image -> collected.add(image) }
        frames = collected
    }

    LaunchedEffect(frames) {
        if (frames.isNotEmpty()) {
            var i = 0
            while (true) {
                currentFrame = i % frames.size
                delay(animation.frameMs)
                i++
            }
        }
    }

    Box(
        modifier.size(animation.width.dp, animation.height.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (frames.isEmpty()) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Box(
                Modifier.fillMaxSize().drawBehind {
                    frames.getOrNull(currentFrame)?.let {
                        drawImage(it.toComposeImageBitmap())
                    }
                }
            )
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(24.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    .clickable { reloadKey++ },
                contentAlignment = Alignment.Center,
            ) {
                Text("↺", color = Color.White)
            }
        }
    }
}
