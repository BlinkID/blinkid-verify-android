/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkidverify.ux.capture.scanning

import com.microblink.blinkidverify.core.capture.session.CaptureSession
import com.microblink.blinkidverify.core.capture.session.ProcessResult
import com.microblink.blinkidverify.core.capture.session.image.InputImage

/**
 * An interface that represents the translation process from [ScanningUxEvent] to the UX.
 */
interface UxTranslator {
    suspend fun translate(
        processResult: ProcessResult,
        inputImage: InputImage?,
        session: CaptureSession,
    ): List<ScanningUxEvent>
}