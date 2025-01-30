package com.microblink.blinkidverify.ux.theme

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import com.microblink.blinkidverify.ux.R

@Immutable
data class VerifySdkStrings(
    val scanningStrings: ScanningStrings,
    val helpDialogsStrings: HelpDialogsStrings
) {
    companion object {
        val Default: VerifySdkStrings =
            VerifySdkStrings(
                scanningStrings = ScanningStrings.Default,
                helpDialogsStrings = HelpDialogsStrings.Default
        )
    }
}


var LocalBaseSdkStrings = staticCompositionLocalOf {
    VerifySdkStrings.Default
}

@Immutable
data class ScanningStrings(
    @StringRes val instructionsEmptyString: Int,
    @StringRes val instructionsFrontSide: Int,
    @StringRes val instructionsBackSide: Int,
    @StringRes val instructionsBarcode: Int,
    @StringRes val instructionsFlipDocument: Int,
    @StringRes val instructionsDocumentTooCloseToEdge: Int,
    @StringRes val instructionsDocumentNotFullyVisible: Int,
    @StringRes val instructionsDocumentTilted: Int,
    @StringRes val instructionsFacePhotoNotFullyVisible: Int,
    @StringRes val instructionsScanningWrongSide: Int,
    @StringRes val instructionsBlurDetected: Int,
    @StringRes val instructionsGlareDetected: Int,
    @StringRes val instructionsMoveFarther: Int,
    @StringRes val instructionsMoveCloser: Int,
    @StringRes val snackbarFlashlightWarning: Int
) {
    companion object {
        val Default: ScanningStrings =
            ScanningStrings(
                instructionsEmptyString = R.string.mb_blinkid_empty_instructions,
                instructionsFrontSide = R.string.mb_blinkid_front_instructions,
                instructionsBackSide = R.string.mb_blinkid_back_instructions,
                instructionsBarcode = R.string.mb_blinkid_back_instructions_barcode,
                instructionsFlipDocument = R.string.mb_blinkid_camera_flip_document,
                instructionsDocumentTooCloseToEdge = R.string.mb_blinkid_document_too_close_to_edge,
                instructionsDocumentNotFullyVisible = R.string.mb_blinkid_document_not_fully_visible,
                instructionsDocumentTilted = R.string.mb_blinkid_align_document,
                instructionsFacePhotoNotFullyVisible = R.string.mb_blinkid_face_photo_not_fully_visible,
                instructionsScanningWrongSide = R.string.mb_blinkid_scanning_wrong_side,
                instructionsBlurDetected = R.string.mb_blinkid_blur_detected,
                instructionsGlareDetected = R.string.mb_blinkid_glare_detected,
                instructionsMoveFarther = R.string.mb_blinkid_move_farther,
                instructionsMoveCloser = R.string.mb_blinkid_move_closer,
                snackbarFlashlightWarning = R.string.mb_flashlight_warning_message
            )
    }
}

@Immutable
data class HelpDialogsStrings(
    @StringRes val onboardingTitle: Int,
    @StringRes val onboardingBarcodeTitle: Int,
    @StringRes val onboardingMrzTitle: Int,
    @StringRes val onboardingMessage: Int,
    @StringRes val onboardingBarcodeMessage: Int,
    @StringRes val onboardingMrzMessage: Int,
    @StringRes val helpTitle1: Int,
    @StringRes val helpBarcodeTitle1: Int,
    @StringRes val helpMrzTitle1: Int,
    @StringRes val helpTitle2: Int,
    @StringRes val helpTitle3: Int,
    @StringRes val helpMessage1: Int,
    @StringRes val helpBarcodeMessage1: Int,
    @StringRes val helpMrzMessage1: Int,
    @StringRes val helpMessage2: Int,
    @StringRes val helpMessage3: Int,
) {
    internal companion object {
        val Default: HelpDialogsStrings =
            HelpDialogsStrings(
                onboardingTitle = R.string.mb_blinkid_onboarding_dialog_title,
                onboardingBarcodeTitle = R.string.mb_blinkid_onboarding_dialog_title_barcode,
                onboardingMrzTitle = R.string.mb_blinkid_onboarding_dialog_title_mrz,
                onboardingMessage = R.string.mb_blinkid_onboarding_dialog_message,
                onboardingBarcodeMessage = R.string.mb_blinkid_onboarding_dialog_message_barcode,
                onboardingMrzMessage = R.string.mb_blinkid_onboarding_dialog_message_mrz,
                helpTitle1 = R.string.mb_blinkid_help_dialog_title1,
                helpBarcodeTitle1 = R.string.mb_blinkid_help_dialog_title1_barcode,
                helpMrzTitle1 = R.string.mb_blinkid_help_dialog_title1_mrz,
                helpTitle2 = R.string.mb_blinkid_help_dialog_title2,
                helpTitle3 = R.string.mb_blinkid_help_dialog_title3,
                helpMessage1 = R.string.mb_blinkid_help_dialog_msg1,
                helpBarcodeMessage1 = R.string.mb_blinkid_help_dialog_msg1_barcode,
                helpMrzMessage1 = R.string.mb_blinkid_help_dialog_msg1_mrz,
                helpMessage2 = R.string.mb_blinkid_help_dialog_msg2,
                helpMessage3 = R.string.mb_blinkid_help_dialog_msg3
            )
    }
}