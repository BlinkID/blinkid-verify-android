<p align="center" >
  <img src="https://raw.githubusercontent.com/wiki/blinkid/blinkid-android/images/logo-microblink.png" alt="Microblink" title="Microblink">
</p>

# BlinkID Verify SDK for Android

The _BlinkID Verify_ Android SDK is a comprehensive solution for implementing secure document scanning and verification on Android. It offers powerful capabilities for capturing, analyzing, and verifying a wide range of identification documents.


# Table of contents (TODO)
* [Quick Start](#quick-start)
    * [Quick start with the sample app](#quick-sample)
    * [SDK integration](#sdk-integration)
* [Device requirements](#device-requirements)
    * [Android version](#android-version-req)
    * [Camera](#camera-req)
    * [Processor architecture](#processor-arch-req)
* [Pre-bundling the SDK resources in your app](#pre-bundling-resources)
* [Customizing the look and UX](#customizing-the-look)
    * [Simple customizations](#simple-customizations)
    * [Advanced customizations](#advanced-customizations)
* [Changing default strings and localization](#changing-strings-and-localization)
    * [Defining your own string resources for UI elements](#using-own-string-resources)
* [Completely custom UX (advanced)](#low-level-api)
    * [The `BlinkIDVerifySdk` and `CaptureSession`](#core-api-sdk-and-session)
* [Troubleshooting](#troubleshoot)
* [Additional info](#additional-info)
    * [BlinkID Verify SDK size](#sdk-size)
    * [API documentation](#api-documentation)
    * [Contact](#contact)


# <a name="quick-start"></a> Quick Start

## <a name="quick-sample"></a> Quick start with the sample apps

1. Open Android Studio.
2. In `Quick Start` dialog choose _Open project_.
3. In `File` dialog select _BlinkIDVerify_ folder.
4. Wait for the project to load. If Android Studio asks you to reload the project on startup, select `Yes`.

#### Included sample apps:

- **_app_** demonstrates quick and straightforward integration of the BlinkID Verify SDK using the provided UX in Jetpack Compose to verify a document and display the results.


## <a name="sdk-integration"></a> SDK integration

### Adding _BlinkID Verify_ SDK dependency

The `BlinkID Verify` library is available on Microblink maven repository.

In your project root add _Microblink_ maven repository to `repositories` list:

```
repositories {
    // ... other repositories
    maven {
        url = uri("https://maven.microblink.com")
    }
}
```

Add _BlinkID Verify_ as a dependency in module level `build.gradle(.kts)`:

```
dependencies {
    implementation("com.microblink:blinkid-verify-ux:3.8.0")
}
```

### Launching the document capture session and obtaining the results

1. A valid license key is required to initialize the document capture process. You can request a free trial license key, after you register, at [Microblink Developer Hub](https://account.microblink.com/signin). License is bound to the [application ID](https://developer.android.com/studio/build/configure-app-module#set-application-id) of your app, so please ensure you enter the correct application ID when asked.


2. You first need to initialize the SDK and obtain the `BlinkIDVerifySdk` instance:
```kotlin
val maybeInstance = BlinkIDVerifySdk.initializeSdk(
    BlinkIDVerifySdkSettings(
        context = context,
        licenseKey = <your_license_key>,
    )
)
when {
    maybeInstance.isSuccess -> {
        val sdkInstance = maybeInstance.getOrNull()
        // use the SDK instance
    }

    maybeInstance.isFailure -> {
        val exception = maybeInstance.exceptionOrNull()
        Log.e(TAG, "Initialization failed", exception)
    }
}
```
`BlinkIDVerifySdk.initializeSdk` is a suspend function which should be called from a coroutine.

3. Use `CameraScanningScreen` composable to launch document capture UX and obtain results:
```kotlin
CameraScanningScreen(
    sdkInstance,
    verifyUiSettings = VerifyUiSettings(),
    captureSessionSettings = CaptureSessionSettings(),
    onCaptureSuccess = { captureResult ->
        // captureResult is BlinkIDVerifyCaptureResult
    },
    onCaptureCanceled = {
        // user canceled the capture
    }
)
```

### Document capture session result

After the document capture session is finished the SDK returns an object of type [BlinkIDVerifyCaptureResult](https://blinkid.github.io/blinkid-verify-android/blinkid-verify-core/com.microblink.blinkidverify.core.data.model.result/-blink-i-d-verify-capture-result/index.html). 
The object contains images of the front and back sides of the document. Additionally, if the barcode is present on the document, the camera frame containing a visible barcode will also be available.

`BlinkIDVerifyCaptureResult.toBlinkIDVerifyRequest` helper method should be used to prepare `BlinkIDVerifyRequest` for the verification API call described in the following section.

### Launching the document verification API call and obtaining the results

1. You need to create a `BlinkIDVerifyRequest` by using `BlinkIDVerifyCaptureResult`:
```kotlin
val blinkIDVerifyRequest = captureResult.toBlinkIDVerifyRequest(
    BlinkIDVerifyProcessingRequestOptions(),
    BlinkIDVerifyProcessingUseCase()
)
```
Ensure that the `CaptureSessionSettings` used for capturing document images match the settings used for `BlinkIDVerifyRequest`.

2. You also need to create a [BlinkIDVerifyClient](https://blinkid.github.io/blinkid-verify-android/blinkid-verify-core/com.microblink.blinkidverify.core/-blink-i-d-verify-client/index.html) for the document verification service providing your API token.
```kotlin
val client = BlinkIDVerifyClient(
    BlinkIDVerifyServiceSettings(
        // if using self hosted solution, set appropriate base URL
        verificationServiceBaseUrl = "https://usc1.verify.microblink.com/api/v2/docver",
        token = "your_API_token",
    )
)
```
If you don't already have the API token, contact us at [help.microblink.com](https://help.microblink.com/).

3. Finally use `val response = client.verify(blinkIDVerifyRequest)` to send the request and fetch the response that will contain either an error reason or the results of the verification process:
```kotlin
CoroutineScope(IO).launch {
    when (val response = client.verify(blinkIDVerifyRequest)) {
        is Response.Error -> {
            // check `response.errorReason` to check why the request failed
        }
        is Response.Success -> {
            // check `response.endpointResponse` for final result
        }
    }
}
```

### Document verification results

The final result from the document verification service is of type [BlinkIDVerifyEndpointResponse](https://blinkid.github.io/blinkid-verify-android/blinkid-verify-core/com.microblink.blinkidverify.core.data.model.result/-blink-i-d-verify-endpoint-response/index.html) and it contains both extraction and verification results.


# <a name="device-requirements"></a> Device requirements

## <a name="android-version-req"></a> Android version

_BlinkID Verify_ SDK requires Android API level **24** or newer.

## <a name="camera-req"></a> Camera

To perform successful scans, the camera preview resolution must be at least **1080p**. Note that the camera preview resolution is not the same as the video recording resolution.

## <a name="processor-arch-req"></a> Processor architecture

_BlinkID Verify_ SDK is distributed with **ARMv7** and **ARM64** native library binaries.

_BlinkID Verify_ is a native library written in C++ and available for multiple platforms. Because of this, _BlinkID Verify_ cannot work on devices with obscure hardware architectures. We have compiled SDK's native code only for the most popular Android [ABIs](https://en.wikipedia.org/wiki/Application_binary_interface).

If you are combining _BlinkID Verify_ library with other libraries that contain native code in your application, make sure to match the architectures of all native libraries. For example, if the third-party library has only ARMv7 version, you must use exactly ARMv7 version of _BlinkID Verify_ with that library, but not ARM64. Using different architectures will crash your app at the initialization step because JVM will try to load all its native dependencies in the same preferred architecture and fail with `UnsatisfiedLinkError`.

To avoid this issue and ensure that only architectures supported by the _BlinkID Verify_ library are packaged in the final application, add the following statement to your `android/defaultConfig` block inside `build.gradle.kts`:

```
android {
    ...
    defaultConfig {
        ...
        ndk {
            // Tells Gradle to package the following ABIs into your application
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
    }
}
```

# <a name="pre-bundling-resources"></a> Pre-bundling the SDK resources into your app

If you want to reduce the SDK startup time and network traffic, you have option to pre-bundle the SDK resources as assets into your application. All required resources are located in [libs/resources/assets/microblink/blinkidverify](https://github.com/BlinkID/blinkid-verify-android/tree/main/libs/resources/assets/microblink/blinkidverify) folder. You can bundle it to your application by including the mentioned folder to application's assets. Copy mentioned `libs/resources/assets/microblink` directory to `src/main/assets` folder of your application module (or appropriate folder for desired app flavor).

Use `BlinkIDVerifySdkSettings` to set the following options when instantiating the SDK:

```kotlin
BlinkIDVerifySdkSettings(
    context = context,
    licenseKey = "your_license_key",
    // disable resource download
    downloadResources = false,
    // define path if you are not using a default one: "microblink/blinkidverify"
    // resourceLocalFolder = "path_within_app_assets"
)
```



# <a name="customizing-the-look"></a> Customizing the look and the UX

## <a name="simple-customizations"></a> Simple customizations

You can use basic customization options in our default `CameraScanningScreen` composable:

```kotlin
CameraScanningScreen(
    sdkInstance,
    // ui settings options
    verifyUiSettings = VerifyUiSettings(
        typography = yourTypography,
        colorScheme = yourColorScheme,
        reticleColors = youReticleColors,
        verifySdkStrings = yourVerifySdkStrings,
        showOnboardingDialog = true, // or false
        showHelpButton = true // or false
    ),
    captureSessionSettings = CaptureSessionSettings(),
    onCaptureSuccess = { captureResult ->
        // result is BlinkIDVerifyCaptureResult
    },
    onCaptureCanceled = {
        // user canceled the capture
    }
)
```

For a complete reference on available customization options, see [VerifyUiSettings](https://blinkid.github.io/blinkid-verify-android/blinkid-verify-ux/com.microblink.blinkidverify.ux/-verify-ui-settings/index.html) API docs.

## <a name="advanced-customizations"></a> Advanced customizations

### Implementing scanning Composable

It is possible to use completely custom UI elements by implementing your own Composable.

Create your implementation of scanning ViewModel (which must be a subclass of our `CameraViewModel`) to handle UX events that come from our SDK:

```kotlin
class YourBlinkIDVerifyViewModel(
    blinkIDVerifySdkInstance: BlinkIDVerifySdk,
    captureSessionSettings: CaptureSessionSettings
) : CameraViewModel() {

    val imageAnalyzer = BlinkIDVerifyAnalyzer(
        verifySdk = blinkIDVerifySdkInstance,
        captureSessionSettings = captureSessionSettings,
        scanningDoneHandler = object : ScanningDoneHandler {
            override fun onScanningFinished(result: BlinkIDVerifyCaptureResult) {
                // TODO use capture result
            }

            override fun onScanningCancelled() {
                // user cancelled the scanning
            }
        },
        uxEventHandler = object : ScanningUXEventHandler {
            override fun onUXEvents(events: List<ScanningUXEvent>) {
                // handle scanning UX events to update UI state
                for (event in events) {
                    when (event) {
                        is ScanningUXEvent.ScanningDone -> {
                            // TODO
                        }

                        is ScanningUXEvent.DocumentNotFound -> {
                            // TODO
                        }

                        is ScanningUXEvent.DocumentNotFullyVisible -> {
                            // TODO
                        }

                        is ScanningUXEvent.DocumentTooClose -> {
                            // TODO
                        }
                        // TODO ... handle other events, when must be exhaustive, omitted for brevity
                    }
                }
            }
        }
    )
    
    override fun analyzeImage(image: ImageProxy) {
        // image has to be closed after processing
        image.use {
            imageAnalyzer?.analyze(it)
        }
    }

     override fun onCleared() {
        super.onCleared()
        // cancel and close image analyzer when view model is cleared
        imageAnalyzer.cancel()
        imageAnalyzer.close()
    }
}

```

Implement your camera scanning screen Composable by using our `CameraScreen` Composable which is responsible for camera management: 

```kotlin
@Composable
fun YourCameraScanningScreen(
    viewModel: YourBlinkIDVerifyViewModel
    //... other required parameters for your UI
) {
    // ...
    CameraScreen(
        cameraViewModel = viewModel,
    ) {
        // TODO your camera overlay Compose content
    }

}
``` 

### Modifying our `blinkid-verify-ux` library source code

For larger control over the UX, you can use the open-source `blinkid-verify-ux` library and perform certain modifications. **Only the source files that specifically allow for modification by the license header** can be modified.

To do so, you can include the source code of our library directly in your application.
It is located in `libs/sources/blinkid-verify-ux` module.

**Please keep in mind that we will regularly make changes and update the source code with each release.**

# <a name="changing-strings-and-localization"></a> Changing default strings and localization

You can modify strings and add another language. For more information on how localization works in Android, check out the [official Android documentation](https://developer.android.com/guide/topics/resources/localization).

## <a name="using-own-string-resources"></a> Defining your own string resources for UI elements

You can define string resources that will be used instead of predefined ones by using the custom [VerifySdkStrings](https://blinkid.github.io/blinkid-verify-android/blinkid-verify-ux/com.microblink.blinkidverify.ux.theme/-verify-sdk-strings/index.html) while creating the `VerifyUiSettings`.


# <a name="low-level-api"></a> Completely custom UX (advanced)

When using the low-level API, you are responsible for preparing the input image stream (or static images) for analysis as well as building a completely custom UX from scratch based on the image-by-image feedback from the SDK. 

Low-level API gives you more flexibility with the cost of a significantly larger integration effort. For example, if you need a camera, you will be responsible for camera management and displaying real-time user guidance.

### Adding _BlinkID Verify_ Core SDK dependency for low-level API

For low-level API integration, only _BlinkID Verify_ SDK core library: **blinkid-verify-core** is needed. The `blinkid-verify-ux is` not needed.

In your project root, add _Microblink_ maven repository to the repositories list:

```
repositories {
    // ... other repositories
    maven {
        url = uri("https://maven.microblink.com")
    }
}
```

Add _blinkid-verify-core_ library as a dependency in module level `build.gradle(.kts)`:

```
dependencies {
    implementation("com.microblink:blinkid-verify-core:3.8.0")
}
```

## <a name="core-api-sdk-and-session"></a> The `BlinkIDVerifySdk` and `CaptureSession`

[BlinkIDVerifySdk](https://blinkid.github.io/blinkid-verify-android/blinkid-verify-core/com.microblink.blinkidverify.core/-blink-i-d-verify/index.html) is a singleton that is main entry point to the _BlinkID Vreify_ SDK. It manages the global state of the SDK. This involves managing the main processing, unlocking the SDK, ensuring that licence check is up-to-date, downloading resources, and performing all necessary synchronization for the processing operations.

Once you obtain an instance of the `BlinkIDVerifySdk` class after the SDK initialization is completed, you can use it to start a document capture session. 

[CaptureSession](https://blinkid.github.io/blinkid-verify-android/blinkid-verify-core/com.microblink.blinkidverify.core.capture.session/-capture-session/index.html) is the main object that accepts images and camera frames, processes them and returns frame-by-frame results, and final result when it becomes available.


### <a name="analyzing-image-stream"></a> Analyzing the stream of images

1. First initialize the SDK to obtain `BlinkIDVerifySdk` instance by calling `BlinkIDVerifySdk.initializeSdk` suspend function from a Coroutine:
```kotlin
val maybeInstance = BlinkIDVerifySdk.initializeSdk(
    BlinkIDVerifySdkSettings(
        context = context,
        licenseKey = <your_license_key>,
    )
)
when {
    maybeInstance.isSuccess -> {
        val sdkInstance = maybeInstance.getOrNull()
        // use the SDK instance
    }

    maybeInstance.isFailure -> {
        val exception = maybeInstance.exceptionOrNull()
        Log.e(TAG, "Initialization failed", exception)
    }
}
```
2. Create `CaptureSession` by calling suspend function `BlinkIDVerifySdk.createScanningSession(CaptureSessionSettings)`
```kotlin
val captureSession = blinkIDVerifySdk.createScanningSession(CaptureSessionSettings(
    // use CapturePolicy.Video to analyze stream of images, if you have few 
    // images (e.g. from gallery) use CapturePolicy.Photo
    capturePolicy = CapturePolicy.Video,
    // update other options if required
))
```

3. To process each image (camera frame) that comes to the recognition, call the suspend function `CaptureSession.process(InputImage): ProcessResult`
```kotlin
val processResult = captureSession.process(inputImage)
```

There are helper methods for creating [InputImage](https://blinkid.github.io/blinkid-verify-android/blinkid-verify-core/com.microblink.blinkidverify.core.capture.session.image/-input-image/index.html) from `android.media.Image`, `androidx.camera.core.ImageProxy` and standard Android Bitmap. 

Processing of the single frame returns [ProcessResult](https://blinkid.github.io/blinkid-verify-android/blinkid-verify-core/com.microblink.blinkidverify.core.capture.session/-process-result/index.html) which contains:

- Detailed analysis of the frame, including various detection statuses and potential issues that should be used for frame-by-frame UX updates.
- Completeness status of the overall process.

You should keep calling the process function until the result completeness indicates that the result is complete, but you could have custom logic for cancellation and timeouts.

### <a name="core-api-obtaining-results"></a> Obtaining capture results

If after analysis of some image completeness status of `ProcessResult` indicates that document capture is complete, only then you should get the final result from the `CaptureSession`:

```kotlin
if (processResult.resultCompleteness.isComplete()) {
    val captureResult = session.getResult()
    // do something with the final result
}
```

You will get [BlinkIDVerifyCaptureResult](https://blinkid.github.io/blinkid-verify-android/blinkid-verify-core/com.microblink.blinkidverify.core.data.model.result/-blink-i-d-verify-capture-result/index.html) with document images.

**After scanning is completed, it is important to terminate the scanning session**

To terminate the scanning session, ensure that `ScanningSession.close()` is called.

**If you are finished with the SDK processing, terminate the SDK to free up resources** by invoking `BlinkIDVerifySdk.close()` on the SDK instance.

# <a name="troubleshoot"></a> Troubleshooting

### Integration difficulties
In case of problems with SDK integration, make sure that you have followed [integration instructions](#sdk-integration) and [device requirements](#device-requirements). If you're still having problems, please contact us at [help.microblink.com](http://help.microblink.com) describing your problem and provide the following information:

* high-resolution scan/photo of the item that you are trying to read
* information about device that you are using - we need the exact model name of the device. You can obtain that information with any app like [this one](https://play.google.com/store/apps/details?id=ru.andr7e.deviceinfohw)
* please stress that you are reporting a problem related to the Android version of _BlinkID Verify_ SDK

# <a name="additional-info"></a> Additional info

## <a name="sdk-size"></a> BlinkID Verify SDK size

We recommend that you distribute your app using [App Bundle](https://developer.android.com/platform/technology/app-bundle). This will defer APK generation to Google Play, allowing it to generate minimal APK for each specific device that downloads your app, including only required processor architecture support.


Here is the SDK size, calculated for supported ABIs:

| ABI | Download size | Install size |
| --- |:-------------:| :----------------:|
| armeabi-v7a | 3.38 MB | 5.09 MB |
| arm64-v8a | 3.51 MB | 6.09 MB |

SDK size is calculated as application size increases when _BlinkID Verify_ SDK is added, with all its dependencies included.

### <a name="sdk-size-with-bundled-resources"></a> BlinkID Verify SDK size with bundled resources

## <a name="api-documentation"></a> API documentation
You can find the BlinkID Verify SDK **KDoc** documentation [here](https://blinkid.github.io/blinkid-verify-android/index.html).

## <a name="contact"></a> Contact
For any other questions, feel free to contact us at [help.microblink.com](http://help.microblink.com).
