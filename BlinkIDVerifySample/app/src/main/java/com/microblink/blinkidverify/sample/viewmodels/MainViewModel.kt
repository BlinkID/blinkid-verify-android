package com.microblink.blinkidverify.sample.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microblink.blinkidverify.core.BlinkIdVerifyClient
import com.microblink.blinkidverify.core.BlinkIdVerifySdk
import com.microblink.blinkidverify.core.BlinkIdVerifySdkSettings
import com.microblink.blinkidverify.core.Response
import com.microblink.blinkidverify.core.capture.session.CapturePolicy
import com.microblink.blinkidverify.core.capture.session.VerifyCaptureSessionSettings
import com.microblink.blinkidverify.core.capture.session.ImageQualitySettings
import com.microblink.blinkidverify.core.data.model.request.BlinkIdVerifyProcessingRequestOptions
import com.microblink.blinkidverify.core.data.model.request.BlinkIdVerifyProcessingUseCase
import com.microblink.blinkidverify.core.data.model.request.BlinkIdVerifyRequest
import com.microblink.blinkidverify.core.data.model.result.BlinkIdVerifyCaptureResult
import com.microblink.blinkidverify.core.data.model.result.BlinkIdVerifyEndpointResponse
import com.microblink.blinkidverify.core.settings.BlinkIdVerifyServiceSettings
import com.microblink.blinkidverify.sample.config.BlinkIdVerifyConfig
import com.microblink.blinkidverify.ux.capture.settings.VerifyUxSettings
import com.microblink.ux.UiSettings
import com.microblink.ux.camera.CameraSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "MainViewModel"

@Serializable
data class MainState(
    val blinkidVerifyResult: BlinkIdVerifyEndpointResponse? = null,
    val error: String? = null,
)

data class UiState(
    val displayLoading: Boolean = false,
    val captureResult: BlinkIdVerifyCaptureResult? = null,
)

class MainViewModel : ViewModel() {
    private val _mainState = MutableStateFlow(MainState())
    var mainState = _mainState.asStateFlow()

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    var uiState = _uiState.asStateFlow()

    val blinkIDVerifyRequestOptionsConfig = BlinkIdVerifyProcessingRequestOptions()

    // TODO use constructor
    val blinkIDVerifyRequestUseCase = BlinkIdVerifyProcessingUseCase.Empty

    // TODO use settings options
    val blinkIDVerifyUiSettings = UiSettings()

    // TODO use camera settings
    val cameraSettings = CameraSettings()

    var stepTimeoutDuration: MutableState<Duration> = mutableStateOf(10000.milliseconds)
        private set

    val blinkIDVerifyUxSettings = VerifyUxSettings(
        stepTimeoutDuration = stepTimeoutDuration.value
    )

    var localSdk: BlinkIdVerifySdk? = null
        private set

    // TODO use BlinkIdVerifyProcessingRequestOptions.toCaptureSessionSettings that will be implemented in SDK
    val captureSessionSettings = VerifyCaptureSessionSettings(
        capturePolicy = CapturePolicy.Video,
        treatExpirationAsFraud = blinkIDVerifyRequestOptionsConfig.treatExpirationAsFraud,
        screenMatchLevel = blinkIDVerifyRequestOptionsConfig.screenMatchLevel,
        photocopyMatchLevel = blinkIDVerifyRequestOptionsConfig.photocopyMatchLevel,
        barcodeAnomalyMatchLevel = blinkIDVerifyRequestOptionsConfig.barcodeAnomalyMatchLevel,
        photoForgeryMatchLevel = blinkIDVerifyRequestOptionsConfig.photoForgeryMatchLevel,
        staticSecurityFeaturesMatchLevel = blinkIDVerifyRequestOptionsConfig.staticSecurityFeaturesMatchLevel,
        dataMatchMatchLevel = blinkIDVerifyRequestOptionsConfig.dataMatchMatchLevel,
        imageQualitySettings = ImageQualitySettings(
            blurMatchLevel = blinkIDVerifyRequestOptionsConfig.blurMatchLevel,
            glareMatchLevel = blinkIDVerifyRequestOptionsConfig.glareMatchLevel,
            lightingMatchLevel = blinkIDVerifyRequestOptionsConfig.lightingMatchLevel,
            sharpnessMatchLevel = blinkIDVerifyRequestOptionsConfig.sharpnessMatchLevel,
            handOcclusionMatchLevel = blinkIDVerifyRequestOptionsConfig.handOcclusionMatchLevel,
            dpiMatchLevel = blinkIDVerifyRequestOptionsConfig.dpiMatchLevel,
            tiltMatchLevel = blinkIDVerifyRequestOptionsConfig.tiltMatchLevel,
            imageQualityInterpretation = blinkIDVerifyRequestOptionsConfig.imageQualityInterpretation
        ),
        useCase = blinkIDVerifyRequestUseCase
    )

