/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkidverify.ux.capture.scanning

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.microblink.blinkidverify.core.BlinkIdVerifySdk
import com.microblink.blinkidverify.core.capture.session.VerifyCaptureSession
import com.microblink.blinkidverify.core.capture.session.VerifyCaptureSessionSettings
import com.microblink.blinkidverify.ux.capture.settings.VerifyUxSettings
import com.microblink.core.RemoteLicenseCheckException
import com.microblink.core.image.InputImage
import com.microblink.ux.ScanningUxEventHandler
import com.microblink.ux.camera.ImageAnalyzer
import com.microblink.ux.utils.ErrorReason
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Analyzes images from the camera and processes them using the BlinkID Verify SDK.
 *
 * This class implements the [ImageAnalyzer] interface and is responsible for
 * receiving image frames from the camera, sending them to the BlinkID Verify
 * SDK for processing and results handling. It also manages the scanning
 * session, timeouts, and dispatches UI events.
 *
 * @property verifySdk An instance of the [BlinkIdVerifySdk] used for processing images.
 * @property captureSessionSettings The [VerifyCaptureSessionSettings] used to configure the capture session.
 * @property verifyScanningDoneHandler A [VerifyScanningDoneHandler] to handle the completion
 *                                of the scanning process.
 * @property uxEventHandler An optional [ScanningUxEventHandler] to handle UI events.
 *
 */
class VerifyAnalyzer(
    verifySdk: BlinkIdVerifySdk,
    captureSessionSettings: VerifyCaptureSessionSettings,
    private val uxSettings: VerifyUxSettings,
    private val verifyScanningDoneHandler: VerifyScanningDoneHandler,
    private val uxEventHandler: ScanningUxEventHandler? = null,
) : ImageAnalyzer {
    private val TAG = "VerifyAnalyzer"

    private var session: VerifyCaptureSession? = null
    private var analysisPaused = false
    private var firstImageTimestamp: Long? = null
    private val verifyScanningUxTranslator = VerifyScanningUxTranslator()
    private val stepTimeoutDuration: Duration? =
        if (uxSettings.stepTimeoutDuration == Duration.ZERO) null else uxSettings.stepTimeoutDuration

    init {
        CoroutineScope(Default).launch {
            session = verifySdk.createScanningSession(captureSessionSettings)
        }
    }

    /**
     * Analyzes an image from the camera.
     *
     * This function is called for each frame captured by the camera. It sends the
     * image to the BlinkID Verify SDK for processing and handles the results,
     * timeouts and cancellations.
     *
     * Current implementation of the analyzer cancels the session if the timeout occurs.
     * The timeout timer restarts every time the scanning is paused (onboarding dialog,
     * help dialog, card flip animation). Default timeout value can be checked at
     * [VerifyUxSettings.stepTimeoutDuration].
     *
     * @param image The [ImageProxy] containing the image to be analyzed.
     *
     */
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        if (analysisPaused) return
        if (firstImageTimestamp == null && stepTimeoutDuration != null) {
            firstImageTimestamp = System.nanoTime()
        }
        runBlocking {
            val inputImage = InputImage.createFromCameraXImageProxy(image)
            inputImage.use {
                session?.let { session ->
                    try {
                        val processResult = session.process(inputImage)
                        if (session.isCanceled) {
                            Log.w(TAG, "processing has been canceled")
                        } else {
                            uxEventHandler?.dispatchVerifyEvents(
                                verifyScanningUxTranslator,
                                processResult,
                                inputImage,
                                session
                            )

                            if (processResult.resultCompleteness.isComplete()) {
                                val sessionResult = session.getResult()
                                analysisPaused = true
                                verifyScanningDoneHandler.onScanningFinished(sessionResult)
                            } else if (stepTimeoutDuration != null) {
                                firstImageTimestamp?.let { timestamp ->
                                    val currentDuration =
                                        (System.nanoTime() - timestamp).toDuration(DurationUnit.NANOSECONDS)
                                    if (currentDuration > stepTimeoutDuration) {
                                        Log.w(TAG, "processing timeout occurred")
                                        analysisPaused = true
                                        verifyScanningDoneHandler.onError(ErrorReason.ErrorTimeoutExpired)
                                        // finish with whatever result we have
                                        firstImageTimestamp = null
                                    } else {
                                        Log.v(TAG, "continuing processing...")
                                    }
                                }
                            } else {
                                Log.v(TAG, "Neither complete nor timeout, continuing...")
                            }
                        }
                    } catch (e: RemoteLicenseCheckException) {
                        verifyScanningDoneHandler.onError(ErrorReason.ErrorInvalidLicense)
                    }
                }
            }
        }
    }

    override fun pauseAnalysis() {
        analysisPaused = true
        firstImageTimestamp = null
    }

    override fun resumeAnalysis() {
        analysisPaused = false
    }

    override fun cancel() {
        session?.cancelActiveProcess()
        verifyScanningDoneHandler.onScanningCanceled()
    }

    override fun restartAnalysis() {
        CoroutineScope(Default).launch {
            session?.restartSession()
        }
        analysisPaused = false
    }

    override fun close() {
        val s = session
        session = null
        s?.close()
    }
}
