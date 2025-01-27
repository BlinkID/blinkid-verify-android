package com.microblink.blinkidverify.ux.capture.camera

import androidx.camera.core.ImageAnalysis

interface ImageAnalyzer : ImageAnalysis.Analyzer, AutoCloseable {
    fun cancel()
    fun pauseAnalysis()
    fun resumeAnalysis()
    fun restartAnalysis()
}