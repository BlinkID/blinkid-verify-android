package com.microblink.blinkidverify.sample.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.microblink.blinkidverify.core.data.model.result.BlinkIDVerifyCaptureResult
import com.microblink.blinkidverify.sample.R
import com.microblink.blinkidverify.sample.viewmodels.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    launchCameraCapture: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                   launchCameraCapture()
                }) {
                Text(text = stringResource(R.string.btn_launch_verification))
            }
        }

        if (uiState.displayLoading) {
            LoadingDialog()
        }

        uiState.captureResult?.let {
            CaptureResultDialog(
                result = it,
                onDismiss = {
                    viewModel.resetState()
                },
                onProcessImagesOnServerClicked = {
                    viewModel.sendVerifyRequestsFromCaptureResult(it)
                }
            )
        }
    }
}

@Composable
private fun LoadingDialog() {
    Dialog(
        onDismissRequest = {},
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.Center)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(16.dp),
                color = com.microblink.blinkidverify.sample.ui.theme.Cobalt
            )
        }
    }
}

@Composable
private fun CaptureResultDialog(
    result: BlinkIDVerifyCaptureResult,
    onDismiss: () -> Unit,
    onProcessImagesOnServerClicked: () -> Unit
) {
    Dialog(
        properties = DialogProperties(
            dismissOnClickOutside = false
        ),
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(
                    state = rememberScrollState()
                )
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                result.frontCameraFrame?.let { frontCameraFrame ->
                    Text(
                        text = stringResource(R.string.dialog_capture_results_front_image),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                    )
                    val frontCameraFrameBitmap = frontCameraFrame.toBitmap()
                    Image(
                        bitmap = frontCameraFrameBitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.dialog_capture_results_front_image),
                        contentScale = ContentScale.Inside,
                    )
                }
                result.backCameraFrame?.let { backCameraFrame ->
                    Text(
                        text = stringResource(R.string.dialog_capture_results_back_image),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                    )
                    val backCameraFrameBitmap = backCameraFrame.toBitmap()
                    Image(
                        bitmap = backCameraFrameBitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.dialog_capture_results_back_image),
                        contentScale = ContentScale.Inside,
                    )
                }
                result.barcodeCameraFrame?.let { barcodeCameraFrame ->
                    Text(
                        text = stringResource(R.string.dialog_capture_results_barcode_image),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                    )
                    Image(
                        bitmap = barcodeCameraFrame.toBitmap().asImageBitmap(),
                        contentDescription = stringResource(R.string.dialog_capture_results_barcode_image),
                        contentScale = ContentScale.Inside,
                    )
                }
                Button(
                    onClick = {
                        onProcessImagesOnServerClicked()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.dialog_capture_results_btn_process),
                        textAlign = TextAlign.Center
                    )
                }
                Button(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.btn_cancel),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
