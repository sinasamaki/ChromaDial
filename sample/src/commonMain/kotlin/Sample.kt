import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Sample() {

    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Neutral950),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(64.dp)
        ) {
            item {
                ProgressGaugeDial()
            }
            item {
                CameraModeDial()
            }
            item {
                TimerDial()
            }
            item {
                AngleIndicatorDial()
            }
            item {
                NutritionGoalDial()
            }
            item {
                DefaultDial()
            }
            item {
                Over360Dial()
            }
            item {
                TickedDial()
            }
            item {
                MaterialDial()
            }
            item {
                MinimalClock()
            }
            item {
                AmPmShadowClock()
            }
        }
    }

}
