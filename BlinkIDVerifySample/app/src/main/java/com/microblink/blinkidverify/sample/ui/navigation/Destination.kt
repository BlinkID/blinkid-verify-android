package com.microblink.blinkidverify.ui.navigation

import com.microblink.blinkidverify.core.data.model.result.BlinkIDVerifyEndpointResponse
import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object Main: Destination

    @Serializable
    data object DocumentCapture: Destination

    @Serializable
    data class VerifyResult(val documentVerificationResult: BlinkIDVerifyEndpointResponse): Destination
}