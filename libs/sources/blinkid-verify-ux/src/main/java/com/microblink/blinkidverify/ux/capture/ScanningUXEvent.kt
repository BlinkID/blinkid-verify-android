package com.microblink.blinkidverify.ux.capture

import com.microblink.blinkidverify.core.capture.session.CaptureSession
import com.microblink.blinkidverify.core.capture.session.DocumentLocation
import com.microblink.blinkidverify.core.capture.session.FrameAnalysisResult
import com.microblink.blinkidverify.core.capture.session.ProcessResult
import com.microblink.blinkidverify.core.capture.session.image.InputImage
import com.microblink.blinkidverify.core.data.model.result.BlinkIDVerifyCaptureResult

interface ScanningDoneHandler {
    fun onScanningFinished(result: BlinkIDVerifyCaptureResult)

    fun onScanningCancelled()
}

sealed class ScanningUXEvent {

    data class RequestDocumentSide(
        val side: DocumentSide
    ) : ScanningUXEvent()

    class BlurDetected : ScanningUXEvent()

    class GlareDetected : ScanningUXEvent()

    class MoireDetected : ScanningUXEvent()

    class DocumentNotFound : ScanningUXEvent()

    class ScanningWrongSide : ScanningUXEvent()

    data class DocumentLocatedLocation(
        val location: DocumentLocation,
        val inputImage: InputImage
    ) : ScanningUXEvent()

    class DocumentLocated: ScanningUXEvent()

    class DocumentTooFar : ScanningUXEvent()

    class DocumentTooClose : ScanningUXEvent()

    class DocumentNotFullyVisible : ScanningUXEvent()

    class DocumentTooCloseToCameraEdge : ScanningUXEvent()

    class DocumentTooTilted : ScanningUXEvent()

    // Do we need it for both front and back?
    class ScanningDone: ScanningUXEvent()

    data class DocumentFrameAnalysisResult(
        val frameAnalysisResult: FrameAnalysisResult
    ) : ScanningUXEvent()

}

enum class DocumentSide {
    Front,
    Back,
    Barcode
}

interface ScanningUXEventHandler {
    fun onUXEvents(events: List<ScanningUXEvent>)
}

suspend fun ScanningUXEventHandler.dispatchEvents(translator: UXTranslator, processResult: ProcessResult, inputImage: InputImage?, session: CaptureSession) {
    onUXEvents(translator.translate(processResult, inputImage, session))
//    onUXEvents(translator.translate(processResult.resultCompleteness, processResult.frameAnalysisResult))
}