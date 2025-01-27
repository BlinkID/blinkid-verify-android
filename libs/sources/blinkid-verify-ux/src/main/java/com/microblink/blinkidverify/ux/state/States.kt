package com.microblink.blinkidverify.ux.state

import com.microblink.blinkidverify.ux.components.flipAnimationDurationMs
import com.microblink.blinkidverify.ux.components.successAnimationDurationMs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

enum class ProcessingState(
    val reticleState: ReticleState,
    val minDuration: Duration
) {
    Sensing(ReticleState.Sensing, 1000.milliseconds),
    Processing(ReticleState.IndefiniteProgress, 1000.milliseconds),
    CardAnimation(ReticleState.Hidden, flipAnimationDurationMs.milliseconds),
    SuccessAnimation(ReticleState.Success, successAnimationDurationMs.milliseconds),
    Success(ReticleState.Hidden, 0.milliseconds),
    Error(ReticleState.Error, 1500.milliseconds),
    ErrorDialog(ReticleState.Hidden, 0.milliseconds)
}

enum class ReticleState {
    Hidden,
    Sensing,
    IndefiniteProgress,
    Success,
    Error
}