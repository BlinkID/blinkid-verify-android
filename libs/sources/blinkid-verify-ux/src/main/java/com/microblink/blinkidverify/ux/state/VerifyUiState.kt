/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkidverify.ux.state

import androidx.compose.runtime.Composable
import com.microblink.blinkidverify.core.data.model.result.BlinkIdVerifyCaptureResult
import com.microblink.blinkidverify.ux.DefaultShowHelpButton
import com.microblink.blinkidverify.ux.DefaultShowOnboardingDialog
import com.microblink.blinkidverify.ux.capture.scanning.DocumentSide
import com.microblink.blinkidverify.ux.theme.VerifyTheme

data class VerifyUiState(
    val blinkIdVerifyCaptureResult: BlinkIdVerifyCaptureResult? = null,
    val reticleState: ReticleState = ReticleState.Hidden,
    val processingState: ProcessingState = ProcessingState.Sensing,
    val cardAnimationState: CardAnimationState = CardAnimationState.Hidden,
    val statusMessage: StatusMessage = StatusMessage.ScanFrontSide,
    val currentSide: DocumentSide = DocumentSide.Front,
    val torchState: MbTorchState = MbTorchState.Off,
    val cancelRequestState: CancelRequestState = CancelRequestState.CancelNotRequested,
    val helpButtonDisplayed: Boolean = DefaultShowHelpButton,
    val helpDisplayed: Boolean = false,
    val helpTooltipDisplayed: Boolean = false,
    val onboardingDialogDisplayed: Boolean = DefaultShowOnboardingDialog,
    val unrecoverableErrorState: UnrecoverableErrorState = UnrecoverableErrorState.NoError,
    val hapticFeedbackState: HapticFeedbackState = HapticFeedbackState.VibrationOff
)

/**
 * Current state of the flip-card animation shown after the first side
 * has been successfully scanned.
 */
enum class CardAnimationState(val isPortrait: Boolean) {
    Hidden(false),
    ShowFlipLandscape(false),
    ShowFlipPortrait(true),
    ShowRotationToPortrait(true),
    ShowRotationToLandscape(false)
}

/**
 * Current state of the torch (flashlight) as shown by the icon
 * on the UI.
 */
enum class MbTorchState {
    NotSupportedByCamera,
    Off,
    On
}

/**
 * Represents the reason of the cancel request.
 * Currently not used.
 */
enum class CancelRequestState {
    CancelNotRequested,
    CancelByUser,
    CancelLicenseError,
    CancelAnalyzerSettingsUnsuitable,
    CancelUnknownError
}

/**
 * Represents the state of the unrecoverable error.
 */
enum class UnrecoverableErrorState {
    NoError,
    ErrorInvalidLicense,
    ErrorNetworkError,
    ErrorTimeoutExpired
}

/**
 * Represents all the instruction messages that may be shown
 * during the scanning session.
 *
 * This enum class defines the various status messages that can be displayed to the
 * user during the document scanning process. Each enum value corresponds to a
 * specific instruction or feedback message.
 *
 */
enum class StatusMessage {
    Empty,
    ScanFrontSide,
    ScanBackSide,
    ScanBarcode,
    FlipDocument,
    RotateDocument,
    RotateDocumentShort,
    MoveFarther,
    MoveCloser,
    KeepDocumentVisible,
    AlignDocument,
    MoveDocumentFromEdge,
    IncreaseLightingIntensity,
    DecreaseLightingIntensity,
    EliminateBlur,
    EliminateGlare,
    FilterSpecificMessage,
    ScanningWrongSide;

    /**
     * Returns the string resource ID associated with this status message.
     *
     * This function is used to get the string resource that should be
     * displayed to the user for this status message. Status messages
     * that are mapped to `null` are not possible to happen, but might
     * become available in the future releases.
     *
     * @return The string resource ID, or `null` if there is no associated
     *         string resource.
     *
     */
    @Composable
    fun statusMessageToStringRes(): Int? {
        val strings = VerifyTheme.sdkStrings.scanningStrings
        return when (this) {
            Empty -> null
            ScanFrontSide -> strings.instructionsFrontSide
            ScanBackSide -> strings.instructionsBackSide
            ScanBarcode -> strings.instructionsBarcode
            FlipDocument -> strings.instructionsFlipDocument
            RotateDocument -> null
            RotateDocumentShort -> null
            MoveFarther -> strings.instructionsMoveFarther
            MoveCloser -> strings.instructionsMoveCloser
            KeepDocumentVisible -> strings.instructionsDocumentNotFullyVisible
            AlignDocument -> strings.instructionsDocumentTilted
            MoveDocumentFromEdge -> strings.instructionsDocumentTooCloseToEdge
            IncreaseLightingIntensity -> null
            DecreaseLightingIntensity -> null
            EliminateBlur -> strings.instructionsBlurDetected
            EliminateGlare -> strings.instructionsGlareDetected
            FilterSpecificMessage -> null
            ScanningWrongSide -> strings.instructionsScanningWrongSide
        }
    }
}

/**
 * Current state of the haptic (vibration) feedback that
 * activates during the scanning session.
 */
enum class HapticFeedbackState {
    VibrationOff,
    VibrationOneTimeShort,
    VibrationOneTimeLong
}