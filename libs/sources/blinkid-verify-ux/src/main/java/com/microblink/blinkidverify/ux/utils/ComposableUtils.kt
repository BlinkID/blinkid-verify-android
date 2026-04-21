/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkidverify.ux.utils

import androidx.compose.runtime.Composable
import com.microblink.blinkidverify.ux.theme.VerifyTheme
import com.microblink.ux.R
import com.microblink.ux.components.ErrorDialog
import com.microblink.ux.components.HelpScreenPage
import com.microblink.ux.components.HelpScreens
import com.microblink.ux.state.ErrorState

@Composable
fun fillHelpScreens(): HelpScreens {
    return HelpScreens(
        onboardingDialogPage = HelpScreenPage(
            pageImage = R.drawable.mb_blinkid_onboarding_id,
            pageTitle = VerifyTheme.sdkStrings.helpDialogsStrings.onboardingTitle,
            pageMessage = VerifyTheme.sdkStrings.helpDialogsStrings.onboardingMessage
        ),
        helpDialogPages = listOf(
            HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_id_page_one,
                pageTitle = VerifyTheme.sdkStrings.helpDialogsStrings.helpTitles[0],
                pageMessage = VerifyTheme.sdkStrings.helpDialogsStrings.helpMessages[0]
            ),
            HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_id_page_two,
                pageTitle = VerifyTheme.sdkStrings.helpDialogsStrings.helpTitles[1],
                pageMessage = VerifyTheme.sdkStrings.helpDialogsStrings.helpMessages[1]
            ),
            HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_id_page_three,
                pageTitle = VerifyTheme.sdkStrings.helpDialogsStrings.helpTitles[2],
                pageMessage = VerifyTheme.sdkStrings.helpDialogsStrings.helpMessages[2]
            )
        )
    )
}

@Composable
fun fillErrorDialogs(
    onRetry: () -> Unit,
    onDoneError: () -> Unit
): Map<ErrorState, @Composable () -> Unit> {
    return mapOf(
        ErrorState.NoError to {},
        ErrorState.ErrorInvalidLicense to {
            ErrorDialog(
                R.string.mb_license_locked,
                null,
                R.string.mb_close,
                onButtonClick = onDoneError
            )
        },
        ErrorState.ErrorNetworkError to {
            ErrorDialog(
                R.string.mb_license_locked,
                null,
                R.string.mb_close,
                onButtonClick = onDoneError
            )
        },
        ErrorState.ErrorTimeoutExpired to {
            ErrorDialog(
                R.string.mb_recognition_timeout_dialog_title,
                R.string.mb_recognition_timeout_dialog_message,
                R.string.mb_recognition_timeout_dialog_retry_button,
                onButtonClick = onRetry
            )
        },
        ErrorState.ErrorDocumentClassFiltered to {
            ErrorDialog(
                R.string.mb_document_class_filtered_dialog_title,
                R.string.mb_document_class_filtered_dialog_message,
                R.string.mb_recognition_timeout_dialog_retry_button,
                onButtonClick = onRetry
            )
        },
        ErrorState.ErrorUnsupportedDocument to {
            ErrorDialog(
                R.string.mb_unsupported_document_title,
                R.string.mb_unsupported_document_message,
                R.string.mb_recognition_timeout_dialog_retry_button,
                onButtonClick = onRetry
            )
        }
    )
}
