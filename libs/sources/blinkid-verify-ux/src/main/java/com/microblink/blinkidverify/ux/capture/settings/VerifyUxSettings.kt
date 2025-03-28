package com.microblink.blinkidverify.ux.capture.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Configuration settings for the scanning UX.
 *
 * @param stepTimeoutDuration Duration of the scanning session before a timeout is triggered. Resets every time the scanning is paused (dialogs, side change). Defaults to 15 seconds.
 */
@Parcelize
data class VerifyUxSettings(
    val stepTimeoutDuration: Duration = 15000.milliseconds
) : Parcelable
