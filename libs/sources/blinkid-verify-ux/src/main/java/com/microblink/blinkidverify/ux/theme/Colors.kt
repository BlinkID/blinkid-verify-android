/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkidverify.ux.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.microblink.blinkidverify.ux.theme.UiColors.Companion.Default

internal val Cobalt = Color(0xFF0062F2)
internal val CobaltLight = Color(0xFF6FA9FF)
internal val CobaltDark = Color(0xFF142641)
internal val Gray = Color(0xFF666666)
internal val DarkGray = Color(0xFF1E1E1E)
internal val White = Color(0xFFFFFFFF)
internal val Black = Color(0xFF000000)
internal val ErrorRed = Color(0x99FB7185)

val VerifyColorScheme = lightColorScheme(
    primary = Cobalt,
    onBackground = Color.Black,
    background = Color.White
)

val DarkVerifyColorScheme = darkColorScheme(
    primary = CobaltLight,
    onBackground = Color.White,
    background = DarkGray
)

var LocalVerifyTheme = staticCompositionLocalOf {
    VerifyColorScheme
}

/**
 * Data class contains all the text, button, and background colors used
 * throughout the SDK scanning session. [Default] can be used to keep
 * the original theme colors if only some of the elements are to be changed.
 *
 * This class shouldn't be modified, but rather a new instance should be
 * created and used in [com.microblink.blinkidverify.ux.VerifyUiSettings.uiColors]
 * when creating an instance of [com.microblink.blinkidverify.ux.CameraScanningScreen].
 *
 */
@Immutable
data class UiColors(
    val helpButtonBackground: Color,
    val helpButton: Color,
    val helpTooltipBackground: Color,
    val helpTooltipText: Color
) {
    companion object {
        val Default: UiColors =
            UiColors(
                helpButtonBackground = Color.White,
                helpButton = Cobalt,
                helpTooltipBackground = Cobalt,
                helpTooltipText = Color.White
            )
        val DefaultDark: UiColors =
            UiColors(
                helpButtonBackground = CobaltDark,
                helpButton = CobaltLight,
                helpTooltipBackground = CobaltDark,
                helpTooltipText = Color.White
            )
    }
}

var LocalBaseUiColors = staticCompositionLocalOf {
    UiColors.Default
}