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
import com.microblink.blinkidverify.ux.state.MbTorchState
import com.microblink.blinkidverify.ux.theme.VerifyTheme

@Composable
fun TorchButton(
    modifier: Modifier,
    torchState: MbTorchState,
    onTorchStateChange: () -> Unit
) {
//    if (torchState != MbTorchState.NOT_SUPPORTED_BY_CAMERA) {
    val torchButtonBackgroundColor =
        if (torchState == MbTorchState.On) VerifyTheme.uiColors.torchOnButtonBackground else VerifyTheme.uiColors.torchOffButtonBackground
    val torchButtonColor =
        if (torchState == MbTorchState.On) VerifyTheme.uiColors.torchOnButton else VerifyTheme.uiColors.torchOffButton
    val icon =
        if (torchState == MbTorchState.On) painterResource(R.drawable.mb_icon_torch_on) else painterResource(
            R.drawable.mb_icon_torch_off
        )
    Box(
        modifier = modifier
            .size(uiButtonRadiusDp)
            .clip(CircleShape)
            .background(torchButtonBackgroundColor)
            .clickable {
                onTorchStateChange()
            }
    ) {
        // TODO: accessibility
        Image(
            modifier = Modifier.fillMaxSize(), painter = icon, contentDescription = "",
            colorFilter = ColorFilter.tint(torchButtonColor)
        )
    }
}