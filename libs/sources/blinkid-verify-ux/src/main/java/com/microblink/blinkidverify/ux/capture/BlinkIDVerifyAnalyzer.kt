package com.microblink.blinkidverify.ux.capture

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.microblink.blinkidverify.core.BlinkIDVerifySdk
import com.microblink.blinkidverify.core.capture.session.CaptureSession
import com.microblink.blinkidverify.core.capture.session.CaptureSessionSettings
import com.microblink.blinkidverify.core.capture.session.image.InputImage
import com.microblink.blinkidverify.ux.capture.camera.ImageAnalyzer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class BlinkIDVerifyAnalyzer(
    verifySdk: BlinkIDVerifySdk,
    captureSessionSettings: CaptureSessionSettings,
    private val scanningDoneHandler: ScanningDoneHandler,
    private val uxEventHandler: ScanningUXEventHandler? = null,
) : ImageAnalyzer {
    private val TAG = "BlinkIDVerifyAnalyzer"

    private var session: CaptureSession? = null
    private var analysisPaused = false
    private var firstImageTimestamp: Long? = null
    private val scanningUXTranslator = ScanningUXTranslator()
    private val stepTimeoutDuration: Duration? = if (captureSessionSettings.stepTimeoutDuration == null || captureSessionSettings.stepTimeoutDuration == Duration.ZERO) null else captureSessionSettings.stepTimeoutDuration

    init {
        CoroutineScope(Default).launch {
            session = verifySdk.createScanningSession(captureSessionSettings)
        }
    }

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
                    val processResult = session.process(inputImage)
                    if (session.isCancelled) {
                        Log.w(TAG, "processing has been cancelled")
                    } else {
                        uxEventHandler?.dispatchEvents(
                            scanningUXTranslator,
                            processResult,
                            inputImage,
                            session
                        )

                        if (processResult.resultCompleteness.isComplete()) {
                            val sessionResult = session.getResult()
                            analysisPaused = true
                            scanningDoneHandler.onScanningFinished(sessionResult)
                        } else if (stepTimeoutDuration != null) {
                            firstImageTimestamp?.let { timestamp ->
                                val currentDuration =
                                    (System.nanoTime() - timestamp).toDuration(DurationUnit.NANOSECONDS)
                                if (currentDuration > stepTimeoutDuration) {
                                    Log.w(TAG, "processing timeout occurred")
                                    analysisPaused = true
                                    scanningDoneHandler.onScanningCancelled()
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
        scanningDoneHandler.onScanningCancelled()
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
