package com.entropypp.btcpowerlaw.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecommendedFees(
    @Json(name = "fastestFee") val fastestFee: Int,
    @Json(name = "halfHourFee") val halfHourFee: Int,
    @Json(name = "hourFee") val hourFee: Int,
    @Json(name = "economyFee") val economyFee: Int,
    @Json(name = "minimumFee") val minimumFee: Int
)

@JsonClass(generateAdapter = true)
data class MempoolPrices(
    @Json(name = "USD") val usd: Double,
    @Json(name = "EUR") val eur: Double? = null
)

@JsonClass(generateAdapter = true)
data class MempoolHistoricalPriceResponse(
    @Json(name = "prices") val prices: List<MempoolPricePoint>
)

@JsonClass(generateAdapter = true)
data class MempoolPricePoint(
    @Json(name = "time") val time: Long,
    @Json(name = "USD") val usd: Double
)

