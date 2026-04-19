package com.entropypp.btcpowerlaw.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HistoricalDataResponse(
    @Json(name = "id") val id: String,
    @Json(name = "symbol") val symbol: String,
    @Json(name = "market_data") val marketData: HistoricalMarketData?
)

@JsonClass(generateAdapter = true)
data class HistoricalMarketData(
    @Json(name = "current_price") val currentPrice: Map<String, Double>?
)
