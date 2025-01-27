/*
 * Copyright (c) 2024 Microblink Ltd. All rights reserved.
 *
 * ANY UNAUTHORIZED USE OR SALE, DUPLICATION, OR DISTRIBUTION
 * OF THIS PROGRAM OR ANY OF ITS PARTS, IN SOURCE OR BINARY FORMS,
 * WITH OR WITHOUT MODIFICATION, WITH THE PURPOSE OF ACQUIRING
 * UNLAWFUL MATERIAL OR ANY OTHER BENEFIT IS PROHIBITED!
 * THIS PROGRAM IS PROTECTED BY COPYRIGHT LAWS AND YOU MAY NOT
 * REVERSE ENGINEER, DECOMPILE, OR DISASSEMBLE IT.
 */

package com.microblink.blinkidverify.ux.capture.camera

enum class Resolution(val width: Int, val height: Int) {
    RESOLUTION_720p(1280, 720),
    RESOLUTION_1080p(1920, 1080),
    RESOLUTION_2160p(3840, 2160),
    RESOLUTION_4320p(7680, 4320);
}

enum class CameraLensFacing {
    LENS_FACING_BACK,
    LENS_FACING_FRONT
}

data class CameraSettings(
    val lensFacing: CameraLensFacing = CameraLensFacing.LENS_FACING_BACK,
    val desiredResolution: Resolution = Resolution.RESOLUTION_2160p
)