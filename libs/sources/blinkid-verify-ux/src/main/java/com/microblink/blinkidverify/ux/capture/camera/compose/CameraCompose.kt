/*
 * Copyright (c) 2024 Microblink Ltd. All rights reserved.
 *
 * ANY UNAUTHORIZED USE OR SALE, DUPLICATION, OR DISTRIBUTION
 * OF THIS PROGRAM OR ANY OF ITS PARTS, IN SOURCE OR BINARY FORMS,
 * WITH OR WITHOUT MODIFICATION, WITH THE PURPOSE OF ACQUIRING
 * UNLAWFUL MATERIAL OR ANY OTHER BENEFIT IS PROHIBITED!
 * THIS PROGRAM IS PROTECTED BY COPYRIGHT LAWS AND YOU MAY NOT
 * REVERSE ENGINEER, DECOMPILE, OR DISASSEMBLE IT.
 */

package com.microblink.blinkidverify.ux.capture.camera.compose

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.microblink.blinkidverify.ux.R
import com.microblink.blinkidverify.ux.capture.camera.CameraLensFacing
import com.microblink.blinkidverify.ux.capture.camera.CameraSettings
import com.microblink.blinkidverify.ux.capture.camera.CameraViewModel
import com.microblink.blinkidverify.ux.theme.Cobalt
import com.microblink.blinkidverify.ux.theme.White
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraScreen(
    cameraViewModel: CameraViewModel,
    cameraSettings: CameraSettings = CameraSettings(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val cameraPermissionGranted = remember { mutableStateOf(isCameraPermissionGranted(context)) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionGranted.value = isGranted
    }
    val torchOn = cameraViewModel.torchOn.collectAsStateWithLifecycle()

    LifecycleStartEffect(Unit) {
        cameraPermissionGranted.value = isCameraPermissionGranted(context)
        onStopOrDispose { }
    }
    if (cameraPermissionGranted.value) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CameraPreview(
                cameraSettings,
                torchOn.value
            ) { imageProxy -> cameraViewModel.analyzeImage(imageProxy) }
            content()
        }

    } else {
        LaunchedEffect(Unit) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        CameraPermissionDeniedScreen {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

@Composable
private fun CameraPreview(
    cameraSettings: CameraSettings = CameraSettings(),
    torchOn: Boolean,
    imageAnalyzer: (ImageProxy) -> Unit,
) {
    val lensFacing = when (cameraSettings.lensFacing) {
        CameraLensFacing.LENS_FACING_BACK -> CameraSelector.LENS_FACING_BACK
        CameraLensFacing.LENS_FACING_FRONT -> CameraSelector.LENS_FACING_FRONT
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val previewView = remember {
        PreviewView(context)
    }
    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }
    val cameraControl = remember { mutableStateOf<CameraControl?>(null) }

    LaunchedEffect(lensFacing) {
        val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val resolutionSelector = ResolutionSelector.Builder()
            .setResolutionStrategy(
                ResolutionStrategy(
                    Size(
                        cameraSettings.desiredResolution.width,
                        cameraSettings.desiredResolution.height
                    ),
                    ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                )
            )
            .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
            .build()

        val cameraProvider = context.getCameraProvider()

        val previewUseCase = Preview.Builder()
            .setResolutionSelector(resolutionSelector)
            .build()
        val imageAnalysisUseCase = ImageAnalysis.Builder().apply {
            setResolutionSelector(resolutionSelector)
        }.build()
        imageAnalysisUseCase.setAnalyzer(
            cameraExecutor
        ) { imageProxy ->
            imageAnalyzer(imageProxy)
        }

        val useCaseGroup = UseCaseGroup.Builder().apply {
            previewView.viewPort?.let {
                setViewPort(it)
            }
            addUseCase(previewUseCase)
            addUseCase(imageAnalysisUseCase)
        }.build()

        cameraProvider.unbindAll()
        val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, useCaseGroup)
        previewUseCase.surfaceProvider = previewView.surfaceProvider
        enableTapToFocus(previewView, camera.cameraControl)
        camera.cameraControl.enableTorch(torchOn)
        cameraControl.value = camera.cameraControl
    }

    LaunchedEffect(torchOn) {
        cameraControl.value?.enableTorch(torchOn)
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier
            .fillMaxSize()
    )
}

@Composable
fun CameraPermissionDeniedScreen(requestCameraPermission: () -> Unit) {
    val shouldShowDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF888888)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.mb_camera_denied),
                contentDescription = stringResource(id = R.string.mb_camera_permission_required),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.mb_camera_permission_required),
                color = White,
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(colors = ButtonDefaults.buttonColors().copy(containerColor = Cobalt), onClick = {
                val shouldShowRequestPermissionRationale = (context as? Activity)?.let {
                    shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
                } ?: false
                if (shouldShowRequestPermissionRationale) {
                    requestCameraPermission()
                } else {
                    shouldShowDialog.value = true
                }
            }) {
                Text(text = stringResource(id = R.string.mb_enable_camera))
            }
        }

        if (shouldShowDialog.value) {
            AlertDialog(
                onDismissRequest = { shouldShowDialog.value = false },
                title = { Text(text = stringResource(R.string.mb_warning_title)) },
                text = { Text(text = stringResource(R.string.mb_enable_permission_help)) },
                containerColor = Color.White,
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors().copy(containerColor = Cobalt),
                        onClick = {
                            shouldShowDialog.value = false
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse(
                                        "package:" + context.packageName
                                    )
                                )
                            )
                        }
                    ) {
                        Text(
                            text = stringResource(com.microblink.blinkidverify.ux.R.string.mb_ok),
                        )
                    }
                }
            )
        }

    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                @Suppress("BlockingMethodInNonBlockingContext")
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

@SuppressLint("ClickableViewAccessibility")
private fun enableTapToFocus(previewView: PreviewView, cameraControl: CameraControl) {
    previewView.setOnTouchListener { view, motionEvent ->
        val meteringPoint = previewView.meteringPointFactory
            .createPoint(motionEvent.x, motionEvent.y)
        val action = FocusMeteringAction.Builder(meteringPoint) // default AF|AE|AWB
            // The action is canceled in 3 seconds (if not set, default is 5s).
            .setAutoCancelDuration(3, TimeUnit.SECONDS)
            .build()
        cameraControl.startFocusAndMetering(action)
        // ClickableViewAccessibility: onTouch lambda should call View#performClick when a click is detected
        view.performClick()
        false
    }
}

private fun isCameraPermissionGranted(context: Context) =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
