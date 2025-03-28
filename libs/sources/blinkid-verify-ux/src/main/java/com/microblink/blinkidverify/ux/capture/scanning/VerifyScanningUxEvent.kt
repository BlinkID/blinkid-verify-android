/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkidverify.ux.capture.scanning

import com.microblink.blinkidverify.core.capture.session.DocumentLocation
import com.microblink.blinkidverify.core.capture.session.FrameAnalysisResult
import com.microblink.blinkidverify.core.capture.session.VerifyCaptureSession
import com.microblink.blinkidverify.core.capture.session.VerifyProcessResult
import com.microblink.blinkidverify.core.data.model.result.BlinkIdVerifyCaptureResult
import com.microblink.core.image.InputImage
import com.microblink.ux.ScanningUxEvent
import com.microblink.ux.ScanningUxEventHandler
import com.microblink.ux.utils.ErrorReason

interface VerifyScanningDoneHandler {
    fun onScanningFinished(result: BlinkIdVerifyCaptureResult)

    fun onScanningCanceled()

    fun onError(error: ErrorReason)
}

/**
 * Event that holds all the information about the currently analysed camera frame.
 *
 * @property frameAnalysisResult Represents the result of analyzing a single frame during document processing.
 */
data class DocumentFrameAnalysisResult(
    val frameAnalysisResult: FrameAnalysisResult
) : ScanningUxEvent

/**
 * The document has been located by the recognizer.
 *
 * @property location Specified the exact coordinates of the document and its orientation.
 * @property inputImage Image of the located document.
 */
data class DocumentLocatedLocation(
    val location: DocumentLocation,
    val inputImage: InputImage
) : ScanningUxEvent

/**
 * Dispatches user experience events to the [ScanningUxEventHandler] after translating
 * the process results.
 *
 * This extension function simplifies the process of translating [VerifyProcessResult] from
 * the scanning session into a list of [ScanningUxEvent] objects and then
 * dispatching these events to the [ScanningUxEventHandler].
 *
 * @receiver The [ScanningUxEventHandler] to which the translated events will be dispatched.
 * @param translator The [VerifyUxTranslator] used to translate the process result into
 *                   [ScanningUxEvent] objects.
 * @param processResult The [VerifyProcessResult] from the scanning session.
 * @param inputImage The [InputImage] used for the process. Can be `null`.
 * @param session The [VerifyCaptureSession] that was used for the process.
 */
suspend fun ScanningUxEventHandler.dispatchVerifyEvents(
    translator: VerifyUxTranslator,
    processResult: VerifyProcessResult,
    inputImage: InputImage?,
    session: VerifyCaptureSession
) {
    onUxEvents(translator.translate(processResult, inputImage, session))
}