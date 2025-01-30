package com.microblink.blinkidverify.ux.capture

import com.microblink.blinkidverify.core.capture.session.CaptureSession
import com.microblink.blinkidverify.core.capture.session.DetectionStatus
import com.microblink.blinkidverify.core.capture.session.ProcessResult
import com.microblink.blinkidverify.core.capture.session.image.InputImage
import com.microblink.blinkidverify.core.data.model.result.extraction.ProcessingStatus
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class ScanningUXTranslator : UXTranslator {

    private val backToBarcodeTimeout = 3.seconds
    private var barcodeDispatched = false

    private var currentSide = DocumentSide.Front

    private var firstBackRequestedTimestamp: Long? = null

    // Roughly based on https://miro.com/app/board/uXjVLLJ7Uso=/
    override suspend fun translate(
        processResult: ProcessResult,
        inputImage: InputImage?,
        session: CaptureSession,
    ): List<ScanningUXEvent> {
        val events = mutableListOf<ScanningUXEvent>()

        val frameAnalysisResult = processResult.frameAnalysisResult

        if (processResult.resultCompleteness.overallFlowFinished) {
            events.add(ScanningUXEvent.ScanningDone())
            return events
        }

        if (currentSide == DocumentSide.Front) {
            if (processResult.resultCompleteness.frontSideFinished) {
                currentSide = DocumentSide.Back
                events.add(ScanningUXEvent.RequestDocumentSide(DocumentSide.Back))
            }
        } else if (currentSide == DocumentSide.Back) {
            if (!processResult.resultCompleteness.frontSideFinished) {
                currentSide = DocumentSide.Front
            } else if (firstBackRequestedTimestamp == null) {
                firstBackRequestedTimestamp = System.nanoTime()
            } else {
                if (shouldRequestBarcode(processResult)) {
                    session.setAllowBarcodeStep(true)
                    events.add(ScanningUXEvent.RequestDocumentSide(DocumentSide.Barcode))
                }
            }
        }
        if (events.isNotEmpty()) return events

        if (frameAnalysisResult.documentLocation != null) {
            events.add(
                if (inputImage != null) {
                    ScanningUXEvent.DocumentLocatedLocation(
                        location = frameAnalysisResult.documentLocation!!,
                        inputImage = inputImage
                    )
                } else {
                    ScanningUXEvent.DocumentLocated()
                }
            )
        } else {
            events.add(ScanningUXEvent.DocumentNotFound())
        }

        // below just one event can be generated, by following priorities
        var hasEvents = false

        when (frameAnalysisResult.processingStatus) {

            ProcessingStatus.AwaitingOtherSide -> {
                events.add(ScanningUXEvent.RequestDocumentSide(side = currentSide))
                hasEvents = true
            }

            ProcessingStatus.ScanningWrongSide -> {
                events.add(ScanningUXEvent.ScanningWrongSide())
                hasEvents = true
            }

            else -> { }
        }

        if (hasEvents) {
            events.add(ScanningUXEvent.DocumentFrameAnalysisResult(frameAnalysisResult = frameAnalysisResult))
            return events
        }

        hasEvents = true

        when (frameAnalysisResult.detectionStatus) {
            DetectionStatus.CameraTooFar -> events.add(ScanningUXEvent.DocumentTooFar())
            DetectionStatus.CameraTooClose -> events.add(ScanningUXEvent.DocumentTooClose())
            DetectionStatus.DocumentPartiallyVisible -> events.add(ScanningUXEvent.DocumentNotFullyVisible())
            DetectionStatus.DocumentTooCloseToCameraEdge -> events.add(ScanningUXEvent.DocumentTooCloseToCameraEdge())
            DetectionStatus.CameraAngleTooSteep -> events.add(ScanningUXEvent.DocumentTooTilted())
            else -> {
                hasEvents = false
            }
        }

        if (hasEvents) {
            events.add(ScanningUXEvent.DocumentFrameAnalysisResult(frameAnalysisResult = frameAnalysisResult))
            return events
        }

        hasEvents = true

        if (frameAnalysisResult.blurDetected) events.add(ScanningUXEvent.BlurDetected())
        else if (frameAnalysisResult.glareDetected) events.add(ScanningUXEvent.GlareDetected())
        else if (frameAnalysisResult.occlusionDetected) events.add(ScanningUXEvent.DocumentNotFullyVisible())
        else if (frameAnalysisResult.tiltDetected) events.add(ScanningUXEvent.DocumentTooTilted())
        else hasEvents = false

        if (hasEvents) {
            events.add(ScanningUXEvent.DocumentFrameAnalysisResult(frameAnalysisResult = frameAnalysisResult))
            return events
        }

        events.add(ScanningUXEvent.RequestDocumentSide(side = currentSide))
        events.add(ScanningUXEvent.DocumentFrameAnalysisResult(frameAnalysisResult = frameAnalysisResult))
        return events
    }

    private fun shouldRequestBarcode(processResult: ProcessResult): Boolean {
        return (System.nanoTime() - firstBackRequestedTimestamp!!).nanoseconds > backToBarcodeTimeout && processResult.frameAnalysisResult.hasBarcodeReadingIssues && !barcodeDispatched
    }
}