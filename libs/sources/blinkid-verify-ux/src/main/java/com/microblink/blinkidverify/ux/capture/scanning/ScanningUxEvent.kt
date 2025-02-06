/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkidverify.ux.capture.scanning

import com.microblink.blinkidverify.core.capture.session.CaptureSession
import com.microblink.blinkidverify.core.capture.session.DocumentLocation
import com.microblink.blinkidverify.core.capture.session.FrameAnalysisResult
import com.microblink.blinkidverify.core.capture.session.ProcessResult
import com.microblink.blinkidverify.core.capture.session.image.InputImage
import com.microblink.blinkidverify.core.data.model.result.BlinkIdVerifyCaptureResult
import com.microblink.blinkidverify.ux.state.UnrecoverableErrorState

interface ScanningDoneHandler {
    fun onScanningFinished(result: BlinkIdVerifyCaptureResult)

    fun onScanningCanceled()

    fun onError(error: UnrecoverableError)
}

sealed class ScanningUxEvent {
    /**
     * Request to scan a specific side of the document.
     *
     * @property side Specified the [DocumentSide] that is to be scanned.
     */
    data class RequestDocumentSide(
        val side: DocumentSide
    ) : ScanningUxEvent()

    /**
     * Camera image is too blurry for accurate document capture.
     */
    class BlurDetected : ScanningUxEvent()

    /**
     * Light reflection is interfering with document capture.
     */
    class GlareDetected : ScanningUxEvent()

    /**
     * No document has been located by the camera.
     */
    class DocumentNotFound : ScanningUxEvent()

    /**
     * Indicates the wrong side of the document is being presented.
     */
    class ScanningWrongSide : ScanningUxEvent()

    /**
     * The document has been located by the recognizer.
     *
     * @property location Specified the exact coordinates of the document and its orientation.
     * @property inputImage Image of the located document.
     */
    data class DocumentLocatedLocation(
        val location: DocumentLocation,
        val inputImage: InputImage
    ) : ScanningUxEvent()

    /**
     * The document has been located
     */
    class DocumentLocated: ScanningUxEvent()

    /**
     * Document is too far from the camera.
     */
    class DocumentTooFar : ScanningUxEvent()

    /**
     * Document is too close to the camera.
     */
    class DocumentTooClose : ScanningUxEvent()

    /*
     * Part of document is occluded or partially outside of the camera.
     */
    class DocumentNotFullyVisible : ScanningUxEvent()

    /**
     * Document is positioned too close to the screen edge.
     */
    class DocumentTooCloseToCameraEdge : ScanningUxEvent()

    /**
     * Document is not parallel to the camera plane.
     */
    class DocumentTooTilted : ScanningUxEvent()

    /**
     * Scanning has been successfully completed.
     */
    class ScanningDone: ScanningUxEvent()

    /**
     * Event that holds all the information about the currently analysed camera frame.
     *
     * @property frameAnalysisResult Represents the result of analyzing a single frame during document processing.
     */
    data class DocumentFrameAnalysisResult(
        val frameAnalysisResult: FrameAnalysisResult
    ) : ScanningUxEvent()

}

/**
 * Specifies the current document side.
 */
enum class DocumentSide {
    Front,
    Back,
    Barcode
}

/**
 * Specifies the unrecoverable error reason.
 */
enum class UnrecoverableError {
    ErrorInvalidLicense,
    ErrorNetworkError,
    ErrorTimeoutExpired
}

fun UnrecoverableError.toUnrecoverableErrorState(): UnrecoverableErrorState {
    return when(this) {
        UnrecoverableError.ErrorInvalidLicense -> UnrecoverableErrorState.ErrorInvalidLicense
        UnrecoverableError.ErrorNetworkError -> UnrecoverableErrorState.ErrorNetworkError
        UnrecoverableError.ErrorTimeoutExpired -> UnrecoverableErrorState.ErrorTimeoutExpired
    }
}

interface ScanningUxEventHandler {
    fun onUxEvents(events: List<ScanningUxEvent>)
}

/**
 * Dispatches user experience events to the [ScanningUxEventHandler] after translating
 * the process results.
 *
 * This extension function simplifies the process of translating [ProcessResult] from
 * the scanning session into a list of [ScanningUxEvent] objects and then
 * dispatching these events to the [ScanningUxEventHandler].
 *
 * @receiver The [ScanningUxEventHandler] to which the translated events will be dispatched.
 * @param translator The [UxTranslator] used to translate the process result into
 *                   [ScanningUxEvent] objects.
 * @param processResult The [ProcessResult] from the scanning session.
 * @param inputImage The [InputImage] used for the process. Can be `null`.
 * @param session The [CaptureSession] that was used for the process.
 */
suspend fun ScanningUxEventHandler.dispatchEvents(translator: UxTranslator, processResult: ProcessResult, inputImage: InputImage?, session: CaptureSession) {
    onUxEvents(translator.translate(processResult, inputImage, session))
}