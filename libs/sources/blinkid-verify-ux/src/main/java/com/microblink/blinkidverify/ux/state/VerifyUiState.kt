package com.microblink.blinkidverify.ux.state

import androidx.compose.runtime.Composable
import com.microblink.blinkidverify.core.data.model.result.BlinkIDVerifyCaptureResult
import com.microblink.blinkidverify.ux.DefaultShowHelpButton
import com.microblink.blinkidverify.ux.DefaultShowOnboardingDialog
import com.microblink.blinkidverify.ux.capture.DocumentSide
import com.microblink.blinkidverify.ux.theme.VerifyTheme
import kotlinx.serialization.Serializable

@Serializable
data class VerifyUiState(
    val blinkIDVerifyCaptureResult: BlinkIDVerifyCaptureResult? = null,
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
    val unrecoverableErrorDialog: UnrecoverableErrorState? = null,
    val hapticFeedbackState: HapticFeedbackState = HapticFeedbackState.VibrationOff
)

enum class CardAnimationState(val isPortrait: Boolean) {
    Hidden(false),
    ShowFlipLandscape(false),
    ShowFlipPortrait(true),
    ShowRotationToPortrait(true),
    ShowRotationToLandscape(false)
}

enum class MbTorchState {
    NotSupportedByCamera,
    Off,
    On
}

enum class CancelRequestState {
    CancelNotRequested,
    CancelByUser,
    CancelLicenseError,
    CancelAnalyzerSettingsUnsuitable,
    CancelUnknownError
}

enum class UnrecoverableErrorState {
    ErrorUnexpected,
    ErrorInvalidLicense,
    ErrorNetworkError,
    ErrorAnalyzerSettingsUnsuitable,
    ErrorTimeoutExpired
}

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

    @Composable
    fun statusMessageToStringRes(): Int? {
        val strings = VerifyTheme.sdkStrings.scanningStrings
        return when (this) {
            Empty -> null
            ScanFrontSide -> strings.instructionsFrontSide
            ScanBackSide -> strings.instructionsBackSide
            ScanBarcode -> strings.instructionsBarcode
            FlipDocument -> strings.instructionsFlipDocument
            RotateDocument -> TODO()
            RotateDocumentShort -> TODO()
            MoveFarther -> strings.instructionsMoveFarther
            MoveCloser -> strings.instructionsMoveCloser
            KeepDocumentVisible -> strings.instructionsDocumentNotFullyVisible
            AlignDocument -> strings.instructionsDocumentTilted
            MoveDocumentFromEdge -> strings.instructionsDocumentTooCloseToEdge
            IncreaseLightingIntensity -> TODO()
            DecreaseLightingIntensity -> TODO()
            EliminateBlur -> strings.instructionsBlurDetected
            EliminateGlare -> strings.instructionsGlareDetected
            FilterSpecificMessage -> TODO()
            ScanningWrongSide -> strings.instructionsScanningWrongSide
        }
    }
}

enum class HapticFeedbackState {
    VibrationOff,
    VibrationOneTimeShort,
    VibrationOneTimeLong
}