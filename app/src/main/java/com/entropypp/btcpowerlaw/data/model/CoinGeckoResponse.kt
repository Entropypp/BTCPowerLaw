package com.entropypp.btcpowerlaw.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Represent the response from the CoinGecko /coins/markets endpoint.
 * This is a list response, but we only care about the first item (Bitcoin).
 */
@JsonClass(generateAdapter = true)
data class MarketData(
    @Json(name = "id") val id: String,
    @Json(name = "symbol") val symbol: String,
    @Json(name = "current_price") val currentPrice: Double,
    @Json(name = "ath") val ath: Double
)

