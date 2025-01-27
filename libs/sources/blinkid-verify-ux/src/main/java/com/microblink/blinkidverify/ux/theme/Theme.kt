package com.microblink.blinkidverify.ux.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun BlinkIDVerifySdkTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalBaseUiColors provides UiColors.Default,
        LocalBaseReticleColors provides ReticleColors.Default,
        LocalBaseSdkStrings provides VerifySdkStrings.Default

    ) {
        MaterialTheme(
            typography = VerifyTypography,
            content = content
        )
    }
}

internal object VerifyTheme {
    val uiColors: UiColors
        @Composable
        get() = LocalBaseUiColors.current

    val reticleColors: ReticleColors
        @Composable
        get() = LocalBaseReticleColors.current

    val sdkStrings: VerifySdkStrings
        @Composable
        get() = LocalBaseSdkStrings.current
}