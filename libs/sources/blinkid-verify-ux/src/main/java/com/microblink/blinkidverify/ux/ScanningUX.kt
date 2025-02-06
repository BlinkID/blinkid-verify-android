/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

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
import com.microblink.blinkidverify.ux.state.UnrecoverableErrorState
import com.microblink.blinkidverify.ux.theme.Gray

/**
 * Composable function that provides the user interface for the scanning screen,
 * including the reticle, instruction messages, buttons, and dialogs.
 *
 * This function is responsible for rendering all the UI elements visible
 * during the document scanning process, except for the camera preview itself.
 * It handles displaying the reticle, instruction messages, exit and torch
 * buttons, help components, and various dialogs based on the provided UI state
 * and settings.
 *
 * @param modifier The [Modifier] to be applied to the outermost container of the UI.
 * @param uiState The [VerifyUiState] containing the current state of the UI.
 * @param onExitScanning A callback function invoked when the user wants to
 *                       exit the scanning process.
 * @param verifyUiSettings The [VerifyUiSettings] used to configure the UI.
 * @param onTorchStateChange A callback function invoked when the user wants to
 *                           change the torch state.
 * @param onFlipDocumentAnimationCompleted A callback function invoked when the
 *                                         flip document animation is completed.
 * @param onReticleSuccessAnimationCompleted A callback function invoked when
 *                                           the reticle success animation is
 *                                           completed.
 * @param onChangeOnboardingDialogVisibility A callback function invoked when
 *                                           the visibility of the onboarding
 *                                           dialog should change.
 * @param onChangeHelpScreensVisibility A callback function invoked when the
 *                                      visibility of the help screens should
 *                                      change.
 * @param onChangeHelpTooltipVisibility A callback function invoked when the
 *                                       visibility of the help tooltip should
 *                                       change.
 * @param onRetryTimeout A callback function invoked when the retry timeout occurs and the button is pressed.
 * @param onDoneError A callback function invoked when the license error timeout occurs and the button is pressed.
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanningUx(
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
    onDoneError: () -> Unit
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
        if (verifyUiSettings.showHelpButton) HelpBox(
            Modifier.align(Alignment.BottomEnd),
            uiState.helpButtonDisplayed,
            uiState.helpTooltipDisplayed,
            onChangeHelpScreensVisibility,
            onChangeHelpTooltipVisibility
        )
    }

    if (verifyUiSettings.showOnboardingDialog && uiState.onboardingDialogDisplayed) {
        OnboardingDialog { onChangeOnboardingDialogVisibility(false) }
    }
    if (verifyUiSettings.showHelpButton && uiState.helpDisplayed) {
        HelpScreens(onChangeHelpScreensVisibility)
    }
    when (uiState.unrecoverableErrorState) {
        UnrecoverableErrorState.NoError -> {}
        UnrecoverableErrorState.ErrorInvalidLicense ->
            ErrorDialog(
                R.string.mb_blinkid_verify_license_error_dialog_title,
                null,
                R.string.mb_blinkid_verify_license_error_dialog_button,
                onButtonClick = onDoneError
            )

        UnrecoverableErrorState.ErrorNetworkError ->
            ErrorDialog(
                R.string.mb_blinkid_verify_license_error_dialog_title,
                null,
                R.string.mb_blinkid_verify_license_error_dialog_button,
                onButtonClick = onDoneError
            )

        UnrecoverableErrorState.ErrorTimeoutExpired ->
            ErrorDialog(
                R.string.mb_blinkid_verify_recognition_timeout_dialog_title,
                R.string.mb_blinkid_verify_recognition_timeout_dialog_message,
                R.string.mb_blinkid_verify_recognition_timeout_dialog_retry_button,
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
    onReticleSuccessAnimationCompleted: () -> Unit
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
                    MessageContainer(it, Gray.copy(0.9f))
                }
            }
        }
    }
}