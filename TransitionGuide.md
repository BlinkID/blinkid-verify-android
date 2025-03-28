# Transition Guide: BlinkID to BlinkID Verify SDK

This guide will help you migrate your application from BlinkID to the new BlinkID Verify SDK. The BlinkID Verify SDK provides a modernized approach to document scanning and verification with improved architecture and Jetpack Compose support.

## Key differences

### 1. Architecture changes

- **New Core Components**: Instead of Recognizer-based architecture architecture, BlinkID Verify uses a streamlined CaptureSession-based approach
- **Modern Kotlin Features**: Written fully in Kotlin, the code is simple and easy to work with 
- **Jetpack Compose**: Jetpack Compose is the main driver for the UI through blinkid-verify-ux package
- **Simplified Flow**: More straightforward API with clearer separation of concerns

### 2. Integration methods

#### BlinkID (Old):
```kotlin
1. Maven (maven.microblink.com)
2. Manual Integration (through .aar)
```

#### BlinkID Verify (New):
```kotlin
1. MavenCentral
2. Manual Integration (through .aar)
3. Custom integration (source-available UX module allows forking and customizations)
```

## Migration guide

### 1. Update dependencies

```kts
// Add Maven Central
mavenCentral()
// to repositories declaration in your gradle files
```

#### Remove old dependencies:
```kts
// remove
implementation(com.microblink:blinkid)
maven { url 'https://maven.microblink.com' }
// from build.gradle.kts

// or
microblink-blinkid = { module = "com.microblink:blinkid", version.ref = "microblinkBlinkId" }
// from libs.versions.toml

// or through .aar file
```

#### Add new dependencies:
```kotlin
// for the base BlinkID Verify SDK version, add
implementation(com.microblink:blinkid-verify-core)

// for the version that includes the scanning UX, add
implementation(com.microblink:blinkid-verify-ux)
// to build.gradle.kts
// NOTE: blinkid-verify-ux depends on blinkid-verify-core, so there is no need to include both 

// alternatively, use libs.versions.toml
blinkid-verify-ux = { group = "com.microblink", name = "blinkid-verify-core", version.ref = "blinkidVerifySdkVersion" }
blinkid-verify-ux = { group = "com.microblink", name = "blinkid-verify-ux", versions.ref = "blinkidVerifySdkVersion" }

// or through .aar file
```

### 2. Update Import Statements

#### Old:
```kotlin
import com.microblink.blinkid.*
```

#### New:
```kotlin
import com.microblink.blinkidverify.core*
// if using the UX components
import com.microblink.blinkidverify.ux*  
```

### 3. Initialization Changes

#### Old (BlinkID):
```kotlin
// old initialization
MicroblinkSDK.setLicenseFile("license-key", context)

// creating recognizer
mRecognizer = new BlinkIdMultiSideRecognizer();
// bundle recognizers into RecognizerBundle
mRecognizerBundle = new RecognizerBundle(mRecognizer);
```

#### New (BlinkID Verify):
```kotlin
// New initialization
val instance = BlinkIdVerifySdk.initializeSdk(
    BlinkIdVerifySdkSettings(
        context = context,
        licenseKey = licenseKey
    )
)

when {
    instance.isSuccess -> {
        CameraScanningScreen(
            blinkIdVerifySdk = instance,
            verifyUiSettings = blinkIdVerifyUiSettings,
            captureSessionSettings = captureSessionSettings,
            onCaptureSuccess = { },
            onCaptureCancelled = { }
        )
    }

    maybeInstance.isFailure -> {
        val exception = instance.exceptionOrNull()
        Log.e(TAG, "Initialization failed", exception)
    }
}
```

### 4. UI Implementation Changes

#### Old (BlinkID):

Many different implementation methods exist for BlinkID, with the following being the simplest:
```kotlin
    private val resultLauncher = registerForActivityResult(TwoSideDocumentScan()) { twoSideScanResult: TwoSideScanResult ->
    when (twoSideScanResult.resultStatus) {
        ResultStatus.FINISHED -> {
            // code after a successful scan
            // use twoSideScanResult.result for fetching results, for example:
            val firstName = twoSideScanResult.result?.firstName?.value()
            Toast.makeText(this@MainActivity, "Name: $firstName", Toast.LENGTH_SHORT).show()
        }
        ResultStatus.EXCEPTION -> {
            // code after a failed scan
            Toast.makeText(this@MainActivity, "Scan failed: ${twoSideScanResult.exception?.message}", Toast.LENGTH_SHORT).show()
        }
        else -> {
            // code after a cancelled scan
            Toast.makeText(this@MainActivity, "Scan canceled!", Toast.LENGTH_SHORT).show()
        }
    }
}
```

#### New (BlinkID Verify) using Jetpack Compose:

`CameraScanningScreen` is a `@Composable` function that can be invoked when needed.
It is recommended to use on its own separate screen through `Navigation` and `ViewModel` (see Sample app).
```kotlin
    CameraScanningScreen(
        blinkIdVerifySdk = instance,
        verifyUiSettings = blinkIdVerifyUiSettings, // customize the appearance of the scanning screen (fonts, colors, strings)
        captureSessionSettings = captureSessionSettings, // define specific image quality tresholds, timeout duration, and certain check strictness
        onCaptureSuccess = { 
            // define what happens when the scanning process completes sucessfuly
        },
        onCaptureCancelled = {
            // define what happens when the scanning process is cancelled
        }
    )
}
```

