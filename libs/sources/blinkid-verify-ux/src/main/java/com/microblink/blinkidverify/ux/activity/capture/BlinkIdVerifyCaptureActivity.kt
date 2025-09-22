package com.microblink.blinkidverify.ux.activity.capture

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.microblink.blinkidverify.ux.VerifyCameraScanningScreen
import com.microblink.blinkidverify.ux.result.contract.BlinkIdVerifyActivitySettings
import com.microblink.blinkidverify.ux.result.contract.BlinkIdVerifyCaptureResultHolder
import com.microblink.blinkidverify.ux.result.contract.MbBlinkIdVerifyCapture
import com.microblink.blinkidverify.ux.theme.BlinkIdVerifySdkTheme
import com.microblink.blinkidverify.ux.theme.DarkVerifyColorScheme
import com.microblink.blinkidverify.ux.theme.VerifyColorScheme
import com.microblink.ux.UiSettings
import com.microblink.ux.components.LoadingScreen
import com.microblink.ux.theme.LocalBaseUiColors
import com.microblink.ux.theme.LocalTheme
import com.microblink.ux.theme.UiColors
import com.microblink.ux.utils.toUiTypography
import kotlinx.coroutines.launch

internal class BlinkIdVerifyCaptureActivity : AppCompatActivity() {

    private val activityViewModel: BlinkIdVerifyCaptureActivityViewModel by viewModels()
    private lateinit var verifyActivitySettings: BlinkIdVerifyActivitySettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyActivitySettings = BlinkIdVerifyActivitySettings.loadFromIntent(intent)
        if (verifyActivitySettings.enableEdgeToEdge) enableEdgeToEdge()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onCancel(MbBlinkIdVerifyCapture.CancelReason.UserRequested)
            }
        })

        activityViewModel.viewModelScope.launch {
            activityViewModel.initializeLocalSdk(
                context = this@BlinkIdVerifyCaptureActivity,
                blinkIdVerifySdkSettings = verifyActivitySettings.blinkIdVerifySdkSettings,
                onInitFailed = {
                    onCancel(MbBlinkIdVerifyCapture.CancelReason.ErrorLicenseCheck)
                }
            )
        }

        setContent {

            val verifyUiSettings = createUiSettings(verifyActivitySettings)

            BlinkIdVerifySdkTheme(verifyUiSettings) {
                val displayLoading = activityViewModel.displayLoading.collectAsStateWithLifecycle()
                if (displayLoading.value) {
                    LoadingScreen()
                } else {
                    activityViewModel.localSdk?.let {
                        VerifyCameraScanningScreen(
                            blinkIdVerifySdk = it,
                            uxSettings = verifyActivitySettings.uxSettings,
                            uiSettings = verifyUiSettings,
                            cameraSettings = verifyActivitySettings.cameraSettings,
                            captureSessionSettings = verifyActivitySettings.captureSessionSettings,
                            onCaptureSuccess = { result ->
                                BlinkIdVerifyCaptureResultHolder.blinkIdVerifyCaptureResult = result
                                this.setResult(RESULT_OK)
                                this.finish()
                            },
                            onCaptureCanceled = {
                                onCancel(MbBlinkIdVerifyCapture.CancelReason.UserRequested)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        if (verifyActivitySettings.deleteCachedAssetsAfterUse) {
            activityViewModel.unloadSdkAndDeleteCachedAssets()
        } else {
            activityViewModel.unloadSdk()
        }
        super.onDestroy()
    }

    fun onCancel(cancelReason: MbBlinkIdVerifyCapture.CancelReason) {
        val extras = Intent()
        extras.putExtra(MbBlinkIdVerifyCapture.EXTRA_CANCEL_REASON, cancelReason)
        this.setResult(RESULT_CANCELED, extras)
        this.finish()
    }

    @Composable
    fun createUiSettings(verifyActivitySettings: BlinkIdVerifyActivitySettings): UiSettings {
        val primaryColor =
            if (verifyActivitySettings.verifyActivityUiColors?.primary != null) Color(
                verifyActivitySettings.verifyActivityUiColors.primary
            ) else if (isSystemInDarkTheme()) DarkVerifyColorScheme.primary else VerifyColorScheme.primary
        val backgroundColor =
            if (verifyActivitySettings.verifyActivityUiColors?.background != null) Color(
                verifyActivitySettings.verifyActivityUiColors.background
            ) else if (isSystemInDarkTheme()) DarkVerifyColorScheme.background else VerifyColorScheme.background
        val onBackgroundColor =
            if (verifyActivitySettings.verifyActivityUiColors?.onBackground != null) Color(
                verifyActivitySettings.verifyActivityUiColors.onBackground
            ) else if (isSystemInDarkTheme()) DarkVerifyColorScheme.onBackground else VerifyColorScheme.onBackground

        val verifyColorScheme = LocalTheme.current.copy(
            primary = primaryColor,
            background = backgroundColor,
            onBackground = onBackgroundColor
        )

        val helpButtonColor =
            if (verifyActivitySettings.verifyActivityUiColors?.helpButton != null) Color(
                verifyActivitySettings.verifyActivityUiColors.helpButton
            ) else if (isSystemInDarkTheme()) UiColors.DefaultDark.helpButton else UiColors.Default.helpButton
        val helpButtonBackgroundColor =
            if (verifyActivitySettings.verifyActivityUiColors?.helpButtonBackground != null) Color(
                verifyActivitySettings.verifyActivityUiColors.helpButtonBackground
            ) else if (isSystemInDarkTheme()) UiColors.DefaultDark.helpButtonBackground else UiColors.Default.helpButtonBackground
        val helpTooltipTextColor =
            if (verifyActivitySettings.verifyActivityUiColors?.helpTooltipText != null) Color(
                verifyActivitySettings.verifyActivityUiColors.helpTooltipText
            ) else if (isSystemInDarkTheme()) UiColors.DefaultDark.helpTooltipText else UiColors.Default.helpTooltipText
        val helpTooltipBackgroundColor =
            if (verifyActivitySettings.verifyActivityUiColors?.helpTooltipBackground != null) Color(
                verifyActivitySettings.verifyActivityUiColors.helpTooltipBackground
            ) else if (isSystemInDarkTheme()) UiColors.DefaultDark.helpTooltipBackground else UiColors.Default.helpTooltipBackground

        val verifyUiColors = LocalBaseUiColors.current.copy(
            helpButton = helpButtonColor,
            helpButtonBackground = helpButtonBackgroundColor,
            helpTooltipText = helpTooltipTextColor,
            helpTooltipBackground = helpTooltipBackgroundColor
        )

        val uiTypography = verifyActivitySettings.verifyActivityTypography.toUiTypography()

        return UiSettings(
            typography = uiTypography,
            colorScheme = verifyColorScheme,
            uiColors = verifyUiColors,
            sdkStrings = verifyActivitySettings.verifyActivityUiStrings,
            showOnboardingDialog = verifyActivitySettings.showOnboardingDialog,
            showHelpButton = verifyActivitySettings.showHelpButton
        )
    }
}