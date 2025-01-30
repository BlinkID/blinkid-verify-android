package com.microblink.blinkidverify.ux

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import com.microblink.blinkidverify.ux.theme.VerifyColorScheme
import com.microblink.blinkidverify.ux.theme.VerifySdkStrings
import com.microblink.blinkidverify.ux.theme.VerifyTypography
import com.microblink.blinkidverify.ux.theme.ReticleColors

const val DefaultShowOnboardingDialog = true
const val DefaultShowHelpButton = true

public data class VerifyUiSettings(
    val buttonShape: ButtonShape = ButtonShape.Default,
    val typography: Typography = VerifyTypography,
    // maybe merge these two together
    val colorScheme: ColorScheme = VerifyColorScheme,
    val reticleColors: ReticleColors = ReticleColors.Default,
    val verifySdkStrings: VerifySdkStrings = VerifySdkStrings.Default,
    val showOnboardingDialog: Boolean = DefaultShowOnboardingDialog,
    val showHelpButton: Boolean = DefaultShowHelpButton
)
@Immutable
public data class ButtonShape(
    val shape: Shape
) {
    internal companion object {
        val Default: ButtonShape = ButtonShape(shape = CircleShape)
    }
}