#### New (BlinkID Verify) using Android Views:

Wrap the `Composable` in a `ComposeView` class:
```xml
<androidx.compose.ui.platform.ComposeView
android:id="@+id/my_composable"
android:layout_width="wrap_content"
android:layout_height="wrap_content" />
```

```kotlin
findViewById<ComposeView>(R.id.my_composable).setContent {
    MaterialTheme {
        Surface {
            CameraScanningScreen(...)
        }
    }
}
```
For additional information on using Jetpack Compose with Views, visit [official docs](https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/compose-in-views).

### 5. Result Handling

#### Old (BlinkID):

Through Activity result implementation (see 4. UI Implementation changes):
```kotlin
        ResultStatus.FINISHED -> {
    // code after a successful scan
    // use twoSideScanResult.result for fetching results, for example:
    val firstName = twoSideScanResult.result?.firstName?.value()
    Toast.makeText(this@MainActivity, "Name: $firstName", Toast.LENGTH_SHORT).show()
}
```

Or through checking recognizer state: 
```kotlin
  recognizerBundle.loadFromIntent(data)
  val result: BlinkIdSingleSideRecognizer.Result = recognizer.result
  val name = result.firstName?.value()
```

#### New (BlinkID Verify):

Implementing server request manually and fetching results on success:
```kotlin
val client = BlinkIdVerifyClient(
    BlinkIdVerifyServiceSettings(
        verificationServiceBaseUrl = "https://docver.dev.microblink.com/api/v2",
        token = "xxx"
    )
)
when (val response = client.verify(blinkIdVerifyRequest)) {
    is Response.Success -> {
        val blinkIdVerifyResult = response.endpointResponse
    }
}
```

Or using the UX module to fetch the results through `BlinkIdVerifyScanningUXViewModel`:
```kotlin
CameraScanningScreen(
    blinkIdVerifySdk = instance,
    verifyUiSettings = blinkIdVerifyUiSettings, 
    captureSessionSettings = captureSessionSettings, 
    onCaptureSuccess = { result ->
        // result is now available through the ViewModel
        viewModel.onCaptureResultAvailable(result)
        navController.popBackStack(
            route = Destination.ResultScreen, // navigate to result screen or somewhere else
            inclusive = false
        )
    },
    onCaptureCanceled = {
        navController.popBackStack(
            route = Destination.Main,
            inclusive = false
        )
    }
)
```

### 6. Custom UI Implementation

#### Old (BlinkID):

Old BlinkID offered several ways of custom UI integration through resource and UI customization. 
More info can be found here [on our GitHub page](https://github.com/BlinkID/blinkid-android?tab=readme-ov-file#built-in-ui-components).

#### New (BlinkID Verify):

New implementation offers customization in two ways:
- Through `VerifyUiSettings` class
- By forking the repository and customizing certain classes

`VerifyUiSettings` offers quick and easy customization for colors, strings, and fonts used in the SDK.
```kotlin
public data class VerifyUiSettings(
    val typography: Typography = VerifyTypography,
    val colorScheme: ColorScheme = VerifyColorScheme,
    val uiColors: UiColors? = null,
    val verifySdkStrings: VerifySdkStrings = VerifySdkStrings.Default,
    val showOnboardingDialog: Boolean = DefaultShowOnboardingDialog,
    val showHelpButton: Boolean = DefaultShowHelpButton
)
```
All of the variables have default values so default class constructor can be easily used if no changes are necessary.

```kotlin
val verifyUiSettings = VerifyUiSettings(
    typography = ..., // override if necessary
    colorScheme = ...,  // override if necessary
    uiColors = ...,  // override if necessary
    verifySdkStrings = ...,  // override if necessary
    showOnboardingDialog = ...,  // override if necessary
    showHelpButton = ...,  // override if necessary
)

fun CameraScanningScreen(
    sdkInstance,
    verifyUiSettings
    ...)
```

If these customization options are not enough, certain files can be modified.
Any class in `blinkid-verify-ux` library which has this license header is allowed to be modified.
```
/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */
```

Any modifications to classes which do not have this exact header are not allowed.

### Backend Verification Integration

BlinkID Verify SDK provides built-in support for backend verification.

  ```kotlin
  val verifyServiceSettings = BlinkIdVerifyServiceSettings(
      verificationServiceBaseUrl = "docver.microblink.com",
      token = "your-token"
  )
  
  val docVerService = BlinkIdVerifyClient(verifyServiceSettings)
  ```

## Best Practices for Migration

1. **Gradual Migration**:
    - Consider migrating feature by feature if possible
    - Test thoroughly in a development environment before production deployment

2. **Resource Management**:
    - Decide between downloaded or bundled resources early in the migration
    - Set up proper resource paths and verify resource loading

3. **UI/UX Considerations**:
    - Take advantage of Jetpack Compose if possible
    - Consider reimplementing custom UI components using the new architecture

4. **Error Handling**:
    - Update error handling to work with the new async/await pattern
    - Implement proper error handling for resource downloading if used

## Support and Resources

- For API documentation: Visit the BlinkID Verify SDK [Android API](https://blinkid.github.io/blinkid-verify-android/index.html) docs. 
- For backend verification: Check the [BlinkID Verify API](https://blinkidverify.docs.microblink.com/docs/api/request/)
- For support: Contact technical support through the support portal