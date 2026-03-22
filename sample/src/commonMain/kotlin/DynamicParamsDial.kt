import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinasamaki.chroma.dial.Dial
import com.sinasamaki.chroma.dial.rememberDialState

@Composable
fun DynamicParamsDial() {
    var startDegrees by remember { mutableFloatStateOf(180f) }
    var sweepDegrees by remember { mutableFloatStateOf(275f) }
    var degree by remember { mutableFloatStateOf(90f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Dynamic Params",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Dial(
            degree = degree,
            onDegreeChange = { degree = it },
            startDegrees = startDegrees,
            sweepDegrees = sweepDegrees,
            modifier = Modifier.size(200.dp),
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "Start: ${startDegrees.toInt()}°",
                color = Zinc400,
                fontSize = 12.sp,
            )
            Slider(
                value = startDegrees,
                onValueChange = { startDegrees = it },
                valueRange = 0f..360f,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                "Sweep: ${sweepDegrees.toInt()}°",
                color = Zinc400,
                fontSize = 12.sp,
            )
            Slider(
                value = sweepDegrees,
                onValueChange = { sweepDegrees = it },
                valueRange = 10f..720f,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
