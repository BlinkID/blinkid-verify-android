/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkidverify.ux.capture.scanning

import com.microblink.blinkidverify.core.capture.session.CaptureSession
import com.microblink.blinkidverify.core.capture.session.DetectionStatus
import com.microblink.blinkidverify.core.capture.session.ProcessResult
import com.microblink.blinkidverify.core.capture.session.image.InputImage
import com.microblink.blinkidverify.core.data.model.result.extraction.ProcessingStatus
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Translates [ProcessResult] and other scanning session information into a
 * list of [ScanningUxEvent] objects.
 *
 * This class is responsible for interpreting the results of the document
 * scanning process and generating user experience-related events that can be
 * used to update the UI or provide feedback to the user. It handles logic
 * related to document sides, timeouts, and various detection statuses.
 *
 */
class ScanningUxTranslator : UxTranslator {

    private val backToBarcodeTimeout = 3.seconds
    private var barcodeDispatched = false

    private var currentSide = DocumentSide.Front

    private var firstBackRequestedTimestamp: Long? = null

    /**
     * Translates the given [ProcessResult], [InputImage], and [CaptureSession]
     * into a list of [ScanningUxEvent] objects.
     *
     * This function analyzes the current state of the scanning session and the
     * results of the last image processing step to determine which UX events
     * should be generated.
     *
     * @param processResult The [ProcessResult] from the scanning session.
     * @param inputImage The [InputImage] used for the process. Can be `null`.
     * @param session The [CaptureSession] that was used for the process.
     * @return A list of [ScanningUxEvent] objects representing the user
     *         experience events that should be dispatched.
     */
    override suspend fun translate(
        processResult: ProcessResult,
        inputImage: InputImage?,
        session: CaptureSession,
    ): List<ScanningUxEvent> {
        val events = mutableListOf<ScanningUxEvent>()
        var documentLocated = false

        val frameAnalysisResult = processResult.frameAnalysisResult

        if (processResult.resultCompleteness.overallFlowFinished) {
            events.add(ScanningUxEvent.ScanningDone())
            return events
        }

        if (currentSide == DocumentSide.Front) {
            if (processResult.resultCompleteness.frontSideFinished) {
                currentSide = DocumentSide.Back
                events.add(ScanningUxEvent.RequestDocumentSide(DocumentSide.Back))
            }
        } else if (currentSide == DocumentSide.Back) {
            if (!processResult.resultCompleteness.frontSideFinished) {
                currentSide = DocumentSide.Front
            } else if (firstBackRequestedTimestamp == null) {
                firstBackRequestedTimestamp = System.nanoTime()
            } else {
                if (shouldRequestBarcode(processResult)) {
                    session.setAllowBarcodeStep(true)
                    events.add(ScanningUxEvent.RequestDocumentSide(DocumentSide.Barcode))
                }
            }
        }
        if (events.isNotEmpty()) return events

        if (frameAnalysisResult.documentLocation != null) {
            events.add(
                if (inputImage != null) {
                    ScanningUxEvent.DocumentLocatedLocation(
                        location = frameAnalysisResult.documentLocation!!,
                        inputImage = inputImage
                    )
                } else {
                    ScanningUxEvent.DocumentLocated()
                }
            )
            documentLocated = true
        } else {
            events.add(ScanningUxEvent.DocumentNotFound())
        }

        // below just one event can be generated, by following priorities
        var hasEvents = false

        when (frameAnalysisResult.processingStatus) {

            ProcessingStatus.AwaitingOtherSide -> {
                events.add(ScanningUxEvent.RequestDocumentSide(side = currentSide))
                hasEvents = true
            }

            ProcessingStatus.ScanningWrongSide -> {
                events.add(ScanningUxEvent.ScanningWrongSide())
                hasEvents = true
            }

            else -> { }
        }

        if (hasEvents) {
            events.add(ScanningUxEvent.DocumentFrameAnalysisResult(frameAnalysisResult = frameAnalysisResult))
            return events
        }

        hasEvents = true

        when (frameAnalysisResult.detectionStatus) {
            DetectionStatus.CameraTooFar -> events.add(ScanningUxEvent.DocumentTooFar())
            DetectionStatus.CameraTooClose -> events.add(ScanningUxEvent.DocumentTooClose())
            DetectionStatus.DocumentPartiallyVisible -> events.add(ScanningUxEvent.DocumentNotFullyVisible())
            DetectionStatus.DocumentTooCloseToCameraEdge -> events.add(ScanningUxEvent.DocumentTooCloseToCameraEdge())
            DetectionStatus.CameraAngleTooSteep -> events.add(ScanningUxEvent.DocumentTooTilted())
            else -> {
                hasEvents = false
            }
        }

        if (hasEvents) {
            events.add(ScanningUxEvent.DocumentFrameAnalysisResult(frameAnalysisResult = frameAnalysisResult))
            return events
        }

        hasEvents = true

        if (frameAnalysisResult.blurDetected) events.add(ScanningUxEvent.BlurDetected())
        else if (frameAnalysisResult.glareDetected) events.add(ScanningUxEvent.GlareDetected())
        else if (frameAnalysisResult.occlusionDetected) events.add(ScanningUxEvent.DocumentNotFullyVisible())
        else if (frameAnalysisResult.tiltDetected) events.add(ScanningUxEvent.DocumentTooTilted())
        else hasEvents = false

        if (hasEvents) {
            events.add(ScanningUxEvent.DocumentFrameAnalysisResult(frameAnalysisResult = frameAnalysisResult))
            return events
        }

        if (documentLocated) events.add(ScanningUxEvent.DocumentNotFullyVisible())
        events.add(ScanningUxEvent.RequestDocumentSide(side = currentSide))
        events.add(ScanningUxEvent.DocumentFrameAnalysisResult(frameAnalysisResult = frameAnalysisResult))
        return events
    }

    private fun shouldRequestBarcode(processResult: ProcessResult): Boolean {
        return (System.nanoTime() - firstBackRequestedTimestamp!!).nanoseconds > backToBarcodeTimeout && processResult.frameAnalysisResult.hasBarcodeReadingIssues && !barcodeDispatched
    }
}