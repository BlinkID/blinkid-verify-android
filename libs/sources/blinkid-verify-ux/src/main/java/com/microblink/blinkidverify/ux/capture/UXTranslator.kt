package com.microblink.blinkidverify.ux.capture

import com.microblink.blinkidverify.core.capture.session.CaptureSession
import com.microblink.blinkidverify.core.capture.session.ProcessResult
import com.microblink.blinkidverify.core.capture.session.image.InputImage

interface UXTranslator {
    suspend fun translate(
        processResult: ProcessResult,
        inputImage: InputImage?,
        session: CaptureSession,
    ): List<ScanningUXEvent>
}