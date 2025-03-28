/**
 * Copyright (c) Microblink. All rights reserved. This code is provided for
 * use as-is and may not be copied, modified, or redistributed.
 */

package com.microblink.blinkidverify.ux.capture.scanning

import com.microblink.blinkidverify.core.capture.session.VerifyCaptureSession
import com.microblink.blinkidverify.core.capture.session.VerifyProcessResult
import com.microblink.ux.ScanningUxEvent
import com.microblink.core.image.InputImage

/**
 * An interface that represents the translation process from [ScanningUxEvent] to the UX.
 */
interface VerifyUxTranslator {
    suspend fun translate(
        processResult: VerifyProcessResult,
        inputImage: InputImage?,
        session: VerifyCaptureSession,
    ): List<ScanningUxEvent>
}