package com.microblink.blinkidverify.ux.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.microblink.blinkidverify.ux.R
import com.microblink.blinkidverify.ux.theme.VerifyTheme

@Composable
fun HelpButton(
    modifier: Modifier = Modifier,
    onChangeOnboardingDialogState: (Boolean) -> Unit
) {
    val helpButtonBackgroundColor = VerifyTheme.uiColors.helpButtonBackground
    val helpButtonColor = VerifyTheme.uiColors.helpButton

    Box(
        modifier = modifier
            .size(uiButtonRadiusDp)
            .clip(CircleShape)
            .background(helpButtonBackgroundColor)
            .clickable {
                onChangeOnboardingDialogState(true)
            }
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.mb_icon_help),
            contentDescription = "",
            colorFilter = ColorFilter.tint(helpButtonColor)
        )
    }
}