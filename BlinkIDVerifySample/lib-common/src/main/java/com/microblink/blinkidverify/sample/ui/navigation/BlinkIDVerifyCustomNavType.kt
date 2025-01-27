package com.microblink.blinkidverify.sample.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.microblink.blinkidverify.core.data.model.result.BlinkIDVerifyEndpointResponse
import kotlinx.serialization.json.Json

object BlinkIDVerifyCustomNavType {
    val BlinkIDVerifyResultType = object : NavType<BlinkIDVerifyEndpointResponse>(
        isNullableAllowed = true
    ) {
        override fun get(bundle: Bundle, key: String): BlinkIDVerifyEndpointResponse? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun put(bundle: Bundle, key: String, value: BlinkIDVerifyEndpointResponse) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun parseValue(value: String): BlinkIDVerifyEndpointResponse {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: BlinkIDVerifyEndpointResponse): String {
            return Uri.encode(Json.encodeToString(value))
        }
    }
}