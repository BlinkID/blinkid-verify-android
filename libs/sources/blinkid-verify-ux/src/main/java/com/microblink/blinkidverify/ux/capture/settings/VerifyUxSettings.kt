package com.microblink.blinkidverify.ux.capture.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Configuration settings for the scanning UX.
 *
 * @param stepTimeoutDuration Duration of the scanning session before a timeout is triggered. Resets every time the scanning is paused (dialogs, side change). Defaults to 15 seconds.
 * @param allowHapticFeedback Whether haptic feedback is allowed during the scanning process. Defaults to true.
 */
@Parcelize
data class VerifyUxSettings(
    val stepTimeoutDuration: Duration = 15000.milliseconds,
    val allowHapticFeedback: Boolean = true
) : Parcelable {
    /**
     * Constructor for easier Java implementation.
     *
     * This secondary constructor allows Java developers to create a [VerifyUxSettings]
     * instance by providing the `stepTimeoutDuration` as an `Int` in milliseconds.
     *
     * @param stepTimeoutDurationMs Duration of the scanning session before a timeout is triggered
     * in milliseconds. Resets every time the scanning is paused (dialogs, side change). If set to 0, the scanning will not timeout.
     */
    constructor(stepTimeoutDurationMs: Int, allowHapticFeedback: Boolean = true) : this(stepTimeoutDurationMs.milliseconds, allowHapticFeedback)
}