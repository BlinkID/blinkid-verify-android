package com.microblink.blinkidverify.sample.ui.navigation

import com.microblink.blinkidverify.core.data.model.result.BlinkIdVerifyEndpointResponse
import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object Main: Destination

    @Serializable
    data object DocumentCapture: Destination

    @Serializable
    data class VerifyResult(val documentVerificationResult: BlinkIdVerifyEndpointResponse): Destination
}