package com.sinasamaki.chroma.dial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication

fun main() = singleWindowApplication {

    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Black),
        contentAlignment = Alignment.Center
    ) {

        var degree by remember { mutableFloatStateOf(0f) }

        Dial(
            degree = degree,
            onDegreeChanged = { degree = it },
            modifier = Modifier.size(200.dp),
            startDegrees = 0f,
            sweepDegrees = 360f
        )
    }

}