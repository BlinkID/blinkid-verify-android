package com.microblink.blinkidverify.ux.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val Cobalt = Color(0xFF0062F2)
internal val Gray = Color(0xFF666666)
internal val White = Color(0xFFFFFFFF)

val VerifyColorScheme = lightColorScheme(
    primary = Cobalt,
    background = Color.White
)

val NightDocVerColorScheme = darkColorScheme(
    // TODO: dark color scheme
)

@Immutable
data class UiColors(
    val exitButtonBackground: Color,
    val exitButton: Color,
    val torchOnButtonBackground: Color,
    val torchOnButton: Color,
    val torchOffButtonBackground: Color,
    val torchOffButton: Color,
    val helpButtonBackground: Color,
    val helpButton: Color,
    val helpTooltipBackground: Color,
    val helpTooltipText: Color
) {
    companion object {
        val Default: UiColors =
            UiColors(
                exitButtonBackground = Gray.copy(alpha = 0.6f),
                exitButton = Color.White,
                torchOnButtonBackground = Color.White,
                torchOnButton = Gray.copy(alpha = 0.6f),
                torchOffButtonBackground = Gray.copy(alpha = 0.6f),
                torchOffButton = Color.White,
                helpButtonBackground = Color.White,
                helpButton = Cobalt,
                helpTooltipBackground = Cobalt,
                helpTooltipText = Color.White
            )
    }
}

var LocalBaseUiColors = staticCompositionLocalOf {
    UiColors.Default
}

@Immutable
data class ReticleColors(
    val reticleBaseColor: Color,
    val reticleDotColor: Color,
    val reticleCircleAnimationColor: Color,
    val reticleRotationColor: Color,
    val reticleCircleStaticColor: Color,
    val reticleErrorColor: Color,
    val reticleMessageContainerColor: Color
) {
    companion object {
        val Default: ReticleColors =
            ReticleColors(
                reticleBaseColor = Gray.copy(0.5f),
                reticleDotColor = White,
                reticleCircleAnimationColor = White,
                reticleRotationColor = White.copy(0.75f),
                reticleCircleStaticColor = White.copy(0.3f),
                reticleErrorColor = Color(0x99FB7185),
                reticleMessageContainerColor = Gray.copy(0.9f)
            )
    }
}

var LocalBaseReticleColors = staticCompositionLocalOf {
    ReticleColors.Default
}