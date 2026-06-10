# Release notes

## v3.21.0

### What's New
- Update to BlinkID v7.8 for document capturing and extraction.

## v3.20.0

### What's New
- Update to BlinkID v7.7.1 for document capturing and extraction.

### New features
- Added a "document not supported" alert - when an unsupported document is detected during scanning, the user is now clearly notified via an alert.
- Passport-specific scanning  
  - Added custom passport animations - users are now guided with tailored animations when scanning passports, improving the scanning experience for passport documents.
  - Added `scanUnsupportedBack` and `scanPassportDataPageOnly` options to `BlinkIdVerifyScanningSettings`, allowing control over whether the back side of unsupported documents is scanned and whether only the passport data page (MRZ page) is required.
- Added new and improved analytics pipeline.

### API changes
- Session API renamed:
    - `VerifyCaptureSession` to `BlinkIdVerifyScanningSession`
    - `VerifyCaptureSessionSettings` to `BlinkIdVerifySessionSettings`
    - `VerifyProcessResult` to `BlinkIdVerifyProcessResult`
    - `ProcessResult` has been removed; use `BlinkIdVerifyProcessResult`
- Added new property `generativeAiMatchLevel` in `BlinkIdVerifyProcessingRequestOptions`.
- Added new properties to `ExtractionImageAnalysisResult`:
  - `documentClassInfo`
  - `documentOrientation`
  - `documentRotation`
- UX event types renamed:
    - `DocumentFrameAnalysisResult` to `VerifyDocumentImageAnalysisResult` (now wraps `ExtractionImageAnalysisResult` instead of `FrameAnalysisResult`).
    - `DocumentLocatedLocation` to `BlinkIdVerifyDocumentLocatedLocation` (now uses `Quadrilateral` instead of `DocumentLocation`).
- `dispatchVerifyEvents` extension function on `ScanningUxEventHandler` has been removed. Use the updated `VerifyUxTranslator` directly.
- `VerifyProcessingStatus.None` has been renamed to `VerifyProcessingStatus.Unknown`.
- Scanning settings restructured: Session settings are now passed via `BlinkIdVerifySessionSettings` with nested `BlinkIdVerifyScanningSettings`, which consolidates image quality, match levels, and use-case options that were previously spread across multiple configuration objects.
- `CheckResult` and `VerificationSideMode` classes have been removed.
- Updated `CaptureConditions` to include values `NoControl`, `Basic`, `Hybrid`.
- Added convenience method in `BlinkIdVerifyCaptureResult` `toBlinkIdVerifyRequest()` that automatically derives shared on-device and backend options from the session settings, reducing the risk of mismatched configuration.
- `DocumentAlreadyScannedException` renamed to `ScanAlreadyCompletedException`.

The following changes will impact your implementation only if you have ***advanced SDK customizations*** and don’t use the default `Activity` or `Composable`:
- `DocumentSide`(`Front`, `Back`, `Barcode`) renamed to `UiScanningSide` (`First`, `Second`, `Barcode`).
- `CommonStatusMessage` changed its members (to remove `Document`, `Front`, and `Back` occurrences):
  - `ScanFrontSide` to `ScanFirstSide`
  - `ScanBackSide` to `ScanSecondSide`
  - `FlipDocument` to `Flip`
  - `KeepDocumentVisible` to `KeepVisible`
  - `AlignDocument` to `Align`
  - `ScanBarcode`, `RotateDocument`, `RotateDocumentShort`, `KeepFacePhotoVisible`, `IncreaseLightingIntensity`, `DecreaseLightingIntensity`, `EliminateGlare` and `FilterSpecificMessage`  have been moved from `CommonStatusMessage` to `BlinkIdStatusMessage`
- Composable `HelpScreens` is implemented through a list of `HelpScreenPage` data classes.
- Composable `OnboardingDialog` is implemented through a single `HelpScreenPage` instance.
- Error dialogs for the default UI are now defined in the `ComposableUtils.kt` file.
- `MbBlinkIdVerifyCapture` now uses `ScanActivityColors`, `ScanActivitySettings`, `ScanActivityResultStatus`(`Scanned`, `Canceled`, `ErrorSdkInit`).
- Changed members of `ScanningUxEvent`:
  - `RequestDocumentSide(DocumentSide)` to `RequestSide(UiScanningSide)`
