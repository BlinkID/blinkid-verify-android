/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkidverify.ux.capture.scanning

import com.microblink.blinkidverify.core.data.model.result.BlinkIdVerifyCaptureResult
import com.microblink.blinkidverify.core.data.imageanalysis.ExtractionImageAnalysisResult
import com.microblink.core.image.InputImage
import com.microblink.ux.ScanningUxEvent
import com.microblink.ux.ScanningUxEventHandler
import com.microblink.core.geometry.Quadrilateral
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
data class VerifyDocumentImageAnalysisResult(
    val extractionImageAnalysisResult: ExtractionImageAnalysisResult
) : ScanningUxEvent

/**
 * The document has been located by the recognizer.
 *
 * @property location Specified the exact coordinates of the document and its orientation.
 * @property inputImage Image of the located document.
 */
data class BlinkIdVerifyDocumentLocatedLocation(
    val location: Quadrilateral,
    val inputImage: InputImage
) : ScanningUxEvent
