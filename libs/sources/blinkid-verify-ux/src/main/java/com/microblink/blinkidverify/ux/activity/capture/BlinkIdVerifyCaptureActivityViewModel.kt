package com.microblink.blinkidverify.ux.activity.capture

import android.content.Context
import androidx.lifecycle.ViewModel
import com.microblink.blinkidverify.core.BlinkIdVerifySdk
import com.microblink.blinkidverify.core.BlinkIdVerifySdkSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BlinkIdVerifyCaptureActivityViewModel : ViewModel() {

    private val _displayLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var displayLoading = _displayLoading.asStateFlow()

    var localSdk: BlinkIdVerifySdk? = null
        private set

    suspend fun initializeLocalSdk(
        context: Context,
        blinkIdVerifySdkSettings: BlinkIdVerifySdkSettings,
        onInitFailed: () -> Unit
    ) {
        _displayLoading.update {
            true
        }

        val maybeInstance = BlinkIdVerifySdk.initializeSdk(
            context,
            blinkIdVerifySdkSettings
        )
        when {
            maybeInstance.isSuccess -> {
                localSdk = maybeInstance.getOrNull()
                _displayLoading.update {
                    false
                }
            }

            maybeInstance.isFailure -> {
                onInitFailed()
            }
        }
    }

    fun unloadSdk() {
        try {
            localSdk?.close()
        } catch (_: Exception) {
        }
        localSdk = null
    }

    fun unloadSdkAndDeleteCachedAssets() {
        try {
            localSdk?.closeAndDeleteCachedAssets()
        } catch (_: Exception) {
        }
        localSdk = null
    }

}