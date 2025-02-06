/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkidverify.ux.state

import com.microblink.blinkidverify.ux.components.flipAnimationDurationMs
import com.microblink.blinkidverify.ux.components.successAnimationDurationMs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Represents the different states of the reticle and UI elements during the scanning process.
 *
 * This enum class defines the various states that the UI can be in
 * while a document is being captured and processed. Each state is associated with
 * a specific [ReticleState] and a minimum duration.
 *
 * @property reticleState The [ReticleState] associated with this processing state.
 * @property minDuration The minimum duration that this state should last, it is
 *                      in milliseconds.
 *
 */
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

/**
 * Represents the different states of the reticle elements during the scanning process.
 */
enum class ReticleState {
    Hidden,
    Sensing,
    IndefiniteProgress,
    Success,
    Error
}