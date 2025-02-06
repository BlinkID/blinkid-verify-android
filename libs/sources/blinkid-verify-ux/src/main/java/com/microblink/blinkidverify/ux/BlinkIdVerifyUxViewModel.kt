/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkidverify.ux

import android.os.CountDownTimer
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.microblink.blinkidverify.core.BlinkIdVerifySdk
import com.microblink.blinkidverify.core.capture.session.CaptureSessionSettings
import com.microblink.blinkidverify.core.data.model.result.BlinkIdVerifyCaptureResult
import com.microblink.blinkidverify.ux.capture.scanning.DocumentSide
import com.microblink.blinkidverify.ux.capture.scanning.BlinkIdVerifyAnalyzer
import com.microblink.blinkidverify.ux.capture.scanning.ScanningDoneHandler
import com.microblink.blinkidverify.ux.capture.scanning.ScanningUxEvent
import com.microblink.blinkidverify.ux.capture.scanning.ScanningUxEventHandler
import com.microblink.blinkidverify.ux.capture.camera.CameraViewModel
import com.microblink.blinkidverify.ux.capture.camera.ImageAnalyzer
import com.microblink.blinkidverify.ux.capture.scanning.UnrecoverableError
import com.microblink.blinkidverify.ux.capture.scanning.toUnrecoverableErrorState
import com.microblink.blinkidverify.ux.state.CardAnimationState
import com.microblink.blinkidverify.ux.state.VerifyUiState
import com.microblink.blinkidverify.ux.state.MbTorchState
import com.microblink.blinkidverify.ux.state.ProcessingState
import com.microblink.blinkidverify.ux.state.ReticleState
import com.microblink.blinkidverify.ux.state.StatusMessage
import com.microblink.blinkidverify.ux.state.UnrecoverableErrorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class BlinkIdVerifyUxViewModel(
    blinkIdVerifySdkInstance: BlinkIdVerifySdk,
    captureSessionSettings: CaptureSessionSettings,
) :
    CameraViewModel() {
    private var imageAnalyzer: ImageAnalyzer? = null

    private val _uiState = MutableStateFlow(VerifyUiState())
    val uiState: StateFlow<VerifyUiState> = _uiState.asStateFlow()

    var uiStateStartTime: Duration = Duration.ZERO
    lateinit var lastEvent: ScanningUxEvent
    var isScanningDone: Boolean = false

    private val helpTooltipTimer = object : CountDownTimer(8000, 8000) {
        override fun onTick(millisUntilFinished: Long) {
            { }
        }

        override fun onFinish() {
            changeHelpTooltipVisibility(true)
        }

    }

    init {
        viewModelScope.launch {
            imageAnalyzer = BlinkIdVerifyAnalyzer(
                verifySdk = blinkIdVerifySdkInstance,
                captureSessionSettings = captureSessionSettings,
                scanningDoneHandler = object : ScanningDoneHandler {
                    override fun onScanningFinished(result: BlinkIdVerifyCaptureResult) {
                        _uiState.update {
                            it.copy(blinkIdVerifyCaptureResult = result)
                        }
                    }

                    override fun onError(error: UnrecoverableError) {
                        _uiState.update {
                            it.copy(
                                unrecoverableErrorState = error.toUnrecoverableErrorState(),
                                processingState = ProcessingState.ErrorDialog
                            )
                        }
                    }

                    override fun onScanningCanceled() { }
                },
                uxEventHandler = object : ScanningUxEventHandler {
                    override fun onUxEvents(events: List<ScanningUxEvent>) {
                        for (event in events) {
                            when (event) {
                                is ScanningUxEvent.ScanningDone -> {
                                    isScanningDone = true
                                    imageAnalyzer?.pauseAnalysis()

                                    _uiState.update {
                                        it.copy(
                                            statusMessage = StatusMessage.Empty,
                                            processingState = ProcessingState.SuccessAnimation,
                                            reticleState = ReticleState.Success
                                        )
                                    }
                                    break
                                }

                                is ScanningUxEvent.DocumentNotFound -> {
                                    val newProcessingState = ProcessingState.Sensing

                                    if (!shouldStayInCurrentState(
                                            uiState.value.processingState,
                                            newProcessingState
                                        )
                                    ) {
                                        _uiState.update {
                                            it.copy(
                                                processingState = newProcessingState,
                                                statusMessage = if (it.currentSide == DocumentSide.Front) StatusMessage.ScanFrontSide
                                                else if (it.currentSide == DocumentSide.Back) StatusMessage.ScanBackSide
                                                else StatusMessage.ScanBarcode
                                            )
                                        }
                                        updateStateStartTime()
                                    }

                                }

                                is ScanningUxEvent.DocumentLocated, is ScanningUxEvent.DocumentLocatedLocation -> {
                                    val newProcessingState = ProcessingState.Processing
                                    if (!shouldStayInCurrentState(
                                            uiState.value.processingState,
                                            newProcessingState
                                        )
                                    ) {
                                        _uiState.update {
                                            it.copy(
                                                processingState = newProcessingState,
                                                statusMessage = StatusMessage.Empty
                                            )
                                        }
                                        updateStateStartTime()
                                    }
                                }


                                is ScanningUxEvent.BlurDetected -> {
                                    val newProcessingState = ProcessingState.Error
                                    if (shouldStayInCurrentState(
                                            uiState.value.processingState,
                                            newProcessingState
                                        )
                                    ) break
                                    _uiState.update {
                                        it.copy(
                                            processingState = newProcessingState,
                                            statusMessage = StatusMessage.EliminateBlur
                                        )
                                    }
                                    updateStateStartTime()
                                }

                                is ScanningUxEvent.DocumentNotFullyVisible -> {
                                    val newProcessingState = ProcessingState.Error
                                    if (shouldStayInCurrentState(
                                            uiState.value.processingState,
                                            newProcessingState
                                        )
                                    ) break
                                    _uiState.update {
                                        it.copy(
                                            processingState = newProcessingState,
                                            statusMessage = StatusMessage.KeepDocumentVisible
                                        )
                                    }
                                    updateStateStartTime()
                                }

                                is ScanningUxEvent.DocumentTooClose -> {
                                    val newProcessingState = ProcessingState.Error
                                    if (shouldStayInCurrentState(
                                            uiState.value.processingState,
                                            newProcessingState
                                        )
                                    ) break
                                    _uiState.update {
                                        it.copy(
                                            processingState = newProcessingState,
                                            statusMessage = StatusMessage.MoveFarther
                                        )
                                    }
                                    updateStateStartTime()
                                }

                                is ScanningUxEvent.DocumentTooCloseToCameraEdge -> {
                                    val newProcessingState = ProcessingState.Error
                                    if (shouldStayInCurrentState(
                                            uiState.value.processingState,
                                            ProcessingState.Error
                                        )
                                    ) break
                                    _uiState.update {
                                        it.copy(
                                            processingState = newProcessingState,
                                            statusMessage = StatusMessage.MoveDocumentFromEdge
                                        )
                                    }
                                    updateStateStartTime()
                                }

                                is ScanningUxEvent.DocumentTooFar -> {
                                    val newProcessingState = ProcessingState.Error
                                    if (shouldStayInCurrentState(
                                            uiState.value.processingState,
                                            newProcessingState
                                        )
                                    ) break
                                    _uiState.update {
                                        it.copy(
                                            processingState = newProcessingState,
                                            statusMessage = StatusMessage.MoveCloser
                                        )
                                    }
                                    updateStateStartTime()
                                }

                                is ScanningUxEvent.DocumentTooTilted -> {
                                    val newProcessingState = ProcessingState.Error
                                    if (shouldStayInCurrentState(
                                            uiState.value.processingState,
                                            newProcessingState
                                        )
                                    ) break
                                    _uiState.update {
                                        it.copy(
                                            processingState = newProcessingState,
                                            statusMessage = StatusMessage.AlignDocument
                                        )
                                    }
                                    updateStateStartTime()
                                }

                                is ScanningUxEvent.GlareDetected -> {
                                    val newProcessingState = ProcessingState.Error
                                    if (shouldStayInCurrentState(
                                            uiState.value.processingState,
                                            newProcessingState
                                        )
                                    ) break
                                    _uiState.update {
                                        it.copy(
                                            processingState = newProcessingState,
                                            statusMessage = StatusMessage.EliminateGlare
                                        )
                                    }
                                    updateStateStartTime()
                                }

                                is ScanningUxEvent.ScanningWrongSide -> {
                                    _uiState.update {
                                        it.copy(
                                            processingState = ProcessingState.Error,
                                            statusMessage = StatusMessage.ScanningWrongSide
                                        )
                                    }
                                    updateStateStartTime()
                                }

                                is ScanningUxEvent.RequestDocumentSide -> {
                                    var currentSide: DocumentSide = uiState.value.currentSide
                                    var statusMessage: StatusMessage =
                                        uiState.value.statusMessage
                                    var cardAnimationState: CardAnimationState =
                                        uiState.value.cardAnimationState
                                    var processingState: ProcessingState =
                                        uiState.value.processingState

                                    when (uiState.value.currentSide) {
                                        DocumentSide.Front -> {
                                            when (event.side) {
                                                DocumentSide.Front -> {
                                                }

                                                DocumentSide.Back -> {
                                                    // TODO: support for portrait animations
                                                    processingState =
                                                        ProcessingState.SuccessAnimation
                                                    statusMessage = StatusMessage.Empty
                                                    imageAnalyzer?.pauseAnalysis()
                                                }

                                                DocumentSide.Barcode -> {
                                                    processingState = ProcessingState.Sensing
                                                    statusMessage = StatusMessage.ScanBarcode
                                                    currentSide = DocumentSide.Barcode
                                                }
                                            }
                                        }

                                        DocumentSide.Back -> {
                                            when (event.side) {
                                                DocumentSide.Front -> {}

                                                DocumentSide.Back -> {}

                                                DocumentSide.Barcode -> {
                                                    // TODO: add should stay in processing state
                                                    processingState = ProcessingState.Sensing
                                                    statusMessage = StatusMessage.ScanBarcode
                                                    currentSide = DocumentSide.Barcode
                                                }
                                            }
                                        }

                                        DocumentSide.Barcode -> {
                                            // impossible to reach anything else other than barcode
                                            statusMessage = StatusMessage.ScanBarcode
                                        }
                                    }

                                    if (!shouldStayInCurrentState(
                                            uiState.value.processingState,
                                            processingState
                                        )
                                    ) {
                                        _uiState.update {
                                            it.copy(
                                                processingState = processingState,
                                                statusMessage = statusMessage,
                                                currentSide = currentSide,
                                                cardAnimationState = cardAnimationState
                                            )
                                        }
                                    }


                                }

                                is ScanningUxEvent.DocumentFrameAnalysisResult -> {
                                    // Not used in this UI implementation.
                                    // Can be used for additional frame debugging.
                                }
                            }
                            lastEvent = event
                        }
                    }
                }
            )
        }
    }

    fun setInitialUiStateFromUiSettings(verifyUiSettings: VerifyUiSettings) {
        _uiState.update {
            it.copy(
                helpButtonDisplayed = verifyUiSettings.showHelpButton,
                onboardingDialogDisplayed = verifyUiSettings.showOnboardingDialog
            )
        }
        if (_uiState.value.onboardingDialogDisplayed) changeOnboardingDialogVisibility(true)
        else changeOnboardingDialogVisibility(false)
    }

    override fun analyzeImage(image: ImageProxy) {
        image.use {
            imageAnalyzer?.analyze(it)
        }
    }

    fun shouldStayInCurrentState(
        currentState: ProcessingState,
        newState: ProcessingState,
    ): Boolean {
        return if (currentState == newState) true
        else if (newState.reticleState == ReticleState.Success) false
        else if (_uiState.value.cardAnimationState != CardAnimationState.Hidden) true
        else if (currentState.reticleState == ReticleState.IndefiniteProgress && newState.reticleState == ReticleState.Error) return false
        else if (currentState.reticleState == ReticleState.Sensing && newState.reticleState == ReticleState.Error) return false
        else if ((System.nanoTime()
                .nanoseconds - uiStateStartTime) < currentState.minDuration
        ) true
        else false
    }

    fun onFlipAnimationCompleted() {
        _uiState.update {
            it.copy(
                cardAnimationState = CardAnimationState.Hidden
            )
        }
        if (!_uiState.value.onboardingDialogDisplayed && !_uiState.value.helpDisplayed) {
            imageAnalyzer?.resumeAnalysis()
            helpTooltipTimer.start()
        }
    }

    private fun updateStateStartTime() {
        uiStateStartTime =
            System.nanoTime()
                .toDuration(DurationUnit.NANOSECONDS)
    }

    fun changeTorchState() {
        when (_uiState.value.torchState) {
            MbTorchState.On -> {
                _torchOn.value = false
                _uiState.update {
                    it.copy(torchState = MbTorchState.Off)
                }
            }

            MbTorchState.Off -> {
                _torchOn.value = true
                _uiState.update {
                    it.copy(torchState = MbTorchState.On)
                }
            }

            MbTorchState.NotSupportedByCamera -> {}
        }
    }

    fun changeHelpTooltipVisibility(show: Boolean) {
        if (_uiState.value.helpButtonDisplayed) {
            _uiState.update {
                it.copy(helpTooltipDisplayed = show)
            }
        }
    }

    fun changeOnboardingDialogVisibility(show: Boolean) {
        if (show) {
            imageAnalyzer?.pauseAnalysis()
            helpTooltipTimer.cancel()
        } else {
            imageAnalyzer?.resumeAnalysis()
            helpTooltipTimer.start()
        }
        _uiState.update {
            it.copy(onboardingDialogDisplayed = show)
        }
    }

    fun changeHelpScreensVisibility(show: Boolean) {
        if (show) {
            imageAnalyzer?.pauseAnalysis()
            helpTooltipTimer.cancel()
        } else {
            imageAnalyzer?.resumeAnalysis()
            helpTooltipTimer.start()
        }
        _uiState.update {
            it.copy(helpDisplayed = show)
        }
    }

    fun onRetryTimeout() {
        helpTooltipTimer.cancel()
        _uiState.update {
            it.copy(
                unrecoverableErrorState = UnrecoverableErrorState.NoError,
                processingState = ProcessingState.ErrorDialog,
                statusMessage = StatusMessage.ScanFrontSide,
                currentSide = DocumentSide.Front
            )
        }
        imageAnalyzer?.restartAnalysis()
        helpTooltipTimer.start()
    }

    fun onReticleSuccessAnimationCompleted() {
        // TODO: update to work with barcode
        if (!isScanningDone) {
            _uiState.update {
                it.copy(
                    processingState =
                        ProcessingState.CardAnimation,
                    statusMessage = StatusMessage.FlipDocument,
                    currentSide = DocumentSide.Back,
                    cardAnimationState =
                        CardAnimationState.ShowFlipLandscape
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    processingState = ProcessingState.Success
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        imageAnalyzer?.cancel()
        imageAnalyzer?.close()
    }

    companion object {
        private const val TAG = "BlinkIdVerifyUxViewModel"

        // Define a custom key for your dependency
        val DOCUMENT_VERIFY_SDK =
            object : CreationExtras.Key<BlinkIdVerifySdk> {}
        val DOCUMENT_VERIFY_CAPTURE_SETTINGS =
            object : CreationExtras.Key<CaptureSessionSettings> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                BlinkIdVerifyUxViewModel(
                    this[DOCUMENT_VERIFY_SDK] as BlinkIdVerifySdk,
                    this[DOCUMENT_VERIFY_CAPTURE_SETTINGS] as CaptureSessionSettings
                )
            }
        }
    }
}