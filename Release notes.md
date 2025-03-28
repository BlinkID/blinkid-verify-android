# Release notes

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