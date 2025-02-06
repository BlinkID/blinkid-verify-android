package com.microblink.blinkidverify.sample.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.microblink.blinkidverify.core.data.model.result.BlinkIdVerifyEndpointResponse
import kotlinx.serialization.json.Json

object BlinkIDVerifyCustomNavType {
    val BlinkIDVerifyResultType = object : NavType<BlinkIdVerifyEndpointResponse>(
        isNullableAllowed = true
    ) {
        override fun get(bundle: Bundle, key: String): BlinkIdVerifyEndpointResponse? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun put(bundle: Bundle, key: String, value: BlinkIdVerifyEndpointResponse) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun parseValue(value: String): BlinkIdVerifyEndpointResponse {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: BlinkIdVerifyEndpointResponse): String {
            return Uri.encode(Json.encodeToString(value))
        }
    }
}