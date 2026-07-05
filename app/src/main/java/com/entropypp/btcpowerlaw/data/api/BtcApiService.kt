package com.entropypp.btcpowerlaw.data.api

import com.entropypp.btcpowerlaw.data.model.MarketData
import com.entropypp.btcpowerlaw.data.model.FearAndGreedResponse
import com.entropypp.btcpowerlaw.data.model.RecommendedFees
import com.entropypp.btcpowerlaw.data.model.MempoolPrices
import com.entropypp.btcpowerlaw.data.model.MempoolHistoricalPriceResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApi {
    @GET("api/v3/coins/markets")
    suspend fun getMarkets(
        @Query("vs_currency") currency: String = "usd",
        @Query("ids") ids: String = "bitcoin"
    ): List<MarketData>

    @GET("api/v3/coins/bitcoin/history")
    suspend fun getHistoricalData(
        @Query("date") date: String, // DD-MM-YYYY
        @Query("localization") localization: Boolean = false
    ): com.entropypp.btcpowerlaw.data.model.HistoricalDataResponse
}

interface FearAndGreedApi {
    @GET("fng/")
    suspend fun getFearAndGreed(
        @Query("limit") limit: Int = 1,
        @Query("date") date: String? = null, // Some versions support this
        @Query("offset") offset: Int? = null // Use offset for historical data (days ago)
    ): FearAndGreedResponse
}

interface MempoolApi {
    @GET("api/v1/fees/recommended")
    suspend fun getFees(): RecommendedFees

    @GET("api/blocks/tip/height")
    suspend fun getBlockHeight(): Long

    @GET("api/block/at-timestamp/{timestamp}")
    suspend fun getBlockHeightAtTimestamp(
        @Path("timestamp") timestamp: Long
    ): Long

    @GET("api/v1/prices")
    suspend fun getCurrentPrice(): MempoolPrices

    @GET("api/v1/historical-price")
    suspend fun getHistoricalPrice(
        @Query("timestamp") timestamp: Long
    ): MempoolHistoricalPriceResponse
}