    fun sendVerifyRequestsFromCaptureResult(captureResult: BlinkIdVerifyCaptureResult) {
        _uiState.update {
            it.copy(displayLoading = true)
        }
        viewModelScope.launch {
            invokeServerProcessing(
                captureResult.toBlinkIdVerifyRequest(
                    blinkIDVerifyRequestOptionsConfig,
                    blinkIDVerifyRequestUseCase
                )
            )
        }
    }

    private suspend fun invokeServerProcessing(documentVerificationRequest: BlinkIdVerifyRequest) {
        _uiState.update {
            it.copy(displayLoading = true)
        }
        val client = BlinkIdVerifyClient(
            BlinkIdVerifyServiceSettings(
                verificationServiceBaseUrl = BlinkIdVerifyConfig.verificationServiceBaseUrl,
                token = BlinkIdVerifyConfig.verificationServiceToken,
            )
        )
        when (val response = client.verify(documentVerificationRequest)) {
            is Response.Error -> {
                Log.w(TAG, "Response is error: ${response.errorReason.name}")
                response.exception?.printStackTrace()
                _mainState.update {
                    it.copy(error = response.errorReason.name)
                }
            }

            is Response.Success -> {
                Log.i(
                    TAG,
                    "Response is success: processingStatus -> ${response.endpointResponse.processingStatus}"
                )
                Log.i(
                    TAG,
                    "recognitionStatus -> ${response.endpointResponse.extraction?.recognitionStatus}"
                )
                Log.i(TAG, "Recognition data: ${response.endpointResponse.checks.toString()}")
                _mainState.update {
                    it.copy(blinkidVerifyResult = response.endpointResponse)
                }
            }
        }
    }

    suspend fun initializeLocalSdk(context: Context) {
        _uiState.update {
            it.copy(displayLoading = true)
        }
        val maybeInstance = BlinkIdVerifySdk.initializeSdk(
            context = context,
            BlinkIdVerifySdkSettings(
                licenseKey = BlinkIdVerifyConfig.licenseKey,
            )
        )
        when {
            maybeInstance.isSuccess -> {
                localSdk = maybeInstance.getOrNull()
            }

            maybeInstance.isFailure -> {
                val exception = maybeInstance.exceptionOrNull()
                Log.e(TAG, "Initialization failed", exception)
                _mainState.update {
                    it.copy(error = "Initialization failed: ${exception?.message}")
                }
            }
        }
        _uiState.update {
            it.copy(displayLoading = false)
        }
    }

    fun onCaptureResultAvailable(captureResult: BlinkIdVerifyCaptureResult) {
        _uiState.update {
            it.copy(
                captureResult = captureResult
            )
        }
        // unload the SDK when not needed anymore to free up resources
        unloadSdk()
    }

    fun resetState() {
        _mainState.update { MainState() }
        _uiState.update { UiState() }
    }

    private fun unloadSdk() {
        try {
            // also delete cached resources
            localSdk?.closeAndDeleteCachedAssets()
        } catch (_: Exception) {
            Log.w(TAG, "SDK is already closed")
        }
        localSdk = null
    }
}