package com.sinasamaki.chroma.dial

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colors for customizing the Dial appearance when using the simple Dial overload.
 */
@Immutable
public data class DialColors(
    val inactiveTrackColor: Color,
    val activeTrackColor: Color,
    val thumbColor: Color,
    val thumbStrokeColor: Color,
    val inactiveTickColor: Color,
    val activeTickColor: Color,
) {
    public companion object {
        /**
         * Creates a [DialColors] instance with default colors.
         */
        public fun default(
            inactiveTrackColor: Color = Zinc700,
            activeTrackColor: Color = Lime500,
            thumbColor: Color = Zinc950,
            thumbStrokeColor: Color = Lime400,
            inactiveTickColor: Color = Zinc700,
            activeTickColor: Color = Lime300,
        ): DialColors = DialColors(
            inactiveTrackColor = inactiveTrackColor,
            activeTrackColor = activeTrackColor,
            thumbColor = thumbColor,
            thumbStrokeColor = thumbStrokeColor,
            inactiveTickColor = inactiveTickColor,
            activeTickColor = activeTickColor,
        )
    }
}
