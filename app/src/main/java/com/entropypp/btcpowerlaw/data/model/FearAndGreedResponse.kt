package com.entropypp.btcpowerlaw.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FearAndGreedResponse(
    @Json(name = "data") val data: List<FearAndGreedData>
)

@JsonClass(generateAdapter = true)
data class FearAndGreedData(
    @Json(name = "value") val value: String,
    @Json(name = "value_classification") val valueClassification: String,
    @Json(name = "timestamp") val timestamp: String? = null
)

