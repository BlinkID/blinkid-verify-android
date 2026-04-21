/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkidverify.ux.capture.scanning

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.microblink.blinkidverify.core.BlinkIdVerifySdk
import com.microblink.blinkidverify.core.capture.session.BlinkIdVerifyScanningSession
import com.microblink.blinkidverify.core.capture.session.BlinkIdVerifySessionSettings
import com.microblink.core.RemoteLicenseCheckException
import com.microblink.core.image.InputImage
import com.microblink.core.utils.MbLog
import com.microblink.ux.ScanningUxEvent
import com.microblink.ux.ScanningUxEventHandler
import com.microblink.ux.camera.ImageAnalyzer
import com.microblink.ux.state.UiScanningSide
import com.microblink.ux.utils.ErrorReason
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val TAG = "BlinkIdVerifyAnalyzer"

/**
 * Analyzes images from the camera and processes them using the BlinkID Verify SDK.
 *
 * This class implements the [ImageAnalyzer] interface and is responsible for
 * receiving image frames from the camera, sending them to the BlinkID Verify
 * SDK for processing and results handling. It also manages the scanning
 * session, timeouts, and dispatches UI events.
 *
 * @property verifySdk An instance of the [BlinkIdVerifySdk] used for processing images.
 * @property sessionSettings The [BlinkIdVerifySessionSettings] used to configure the capture session.
 * @property verifyScanningDoneHandler A [VerifyScanningDoneHandler] to handle the completion
 *                                of the scanning process.
 * @property uxEventHandler An optional [ScanningUxEventHandler] to handle UI events.
 *
 */
class BlinkIdVerifyAnalyzer(
    verifySdk: BlinkIdVerifySdk,
    sessionSettings: BlinkIdVerifySessionSettings,
    private val verifyScanningDoneHandler: VerifyScanningDoneHandler,
    private val uxEventHandler: ScanningUxEventHandler? = null,
) : ImageAnalyzer {

    private var session: BlinkIdVerifyScanningSession? =
        runBlocking { verifySdk.createScanningSession(sessionSettings) }
    private var analysisPaused = false
    private val verifyScanningUxTranslator = VerifyScanningUxTranslator()

    /**
     * Analyzes an image from the camera.
     *
     * This function is called for each frame captured by the camera. It sends the
     * image to the BlinkID Verify SDK for processing and handles the results,
     * timeouts and cancellations.
     *
     * @param image The [ImageProxy] containing the image to be analyzed.
     *
     */
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        if (analysisPaused) return
        runBlocking {
            val inputImage = InputImage.createFromCameraXImageProxy(image)
            inputImage.use {
                session?.let { session ->
                    try {
                        val sessionProcessResult = session.process(inputImage)
                        if (session.isCanceled) {
                            MbLog.w(TAG) { "processing has been canceled" }
                        } else {
                            sessionProcessResult.getOrNull()?.let { processResult ->
                                val events = verifyScanningUxTranslator.translate(
                                    processResult,
                                    inputImage
                                )

                                if (events.any { it is ScanningUxEvent.RequestSide && it.side == UiScanningSide.Barcode }) {
                                    session.setAllowBarcodeStep(true)
                                }

                                uxEventHandler?.onUxEvents(events)

                                if (processResult.resultCompleteness.isComplete()) {
                                    val sessionResult = session.getResult()
                                    pauseAnalysis()
                                    verifyScanningDoneHandler.onScanningFinished(sessionResult)
                                } else {
                                    MbLog.v(TAG) { "Neither complete nor timeout, continuing..." }
                                }
                            }
                        }
                    } catch (_: RemoteLicenseCheckException) {
                        verifyScanningDoneHandler.onError(ErrorReason.ErrorInvalidLicense)
                    }
                }
            }
        }
    }

    override fun pauseAnalysis() {
        analysisPaused = true
    }

    override fun resumeAnalysis() {
        analysisPaused = false
    }

    override fun timeoutAnalysis() {
        MbLog.e(TAG) { "processing timeout occurred" }
        analysisPaused = true

        // TODO now that this is called in restartAnalysis, maybe we don't need it here?
        verifyScanningUxTranslator.resetSession()

        verifyScanningDoneHandler.onError(ErrorReason.ErrorTimeoutExpired)
    }

    fun getSessionNumber(): Int? {
        return session?.sessionNumber
    }

    override fun cancel() {
        session?.cancelActiveProcess()
        verifyScanningDoneHandler.onScanningCanceled()
    }

    override fun restartAnalysis() {
        CoroutineScope(Default).launch {
            session?.restartSession()
        }

        // this was added for the Unsupported Dialog, so it can be reset properly
        verifyScanningUxTranslator.resetSession()

        analysisPaused = false
    }

    override fun close() {
        session?.also { s ->
            session = null
            CoroutineScope(IO).launch {
                s.close()
            }
        }
    }
}
