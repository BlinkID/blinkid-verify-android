/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkidverify.ux.activity.capture

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microblink.blinkidverify.core.BlinkIdVerifySdk
import com.microblink.blinkidverify.core.BlinkIdVerifySdkSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlinkIdVerifyCaptureActivityViewModel : ViewModel() {

    private val _displayLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var displayLoading = _displayLoading.asStateFlow()

    var localSdk: BlinkIdVerifySdk? = null
        private set

    suspend fun initializeLocalSdk(
        context: Context,
        blinkIdVerifySdkSettings: BlinkIdVerifySdkSettings,
        onInitFailed: () -> Unit  // TODO add exception parameter
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
        val sdkToClose = localSdk
        localSdk = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sdkToClose?.close()
            } catch (_: Exception) {
            }
        }
    }
    fun unloadSdkAndDeleteCachedAssets() {
        val sdkToClose = localSdk
        localSdk = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sdkToClose?.closeAndDeleteCachedAssets()
            } catch (_: Exception) {
            }
        }
    }

}