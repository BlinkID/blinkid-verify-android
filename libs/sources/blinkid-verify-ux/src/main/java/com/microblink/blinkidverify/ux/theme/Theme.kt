/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkidverify.ux.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.microblink.blinkidverify.ux.VerifyUiSettings
import com.microblink.blinkidverify.ux.theme.VerifyTheme.sdkTheme

/**
 * Theme used in the entire SDK.
 * Defines colors and strings that are used for all the elements.
 * The theme can be updated by defining [VerifyUiSettings] which customizes
 * all the visual elements.
 *
 * @property verifyUiSettings Class that defines all of the visual elements of the SDK.
 *
 */
@Composable
fun BlinkIdVerifySdkTheme(
    verifyUiSettings: VerifyUiSettings,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalBaseUiColors provides (verifyUiSettings.uiColors
            ?: if(isSystemInDarkTheme()) UiColors.DefaultDark else UiColors.Default),
        LocalBaseSdkStrings provides VerifySdkStrings.Default,
        LocalVerifyTheme provides if(isSystemInDarkTheme()) DarkVerifyColorScheme else VerifyColorScheme

    ) {
        MaterialTheme(
            typography = verifyUiSettings.typography,
            colorScheme = verifyUiSettings.colorScheme ?: sdkTheme,
            content = content
        )
    }
}

internal object VerifyTheme {
    val uiColors: UiColors
        @Composable
        get() = LocalBaseUiColors.current

    val sdkStrings: VerifySdkStrings
        @Composable
        get() = LocalBaseSdkStrings.current

    val sdkTheme: ColorScheme
        @Composable
        get() = LocalVerifyTheme.current
}