package com.microblink.blinkidverify.ux

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.microblink.blinkidverify.ux.components.DocumentFlipAnimation
import com.microblink.blinkidverify.ux.components.ErrorDialog
import com.microblink.blinkidverify.ux.components.ExitButton
import com.microblink.blinkidverify.ux.components.HelpBox
import com.microblink.blinkidverify.ux.components.HelpScreens
import com.microblink.blinkidverify.ux.components.MessageContainer
import com.microblink.blinkidverify.ux.components.OnboardingDialog
import com.microblink.blinkidverify.ux.components.Reticle
import com.microblink.blinkidverify.ux.components.TorchButton
import com.microblink.blinkidverify.ux.state.CardAnimationState
import com.microblink.blinkidverify.ux.state.VerifyUiState
import com.microblink.blinkidverify.ux.state.ProcessingState
import com.microblink.blinkidverify.ux.state.ReticleState
import com.microblink.blinkidverify.ux.state.StatusMessage
import com.microblink.blinkidverify.ux.theme.VerifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanningUX(
    modifier: Modifier,
    uiState: VerifyUiState,
    onExitScanning: () -> Unit,
    verifyUiSettings: VerifyUiSettings,
    onTorchStateChange: () -> Unit,
    onFlipDocumentAnimationCompleted: () -> Unit,
    onReticleSuccessAnimationCompleted: () -> Unit,
    onChangeOnboardingDialogVisibility: (Boolean) -> Unit,
    onChangeHelpScreensVisibility: (Boolean) -> Unit,
    onChangeHelpTooltipVisibility: (Boolean) -> Unit,
    onRetryTimeout: () -> Unit,
) {
    ScanningScreenCentralElements(
        modifier = modifier,
        reticleState = uiState.processingState,
        instructionMessage = uiState.statusMessage,
        cardAnimationState = uiState.cardAnimationState,
        onFlipDocumentAnimationCompleted = onFlipDocumentAnimationCompleted,
        onReticleSuccessAnimationCompleted = onReticleSuccessAnimationCompleted
    )
    Box(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ExitButton(Modifier.align(Alignment.TopStart), onExitScanning)
        TorchButton(Modifier.align(Alignment.TopEnd), uiState.torchState, onTorchStateChange)
        if (verifyUiSettings.showHelpButton) HelpBox(Modifier.align(Alignment.BottomEnd), uiState.helpButtonDisplayed, uiState.helpTooltipDisplayed, onChangeHelpScreensVisibility, onChangeHelpTooltipVisibility)
    }

    if (verifyUiSettings.showOnboardingDialog && uiState.onboardingDialogDisplayed) {
        OnboardingDialog { onChangeOnboardingDialogVisibility(false) }
    }
    if (verifyUiSettings.showHelpButton && uiState.helpDisplayed) {
        HelpScreens(onChangeHelpScreensVisibility)
    }
    uiState.unrecoverableErrorDialog?.let { state ->
        ErrorDialog(
            R.string.mb_recognition_timeout_dialog_title,
            R.string.mb_recognition_timeout_dialog_message,
            R.string.mb_recognition_timeout_dialog_retry_button,
            onButtonClick = onRetryTimeout
        )

    }
}

@Composable
internal fun ScanningScreenCentralElements(
    modifier: Modifier = Modifier,
    reticleState: ProcessingState,
    instructionMessage: StatusMessage,
    cardAnimationState: CardAnimationState,
    onFlipDocumentAnimationCompleted: () -> Unit,
    onReticleSuccessAnimationCompleted: () -> Unit,
) {

    var _reticleState by remember { mutableStateOf(ReticleState.Sensing) }
    var _instructionMessage by remember { mutableStateOf(StatusMessage.Empty) }
    var _cardAnimationState by remember { mutableStateOf(CardAnimationState.Hidden) }
    var showInstructionDialog by remember { mutableStateOf(true) }
    var showAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(reticleState) {
        _reticleState = reticleState.reticleState
    }

    LaunchedEffect(instructionMessage) {
        _instructionMessage = instructionMessage
        showInstructionDialog = instructionMessage != StatusMessage.Empty
    }

    LaunchedEffect(cardAnimationState) {
        _cardAnimationState = cardAnimationState
        showAnimation = _cardAnimationState != CardAnimationState.Hidden
    }

    Column(modifier.fillMaxSize()) {

        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp
        val screenWidth = configuration.screenWidthDp
        val screenDimensionMinDp =
            if (screenWidth < screenHeight) screenWidth.dp else screenHeight.dp
        Box(
            Modifier
                .fillMaxWidth()
                .weight(0.55f)
                .padding(bottom = 10.dp), contentAlignment = Alignment.BottomCenter
        ) {
            if (showAnimation) {
                DocumentFlipAnimation(
                    screenDimensionMinDp,
                    onFlipDocumentAnimationCompleted
                )
            } else {
                Reticle(_reticleState, screenDimensionMinDp, onReticleSuccessAnimationCompleted)
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .weight(0.45f)
        ) {
            if (showInstructionDialog) {
                _instructionMessage.statusMessageToStringRes()?.let {
                    MessageContainer(it, VerifyTheme.reticleColors.reticleMessageContainerColor)
                }
            }
        }
    }
}