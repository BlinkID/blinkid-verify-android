/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkidverify.ux.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.microblink.ux.theme.Cobalt
import com.microblink.ux.theme.CobaltLight
import com.microblink.ux.theme.DarkGray
import com.microblink.ux.theme.UiColors

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