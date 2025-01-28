package com.microblink.blinkidverify.sample.ui.result

import androidx.compose.runtime.Composable
import com.microblink.blinkidverify.core.data.model.result.BlinkIDVerifyEndpointResponse
import com.microblink.blinkidverify.sample.result.ResultFieldType
import kotlinx.serialization.json.Json

@Composable
fun VerifySampleResultScreen(
    result: BlinkIDVerifyEndpointResponse,
    onNavigateUp: () -> Unit
) {
    val results: MutableList<ResultItem> = mutableListOf()

    results.add(
        ResultItem(
            title = ResultFieldType.DocVerProcessingStatus.fieldTitleRes,
            value = result.processingStatus.name
        )
    )

    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
        prettyPrint = true
    }

    results.add(
        ResultItem(
            title = ResultFieldType.DocVerOverall.fieldTitleRes,
            value = json.encodeToString(result)
        )
    )

    SampleResultScreen(
        results = results,
        onNavigateUp = onNavigateUp
    )

}