- Added `LightColorScheme` and `DarkColorScheme`.
- Removed `DocumentDrawable` (composable used for `FlipAnimation`) - animations now directly use a `Drawable`.
- `DocumentFlipAnimation` replaced with `FlipAnimation`.

### Improvements and bug fixes
- SDK and session lifecycle management improvements and bug fixes.
- Removed `MoveDocumentFromEdge` scanning UI message.
- Introduced `MbLog` for structured SDK logging, replacing direct Android Log calls. Integrators can set the log level via SDK settings.
- Compile and target SDK set to API level 36.
- UI localization coverage has been expanded with additional languages.

## v3.14.1

### Improvements
- Added the option to use the front-facing camera for scanning. You can select front-facing camera through `CameraSettings` in a scanning screen Composable function or in scan activity settings.

## v3.14.0

### What's New
- Update to BlinkID v7.4 for document capturing and extraction
- Improved document coverage globally with new document version support and new document types
- Improved data extraction accuracy for Quebec and Ontario Healthcare cards

### UI/UX Updates
- Complete scanning instruction messages revamp - the scanning session is now more stable and cleaner, which ensures a better scanning experience
- Added Typography customization to `BlinkIdVerifyCaptureActivity` through `BlinkIdVerifyActivitySettings`
- Added "Demo" overlay for the demo licenses (non-production)
- Added "Powered by Microblink" overlay option for licenses with this enabled
- Added new accessibility features
- Added haptic feedback during the scanning session (setting `allowHapticFeedback`)
- Added a separate timeout timer for the Barcode step
- Updated help screens with new illustrations
- Updated "Need help?" tooltip triggers
- Updated translations for Croatian language

### Bugfixes
- Fixed document number extraction from Canada/Nunavut barcodes
- Fixed data match overall result
- Replaced caching directory for storing downloaded resource files - avoid rare crashes on specific devices

### Other API Changes
- Added additional functions for better interoperability with Java to following classes: `BlinkIdVerifySdkSettings`, `BlinkIdSdkSettings`, `BlinkIdVerifyActivitySettings`, `BlinkIdScanActivitySettings`, `VerifyUxSettings`, `BlinkIdUxSettings`, `SdkStrings`, `BlinkIdSdkStrings`, `ParcelableTextStyle`, `ParcelableFont`
- Renamed class `StatusMessage` to `CommonStatusMessage`
- Renamed `Product` enum to `MbProduct`
- `dependentsInfo` in `VizResult` is now nullable

## v3.9.0

### BlinkID integration
*BlinkID SDK* is now fully integrated into BlinkID Verify SDK.
All BlinkID-specific functionalities, like document extraction, may now be used in a session completely independent of the Verify session.
There is no need to declare BlinkID dependencies as all of the files are automatically included.

### API changes:
* added `BlinkIdVerifyCapture` contract that enables running the document capture process through an independent activity
* added UI localization for 22 additional languages
* added additional `ErrorReason` for response codes
* renamed many classes to ensure compatibility with BlinkID SDK:
    - `FieldType` to `VerifyFieldType`
    - `ProcessResult` to `VerifyProcessResult`
    - `StringResult` to `VerifyStringResult`
    - `DateResult` to `VerifyDateResult`
    - `VerificationRequest` to `BlinkIdVerifyRequest`
    - `DocumentVerificationEndpointResponse` to `BlinkIdVerifyEndpointResponse`
    - `CaptureSession` to `VerifyCaptureSession`
    - `CaptureSessionSettings` to `VerifyCaptureSessionSettings`
    - `ProcessingStatus` to `VerifyProcessingStatus`
    - `ClassInfo` to `DocumentClassInfo`
    - `UnrecoverableErrorState` to `ErrorState`
    - All enums are now in camel-case
* renamed `stepTimeoutDurationMs` to `stepTimeoutDuration` and moved it to `VerifyUxSettings` class
* numerous other fixes and improvements

## v3.8.0

BlinkID Verify SDK is a document-capturing SDK that performs image quality checks fully in line with the BlinkID Verify API.

By using this SDK along with BlinkID Verify API, you can expect the best possible document-capturing success rate and successful verification process without image quality-based rejections.

### Key features:

* image quality estimation in real-time
* guided document capturing with user feedback
* full alignment with BlinkID Verify API
* client for sending requests to the BlinkID Verify API