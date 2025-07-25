# Release notes

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