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
fun ExitButton(
    modifier: Modifier,
    onExit: () -> Unit
) {
    val exitButtonBackgroundColor = VerifyTheme.uiColors.exitButtonBackground
    val exitButtonColor = VerifyTheme.uiColors.exitButton

    Box(
        modifier = modifier
            .size(uiButtonRadiusDp)
            .clip(CircleShape)
            .background(exitButtonBackgroundColor)
            .clickable {
                onExit()
            }
    ) {
        // TODO: accessibility
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.mb_icon_exit),
            contentDescription = "",
            colorFilter = ColorFilter.tint(exitButtonColor)
        )
    }
}