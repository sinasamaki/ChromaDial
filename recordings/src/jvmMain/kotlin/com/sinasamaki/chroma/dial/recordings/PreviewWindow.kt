package com.sinasamaki.chroma.dial.recordings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun showPreviewWindow() {
    showRecordingPreviewWindow(
        title = "ChromaDial Docs Recordings",
    ) {
        AnimPreview("hero") { Previewer(definition = { HeroAnimation() }) }
        AnimPreview("intro_arc_config") { Previewer(definition = { IntroArcConfigAnimation() }) }
        AnimPreview("intro_multi_ring") { Previewer(definition = { IntroMultiRingAnimation() }) }
        AnimPreview("basics_full_circle") { Previewer(definition = { BasicFullCircleAnimation() }) }
        AnimPreview("basics_semi_circle") { Previewer(definition = { BasicSemiCircleAnimation() }) }
        AnimPreview("basics_stepped") { Previewer(definition = { BasicSteppedAnimation() }) }
        AnimPreview("basics_multi_rotation") { Previewer(definition = { BasicMultiRotationAnimation() }) }
        AnimPreview("basics_programmatic") { Previewer(definition = { BasicProgrammaticAnimation() }) }
        AnimPreview("basics_arc_shape") { Previewer(definition = { BasicArcShapeAnimation() }) }
        AnimPreview("basics_snapping") { Previewer(definition = { BasicSnappingAnimation() }) }
        AnimPreview("basics_counterclockwise") { Previewer(definition = { BasicCounterclockwiseAnimation() }) }
        AnimPreview("basics_mapped_value") { Previewer(definition = { BasicMappedValueAnimation() }) }
        AnimPreview("bento_overshoot") { Previewer(definition = { BentoOvershootAnimation() }) }
        AnimPreview("bento_draw_utilities") { Previewer(definition = { BentoDrawUtilitiesAnimation() }) }
        AnimPreview("bento_default") { Previewer(definition = { BentoDefaultDialAnimation() }) }
        AnimPreview("custom_invisible_thumb") { Previewer(definition = { CustomInvisibleThumbAnimation() }) }
        AnimPreview("custom_overshoot") { Previewer(definition = { CustomOvershootAnimation() }) }
        AnimPreview("custom_tick_marks") { Previewer(definition = { CustomTickMarksAnimation() }) }
        AnimPreview("custom_gradient") { Previewer(definition = { CustomGradientAnimation() }) }
        AnimPreview("state_overshoot") { Previewer(definition = { StateOvershootAnimation() }) }
        AnimPreview("dial_colors") { Previewer(definition = { DialColorsAnimation() }) }
        AnimPreview("input_interaction") { Previewer(definition = { InputInteractionAnimation() }) }
        AnimPreview("layout_gauge") { Previewer(definition = { LayoutGaugeAnimation() }) }
    }
}

fun showRecordingPreviewWindow(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = title,
            state = rememberWindowState(width = 580.dp, height = 920.dp),
        ) {
            RecordingPreviewScreen(content = content)
        }
    }
}

@Composable
private fun RecordingPreviewScreen(
    content: @Composable ColumnScope.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var isRecording by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Zinc950)
            .padding(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "ChromaDial Docs Recordings",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f),
            )
            Button(
                onClick = {
                    isRecording = true
                    coroutineScope.launch(Dispatchers.IO) {
                        ProcessBuilder(
                            "./gradlew", ":recordings:run",
                            "--args=record",
                            "--rerun-tasks",
                        )
                            .directory(File("/Users/leshan/projects/ChromaDial"))
                            .inheritIO()
                            .start()
                            .waitFor()
                        isRecording = false
                    }
                },
                enabled = !isRecording,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Red400,
                    disabledContainerColor = Zinc700,
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                if (isRecording) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = Color.White,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Recording...", color = Color.White, fontSize = 13.sp)
                } else {
                    Text("⏺  Record All", color = Color.White, fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
}

@Composable
fun AnimPreview(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Neutral950)
                .padding(32.dp),
            contentAlignment = Alignment.Center,
            content = content,
        )
        Spacer(Modifier.height(6.dp))
        Text(text = label, color = Gray400, fontSize = 11.sp)
    }
}
