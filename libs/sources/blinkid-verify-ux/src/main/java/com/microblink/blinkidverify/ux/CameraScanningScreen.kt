/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkidverify.ux

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.microblink.blinkidverify.core.BlinkIdVerifySdk
import com.microblink.blinkidverify.core.capture.session.CaptureSessionSettings
import com.microblink.blinkidverify.core.data.model.result.BlinkIdVerifyCaptureResult
import com.microblink.blinkidverify.ux.capture.camera.compose.CameraScreen
import com.microblink.blinkidverify.ux.state.MbTorchState
import com.microblink.blinkidverify.ux.state.ProcessingState
import com.microblink.blinkidverify.ux.theme.BlinkIdVerifySdkTheme
import kotlinx.coroutines.launch

private const val TAG = "CameraScanningScreen"

/**
 * Composable function that provides a complete camera scanning screen using
 * the BlinkID Verify SDK.
 *
 * This composable function sets up and manages the entire camera scanning
 * process, including UI elements, camera interaction, and result handling. It
 * uses the provided [BlinkIdVerifySdk] and [CaptureSessionSettings] to
 * configure the scanning session and provides callbacks for handling
 * successful capture and cancellation.
 *
 * @param blinkIdVerifySdk The [BlinkIdVerifySdk] instance used for document
 *                         verification.
 * @param verifyUiSettings The [VerifyUiSettings] used to customize the UI.
 *                         Defaults to [VerifyUiSettings] with default values.
 * @param captureSessionSettings The [CaptureSessionSettings] used to configure
 *                               the capture session. Defaults to [CaptureSessionSettings] with default values.
 * @param onCaptureSuccess A callback function invoked when a document is
 *                         successfully captured. Receives the
 *                         [BlinkIdVerifyCaptureResult] as a parameter.
 * @param onCaptureCanceled A callback function invoked when the user cancels
 *                          the scanning process.
 *
 */
@Composable
fun CameraScanningScreen(
    blinkIdVerifySdk: BlinkIdVerifySdk,
    verifyUiSettings: VerifyUiSettings = VerifyUiSettings(),
    captureSessionSettings: CaptureSessionSettings = CaptureSessionSettings(),
    onCaptureSuccess: (BlinkIdVerifyCaptureResult) -> Unit,
    onCaptureCanceled: () -> Unit,
) {
    val viewModel: BlinkIdVerifyUxViewModel = viewModel(
        factory = BlinkIdVerifyUxViewModel.Factory,
        extras = MutableCreationExtras().apply {
            set(
                BlinkIdVerifyUxViewModel.DOCUMENT_VERIFY_SDK,
                blinkIdVerifySdk
            )
            set(
                BlinkIdVerifyUxViewModel.DOCUMENT_VERIFY_CAPTURE_SETTINGS,
                captureSessionSettings
            )
        }
    )

    var initialUiStateSet by rememberSaveable { mutableStateOf(false) }
    if (!initialUiStateSet) {
        viewModel.setInitialUiStateFromUiSettings(verifyUiSettings)
        initialUiStateSet = true
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarWarningMessage =
        stringResource(verifyUiSettings.verifySdkStrings.scanningStrings.snackbarFlashlightWarning)

    BlinkIdVerifySdkTheme(verifyUiSettings) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->

            CameraScreen(
                cameraViewModel = viewModel,
            ) {
                val overlayUiState = viewModel.uiState.collectAsStateWithLifecycle()

                if (overlayUiState.value.processingState == ProcessingState.Success) {
                    overlayUiState.value.blinkIdVerifyCaptureResult?.let {
                        onCaptureSuccess(it)
                    }
                }
                BackHandler {
                    onCaptureCanceled()
                }
                ScanningUx(
                    Modifier.padding(paddingValues),
                    overlayUiState.value,
                    onCaptureCanceled,
                    verifyUiSettings,
                    {
                        viewModel.changeTorchState()
                        viewModel.viewModelScope.launch {
                            if (viewModel.uiState.value.torchState == MbTorchState.On) {
                                snackbarHostState.showSnackbar(
                                    snackbarWarningMessage,
                                    null,
                                    false,
                                    SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    viewModel::onFlipAnimationCompleted,
                    viewModel::onReticleSuccessAnimationCompleted,
                    viewModel::changeOnboardingDialogVisibility,
                    viewModel::changeHelpScreensVisibility,
                    viewModel::changeHelpTooltipVisibility,
                    viewModel::onRetryTimeout,
                    onCaptureCanceled
                )
            }
        }
    }
}
