package com.microblink.blinkidverify.ux

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.microblink.blinkidverify.core.BlinkIDVerifySdk
import com.microblink.blinkidverify.core.capture.session.CaptureSessionSettings
import com.microblink.blinkidverify.core.data.model.result.BlinkIDVerifyCaptureResult
import com.microblink.blinkidverify.ux.capture.camera.compose.CameraScreen
import com.microblink.blinkidverify.ux.state.MbTorchState
import com.microblink.blinkidverify.ux.state.ProcessingState
import com.microblink.blinkidverify.ux.theme.BlinkIDVerifySdkTheme
import kotlinx.coroutines.launch

private const val TAG = "CameraScanningScreen"

@Composable
fun CameraScanningScreen(
    blinkIDVerifySdk: BlinkIDVerifySdk,
    verifyUiSettings: VerifyUiSettings = VerifyUiSettings(),
    captureSessionSettings: CaptureSessionSettings,
    onCaptureSuccess: (BlinkIDVerifyCaptureResult) -> Unit,
    onCaptureCanceled: () -> Unit,
) {
    val viewModel: BlinkIDVerifyViewModel = viewModel(
        factory = BlinkIDVerifyViewModel.Factory,
        extras = MutableCreationExtras().apply {
            set(
                BlinkIDVerifyViewModel.DOCUMENT_VERIFY_SDK,
                blinkIDVerifySdk
            )
            set(
                BlinkIDVerifyViewModel.DOCUMENT_VERIFY_CAPTURE_SETTINGS,
                captureSessionSettings
            )
        }
    )
    viewModel.setInitialUiStateFromUiSettings(verifyUiSettings)
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarWarningMessage =
        stringResource(verifyUiSettings.verifySdkStrings.scanningStrings.snackbarFlashlightWarning)

    BlinkIDVerifySdkTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->

            CameraScreen(
                cameraViewModel = viewModel,
            ) {
                val overlayUiState = viewModel.uiState.collectAsStateWithLifecycle()

                if (overlayUiState.value.processingState == ProcessingState.Success) {
                    overlayUiState.value.blinkIDVerifyCaptureResult?.let {
                        onCaptureSuccess(it)
                    }
                }
                BackHandler {
                    onCaptureCanceled()
                }
                ScanningUX(
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
                    viewModel::onRetryTimeout
                )
            }
        }
    }
}
