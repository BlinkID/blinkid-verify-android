/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkidverify.ux

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import com.microblink.blinkidverify.ux.theme.VerifySdkStrings
import com.microblink.blinkidverify.ux.theme.VerifyTypography
import com.microblink.blinkidverify.ux.theme.UiColors

const val DefaultShowOnboardingDialog = true
const val DefaultShowHelpButton = true

/**
 * Configuration settings for the verification UI.
 *
 * Allows customization of various aspects of the UI used
 * during the verification process, such as typography, color schemes,
 * reticle colors, string resources, and dialogs.
 *
 * @property typography             The [Typography] to be used in the verification UI.
 *                                  Defaults to [com.microblink.blinkidverify.ux.theme.VerifyTypography] which uses default OS font.
 * @property colorScheme            The [ColorScheme] to be used in the verification UI.
 *                                  If left `null`, the theme will be applied based on the device settings (dark or light mode).
 *                                  Setting this value will overwrite the default behavior and the user
 *                                  will be responsible for handling theming based on device settings. Defaults to `null`.
 * @property uiColors               The [UiColors] to be used for graphical elements of
 *                                  the verification UI. If left `null`, the theme will be applied based on the
 *                                  device settings (dark or light mode). Setting this value will overwrite the
 *                                  default behavior and the user will be responsible for handling theming based
 *                                  on device settings. Defaults to `null`.
 * @property verifySdkStrings       The [VerifySdkStrings] containing text strings for the verification UI, such as scanning strings
 *                                  and help dialog strings. Defaults to [com.microblink.blinkidverify.ux.theme.VerifySdkStrings.Default].
 * @property showOnboardingDialog   A boolean indicating whether to show an onboarding dialog at the beginning
 *                                  of the scanning session. Defaults to [DefaultShowOnboardingDialog].
 * @property showHelpButton         A boolean indicating whether to show a help button and enable help screens
 *                                  during the scanning session. Defaults to [DefaultShowHelpButton].
 *
 */
public data class VerifyUiSettings(
    val typography: Typography = VerifyTypography(null),
    val colorScheme: ColorScheme? = null,
    val uiColors: UiColors? = null,
    val verifySdkStrings: VerifySdkStrings = VerifySdkStrings.Default,
    val showOnboardingDialog: Boolean = DefaultShowOnboardingDialog,
    val showHelpButton: Boolean = DefaultShowHelpButton
)